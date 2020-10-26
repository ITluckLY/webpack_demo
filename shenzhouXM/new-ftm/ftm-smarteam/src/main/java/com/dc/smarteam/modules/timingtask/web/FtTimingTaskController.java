/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.timingtask.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.CrontabModel;
import com.dc.smarteam.cfgmodel.NodesModel;
import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.common.zk.CfgZkService;
import com.dc.smarteam.cons.GlobalCons;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.helper.RequestMsgHelper;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sys.entity.Dict;
import com.dc.smarteam.modules.sys.service.DictService;
import com.dc.smarteam.modules.timingtask.entity.FtTimingTask;
import com.dc.smarteam.service.CrontabService;
import com.dc.smarteam.service.NodesService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 定时任务Controller
 *
 * @author liwang
 * @version 2016-01-11
 */
@Controller
@RequestMapping(value = "${adminPath}/timingtask/ftTimingTask")
public class FtTimingTaskController extends BaseController {

    @Resource(name = "DictServiceImpl")
    private DictService dictService;
    @Resource
    private CrontabService crontabService;
    @Resource
    private NodesService nodesService;
    @Resource
    private CfgZkService cfgZkService;
    @Resource
    private CfgFileService cfgFileService;

    @RequiresPermissions("timingtask:ftTimingTask:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtTimingTask ftTimingTask, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ResultDto<List<CrontabModel.Task>> resultDto = crontabService.listAll();
        List<FtTimingTask> list2 = new ArrayList<FtTimingTask>();
        List<FtTimingTask> list = new ArrayList<FtTimingTask>();
        List<FtTimingTask> listEnd = new ArrayList<FtTimingTask>();

        //编号查询
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<CrontabModel.Task> tasks = resultDto.getData();
            for (CrontabModel.Task task : tasks) {
                FtTimingTask ftTimingTask2 = new FtTimingTask();
                CfgModelConverter.convertTo(task, ftTimingTask2);
                list.add(ftTimingTask2);
            }
            boolean flowId = false; //false:null   ture:not null
            boolean nodeNameTemp = false;
            if (ftTimingTask.getFlowId() != null && !"".equals(ftTimingTask.getFlowId())) {
                flowId = true;
            }
            if (ftTimingTask.getNodeNameTemp() != null && !"".equals(ftTimingTask.getNodeNameTemp())) {
                nodeNameTemp = true;
            }
            if (flowId || nodeNameTemp) {
                for (FtTimingTask ftt : list) {
                    if (flowId && !ftt.getFlowId().toLowerCase().contains(ftTimingTask.getFlowId().toLowerCase())) {
                        continue;
                    }
                    if (nodeNameTemp && !ftt.getNodeNameTemp().toLowerCase().contains(ftTimingTask.getNodeNameTemp().toLowerCase())) {
                        continue;
                    }
                    list2.add(ftt);
                }
            } else {
                list2 = list;
            }
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ResultDto<List<NodesModel.Node>> resultDto2 = nodesService.listAll(null, request);
        List<FtServiceNode> ftServiceNodeList = new ArrayList<FtServiceNode>();
        List<FtServiceNode> ftServiceNodeListEnd = new ArrayList<FtServiceNode>();
        if (ResultDtoTool.isSuccess(resultDto2)) {
            List<NodesModel.Node> nodes = resultDto2.getData();
            for (NodesModel.Node node : nodes) {
                FtServiceNode ftServiceNode2 = new FtServiceNode();
                CfgModelConverter.convertTo(node, ftServiceNode2);
                ftServiceNodeList.add(ftServiceNode2);
            }

            for (FtServiceNode fsn : ftServiceNodeList) {
                if (null != fsn.getSystemName() && !"".equals(fsn.getSystemName()) /*&& fsn.getSystemName().contains("\"text\"")*/) {
                    String systemTemp = fsn.getSystemName();
                    if (systemTemp.equals(ftServiceNode.getSystemName())) {
                        ftServiceNodeListEnd.add(fsn);
                    }
                }
            }
        }
        for (FtTimingTask ftt : list2) {
            for (FtServiceNode fsn : ftServiceNodeListEnd) {
                if (fsn.getName().equals(ftt.getNodeNameTemp())) {
                    listEnd.add(ftt);
                }
            }
        }
        PageHelper.getInstance().getPage(ftTimingTask.getClass(), request, response, model, listEnd);
        return "modules/timingtask/ftTimingTaskList";
    }

