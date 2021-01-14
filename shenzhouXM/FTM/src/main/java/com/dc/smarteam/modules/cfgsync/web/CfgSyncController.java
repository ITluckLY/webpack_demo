package com.dc.smarteam.modules.cfgsync.web;

import com.dc.smarteam.cfgmodel.NodesModel;
import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.common.zk.CfgZkService;
import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.RequestMsgHelper;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.NodesService;
import com.dc.smarteam.util.XMLDealTool;
import com.google.gson.JsonObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.dom4j.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mocg on 2017/5/8.
 */
@Controller
@RequestMapping(value = "${adminPath}/cfgsync")
public class CfgSyncController extends BaseController {
    @Resource
    private CfgFileService cfgFileService;
    @Resource
    private CfgZkService cfgZkService;
    @Resource
    private NodesService nodesService;

    @RequiresPermissions("cfg:cfgsync:view")
    @RequestMapping(value = {"index", ""})
    public String synConf(HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        model.addAttribute("ftServiceNode", ftServiceNode);
        return "modules/cfgsync/ftConfIndex";
    }

    @RequiresPermissions("cfg:cfgsync:view")
    @RequestMapping(value = {"confComp"})
    public String confComp(@RequestParam String fileName, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String sysname = ftServiceNode.getSystemName();
        String currCfgContent = getCurrCfgContent(sysname, fileName, false);

        List<FtServiceNode> activeNodeList = new ArrayList<>();
        List<FtServiceNode> inactiveNodeList = new ArrayList<>();
        ResultDto<List<NodesModel.Node>> resultDto = nodesService.listAll(null, request);
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<NodesModel.Node> nodeList = resultDto.getData();
            for (NodesModel.Node node : nodeList) {
                if (NodeType.DATANODE.name().equalsIgnoreCase(node.getType()) && StringUtils.equals(sysname, node.getSystem().getName())) {
                    //boolean isActive = "1".equals(node.getState());//NOSONAR
                    //根据zk获取节点状态
                    String ipPort = node.getIp() + ":" + node.getCmdPort();
                    Map<String, JsonObject> map = ZkService.getInstance().getDataNodeMap();
                    boolean isActive = map != null && map.containsKey(ipPort);
                    if (isActive) {
                        FtServiceNode ftNode = new FtServiceNode();
                        ftNode.setName(node.getName());
                        ftNode.setIpAddress(node.getIp());
                        ftNode.setCmdPort(node.getCmdPort());
                        ftNode.setName(node.getName());
                        ftNode.setState(node.getState());
                        activeNodeList.add(ftNode);
                    } else {
                        FtServiceNode ftNode = new FtServiceNode();
                        ftNode.setName(node.getName());
                        ftNode.setIpAddress(node.getIp());
                        ftNode.setCmdPort(node.getCmdPort());
                        ftNode.setName(node.getName());
                        ftNode.setState(node.getState());
                        inactiveNodeList.add(ftNode);
                    }
                }
            }
        }

        Map<String, String> cfgContentMap = new HashMap<>();
        for (FtServiceNode node : activeNodeList) {
            String msg = RequestMsgHelper.generate(RequestMsgHelper.trun2Target(fileName), "print", null);
            TCPAdapter otherTcpAdapter = new TCPAdapter();
            ResultDto<String> dto = otherTcpAdapter.invoke(msg, node, String.class);
            if (ResultDtoTool.isSuccess(dto)) {
                cfgContentMap.put(node.getName(), dto.getData());
            } else {
                cfgContentMap.put(node.getName(), null);
            }
    }

        String currCfgContentNoSpace = currCfgContent.replaceAll(" |\n|\t", "");
        List<String> notSameList = new ArrayList<>();
        for (Map.Entry<String, String> entry : cfgContentMap.entrySet()) {
            String value = entry.getValue();
            if (value == null) continue;
            Document docRm = XMLDealTool.readXml(value);
            String asXmlRm = XMLDealTool.withoutTimestamp(docRm).asXML();
            String asXml2Temp = asXmlRm.replaceAll(" |\n|\t", "");
            if (!StringUtils.equals(currCfgContentNoSpace, asXml2Temp)) {
                notSameList.add(entry.getKey());
            }
        }

        if (!notSameList.isEmpty()) {
            model.addAttribute("resultSyn", "文件不相同，请先核对之后，再进行同步操作。");
        } else {
            model.addAttribute("resultSyn", "文件相同，不需要进行同步操作。");
        }

        model.addAttribute("otherFsnConfMap", cfgContentMap);
        model.addAttribute("ftServiceNodeList", activeNodeList);
        model.addAttribute("fileName", fileName);
        model.addAttribute("currCfgContent", currCfgContent);
        model.addAttribute("inactiveNodeList", inactiveNodeList);

        return "modules/cfgsync/ftConfComp";
    }

    @RequiresPermissions("cfg:cfgsync:edit")
    @ResponseBody
    @RequestMapping("sync")
    public String sync(@RequestParam String fileName, HttpServletRequest request) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String sysname = ftServiceNode.getSystemName();
        String content = getCurrCfgContent(sysname, fileName, true);
        cfgZkService.write(sysname, fileName, content.trim());
        return "true";
    }

    @RequiresPermissions("cfg:cfgsync:edit")
    @ResponseBody
    @RequestMapping("syncAll")
    public String syncAll(HttpServletRequest request) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        String sysname = ftServiceNode.getSystemName();
        List<String> cfgFileNameList = new ArrayList<>();
        cfgFileNameList.add("components.xml");
        cfgFileNameList.add("crontab.xml");
        cfgFileNameList.add("file.xml");
        cfgFileNameList.add("file_clean.xml");
        cfgFileNameList.add("file_rename.xml");
        cfgFileNameList.add("flow.xml");
        cfgFileNameList.add("nodes.xml");
        cfgFileNameList.add("route.xml");
        cfgFileNameList.add("services_info.xml");
        cfgFileNameList.add("system.xml");
        cfgFileNameList.add("user.xml");
        cfgFileNameList.add("vsysmap.xml");
        cfgFileNameList.add("client_status.xml");
        // 此处添加一个文件检验流程文件 file_process.xml
        cfgFileNameList.add("file_process.xml");
        // 此处添加一个流量控制文件 netty.xml
        cfgFileNameList.add("netty.xml");
        cfgFileNameList.add("keys.xml");
        for (String fileName : cfgFileNameList) {
            String content = getCurrCfgContent(sysname, fileName, true);
            cfgZkService.write(sysname, fileName, content); // 写入 zk
        }

        return "true";
    }

    private String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException {
        return cfgFileService.getCurrCfgContent(sysname, fileName, hasTimestamp);
    }

}