    @RequiresPermissions("timingtask:ftTimingTask:view")
    @RequestMapping(value = "form")
    public String form(FtTimingTask ftTimingTask, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        //-----------------------------------------------
        Dict dict = new Dict();
        dict.setType(GlobalCons.TASK_TYPE);
        List<Dict> dictList = this.dictService.findList(dict);
        model.addAttribute("dictList", dictList);
        //-----------------------------------------------
        ResultDto<List<NodesModel.Node>> resultDto2 = nodesService.listAll(null, request);
        List<FtServiceNode> ftServiceNodeList = new ArrayList<FtServiceNode>();
        List<FtServiceNode> ftServiceNodeListEnd = new ArrayList<FtServiceNode>();
        List<String> nodeNameList = new ArrayList<String>();
        if (ResultDtoTool.isSuccess(resultDto2)) {
            List<NodesModel.Node> nodes = resultDto2.getData();
            for (NodesModel.Node node : nodes) {
                FtServiceNode ftServiceNode2 = new FtServiceNode();
                CfgModelConverter.convertTo(node, ftServiceNode2);
                ftServiceNodeList.add(ftServiceNode2);
            }
            for (FtServiceNode fsn : ftServiceNodeList) {
                if (null != fsn.getSystemName() && !"".equals(fsn.getSystemName())) {
                    String systemTemp = fsn.getSystemName();
                    if (systemTemp.equals(ftServiceNode.getSystemName())) {
                        ftServiceNodeListEnd.add(fsn);
                        nodeNameList.add(fsn.getName());
                    }
                }
            }
        } else {
            String data = resultDto2.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/timingtask/ftTimingTask/?repage";
        }
        model.addAttribute("nodeNameList", nodeNameList);
        model.addAttribute("ftTimingTask", ftTimingTask);
        return "modules/timingtask/ftTimingTaskForm";
    }

    @RequiresPermissions("timingtask:ftTimingTask:view")
    @RequestMapping(value = "editForm")
    public String editForm(FtTimingTask ftTimingTask, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<CrontabModel.Task> resultDto = crontabService.selByID(ftTimingTask);
        FtTimingTask entity = new FtTimingTask();
        Dict dict = new Dict();
        dict.setType(GlobalCons.TASK_TYPE);
        List<Dict> dictList = this.dictService.findList(dict);
        model.addAttribute("dictList", dictList);
        if (ResultDtoTool.isSuccess(resultDto)) {
            CrontabModel.Task task = resultDto.getData();
            CfgModelConverter.convertTo(task, entity);
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/timingtask/ftTimingTask/?repage";
        }
        model.addAttribute("ftTimingTask", entity);
        return "modules/timingtask/ftTimingTaskEditForm";
    }

    @RequiresPermissions("timingtask:ftTimingTask:edit")
    @RequestMapping(value = "save")
    public String save(FtTimingTask ftTimingTask, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ResultDto<String> resultDto = null;
        if (StringUtils.isNotEmpty(ftTimingTask.getId())) {
            resultDto = crontabService.update(ftTimingTask);
        } else {
            ftTimingTask.setState(FtTimingTask.STOPPING);
            String sTemp = UUID.randomUUID().toString();
            ftTimingTask.setId(sTemp);
            ftTimingTask.setSeq(sTemp);
            resultDto = crontabService.add(ftTimingTask);
        }
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存定时任务成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/timingtask/ftTimingTask/?repage";
    }

    @RequiresPermissions("timingtask:ftTimingTask:edit")
    @RequestMapping(value = "delete")
    public String delete(FtTimingTask ftTimingTask, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ResultDto<String> resultDto = crontabService.del(ftTimingTask);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除定时任务成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/timingtask/ftTimingTask/?repage";
    }

    @RequiresPermissions("timingtask:ftTimingTask:view")
    @RequestMapping(value = "detail")
    public String detail(FtTimingTask ftTimingTask, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String optType = "selByID";
        String saveStr = MessageFactory.getInstance().timingTask(ftTimingTask, optType);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(saveStr, ftServiceNode, String.class);//发送报文

        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            ftTimingTask = JsonToEntityFactory.getInstance().getTaskById(data);
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/timingtask/ftTimingTask/list";
        }
        if (null == ftTimingTask) {
            ftTimingTask = new FtTimingTask();
        }
        model.addAttribute("ftTimingTask", ftTimingTask);
        return "modules/timingtask/ftTimingTaskDetail";
    }

    @RequiresPermissions("timingtask:ftTimingTask:edit")
    @RequestMapping(value = "start")
    public String start(@RequestParam("ids") String ids, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String[] arr = ids.split(",");
        for (int i = 0; i < arr.length; i++) {
            FtTimingTask ftTimingTask = new FtTimingTask();
            ftTimingTask.setId(arr[i]);
            ftTimingTask.setSeq(arr[i]);
            ResultDto<String> resultDto = crontabService.start(ftTimingTask);
            if (ResultDtoTool.isSuccess(resultDto)) {
                String fileName = "crontab.xml";
                String sysname = ftServiceNode.getSystemName();
                String content = getCurrCfgContent(sysname, fileName, true);
                cfgZkService.write(sysname, fileName, content.trim());
                addMessage(redirectAttributes, "启动定时任务成功");
            } else {
                String data = resultDto.getMessage();
                addMessage(redirectAttributes, data);
            }
        }
        return "redirect:" + Global.getAdminPath() + "/timingtask/ftTimingTask/?repage";
    }

    @RequiresPermissions("timingtask:ftTimingTask:edit")
    @RequestMapping(value = "stop")
    public String stop(@RequestParam("ids") String ids, @RequestParam("dataNodeNames") String dataNodeNames, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String[] idArr = ids.split(",");
        String[] dataNodeNameArr = dataNodeNames.split(",");
        for (int i = 0; i < idArr.length; i++) {
            FtTimingTask ftTimingTask = new FtTimingTask();
            ftTimingTask.setId(idArr[i]);
            ftTimingTask.setSeq(idArr[i]);
            ResultDto<String> resultDto = crontabService.stop(ftTimingTask);
            if (ResultDtoTool.isSuccess(resultDto)) {
                String fileName = "crontab.xml";
                // 实时停止DataNode任务
                Map<String, String> data = new HashMap<>();
                data.put("ID", idArr[i]);
                String msg = RequestMsgHelper.generate(RequestMsgHelper.trun2Target(fileName), "stop", data);
                FtServiceNode ftServiceNode2 = new FtServiceNode();
                ftServiceNode2.setName(dataNodeNameArr[i]);
                ResultDto<NodesModel.Node> nodeResultDto = nodesService.selByName(ftServiceNode2, null);
                FtServiceNode ftServiceNode3 = new FtServiceNode();
                if (ResultDtoTool.isSuccess(nodeResultDto)) {
                    NodesModel.Node node = nodeResultDto.getData();
                    CfgModelConverter.convertTo(node, ftServiceNode3);
                    TCPAdapter otherTcpAdapter = new TCPAdapter();
                    otherTcpAdapter.invoke(msg, ftServiceNode3, String.class);
                }
                // 配置同步
                String sysname = ftServiceNode.getSystemName();
                String content = getCurrCfgContent(sysname, fileName, true);
                cfgZkService.write(sysname, fileName, content.trim());
                addMessage(redirectAttributes, "停止定时任务成功");
            } else {
                addMessage(redirectAttributes, resultDto.getMessage());
            }
        }
        return "redirect:" + Global.getAdminPath() + "/timingtask/ftTimingTask/?repage";
    }


    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String getAllStr = MessageFactory.getInstance().timingTask(new FtTimingTask(), "print");//生成查询报文
        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/timingtask/ftTimingTaskOtherConf";
    }

    @RequiresPermissions("timingtask:ftTimingTask:view")
    @RequestMapping(value = "confComp")
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("crontab", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().timingTask(new FtTimingTask(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/timingtask/ftTimingTaskConfComp";
    }

    @RequiresPermissions("timingtask:ftTimingTask:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }

    @RequiresPermissions("timingtask:ftTimingTask:edit")
    @RequestMapping(value = "synNodeConf")
    public String synNodeConf(@RequestParam("ids") String ids, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String fileName = "crontab.xml";
        String sysname = ftServiceNode.getSystemName();
        String content = getCurrCfgContent(sysname, fileName, true);
        cfgZkService.write(sysname, fileName, content.trim());
        addMessage(redirectAttributes, "定时任务同步到当前登录系统");
        return "redirect:" + Global.getAdminPath() + "/timingtask/ftTimingTask/?repage";
    }

    private String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException {
        return cfgFileService.getCurrCfgContent(sysname, fileName, hasTimestamp);
    }

}