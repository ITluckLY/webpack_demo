package com.dc.smarteam.modules.client.web;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.GsonUtil;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.FtFileMsgGen;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.DateHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.client.entity.ClientCfgFile;
import com.dc.smarteam.modules.client.entity.ClientRule;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import com.dc.smarteam.modules.client.service.ClientCfgFileService;
import com.dc.smarteam.modules.client.service.ClientSynService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "${adminPath}/client/clientSyn")
public class ClientSynController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ClientSynController.class);

    @Autowired
    private ClientCfgFileService clientCfgFileService;
    @Autowired
    private ClientSynService clientSynService;

    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = {"list", ""})
    public String list(ClientSyn clientSyn, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

        List<ClientSyn> list = clientSynService.findSingleList(clientSyn);
        for(ClientSyn clientSyn1 : list){
            clientSyn1.setRebootTimes(clientSynService.findRestartTimes(clientSyn1));
        }
        PageHelper.getInstance().getPage(clientSyn.getClass(), request, response, model, list);
        return "modules/client/clientSynList";
    }

    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = "ftsApiCfg")
    public String ftsApiCfg(ClientSyn clientSyn, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        if (clientSyn.getName() != null) {
            request.getSession().setAttribute("clientSyn", clientSyn);
        } else {
            clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        }
        Map<String,String>paramMap = new LinkedHashMap<>();
        ClientCfgFile clientCfgFile = new ClientCfgFile();
        clientCfgFile.setSystem(clientSyn.getName());
        clientCfgFile.setFileName("FtsApiConfig.properties");
        clientCfgFile.setNodeType("CLIENT");
        clientCfgFile = clientCfgFileService.findByNameAndFile(clientCfgFile);
        String content = "";
        String ftsApiCfg = FtFileMsgGen.getFtsApiCfg();
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(ftsApiCfg, clientSyn, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            if(clientCfgFile == null){
                content = resultDto.getData();
                clientCfgFileService.bakfiledata(clientSyn,"FtsApiConfig.properties",content);
            }else{
                content = clientCfgFile.getContent();
            }
        }else{
            addMessage(redirectAttributes, "monitor连通失败");
            return "redirect:" + Global.getAdminPath() + "/client/clientSyn/?repage";
        }
        paramMap = StringUtils.toParamMap(content);
        model.addAttribute("returnMsg", paramMap);
        return "modules/client/ftsApiCfg";
    }

    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = "ftsApiCfgCache")
    public String ftsApiCfgCache(ClientSyn clientSyn, HttpServletRequest request, Model model) {
        clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        Map<String,String>paramMap = new LinkedHashMap<>();
        ClientCfgFile clientCfgFile = new ClientCfgFile();
        clientCfgFile.setSystem(clientSyn.getName());
        clientCfgFile.setFileName("FtsApiCfgCache.properties");
        clientCfgFile.setNodeType("CLIENT");
        clientCfgFile = clientCfgFileService.findByNameAndFile(clientCfgFile);
        String content = "";
        if(clientCfgFile == null){
            String ftsApiCfgCache = FtFileMsgGen.getFtsApiCfgCache();
            TCPAdapter tcpAdapter = new TCPAdapter();
            ResultDto<String> resultDto = tcpAdapter.invoke(ftsApiCfgCache, clientSyn, String.class);
            if (ResultDtoTool.isSuccess(resultDto)) {
                content = resultDto.getData();
                clientCfgFileService.bakfiledata(clientSyn,"FtsApiCfgCache.properties",content);
            }
        }else{
            content = clientCfgFile.getContent();
        }
        paramMap = StringUtils.toParamMap(content);
        model.addAttribute("returnMsg", paramMap);
        return "modules/client/ftsApiCfgCache";
    }

    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = "ftsClientConfig")
    public String ftsClientCfg(ClientSyn clientSyn, HttpServletRequest request, Model model) {
        clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        Map<String,String>paramMap = new LinkedHashMap<>();
        ClientCfgFile clientCfgFile = new ClientCfgFile();
        clientCfgFile.setSystem(clientSyn.getName());
        clientCfgFile.setFileName("FtsClientConfig.properties");
        clientCfgFile.setNodeType("CLIENT");
        clientCfgFile = clientCfgFileService.findByNameAndFile(clientCfgFile);
        String content = "";
        if(clientCfgFile == null){
            String ftsClientCfg = FtFileMsgGen.getFtsClientConfig();
            TCPAdapter tcpAdapter = new TCPAdapter();
            ResultDto<String> resultDto = tcpAdapter.invoke(ftsClientCfg, clientSyn, String.class);
            if (ResultDtoTool.isSuccess(resultDto)) {
                content = resultDto.getData();
                clientCfgFileService.bakfiledata(clientSyn,"FtsClientConfig.properties",content);
            }
        }else{
            content = clientCfgFile.getContent();
        }
        System.out.println(content);
        paramMap = StringUtils.toParamMap(content);
        model.addAttribute("returnMsg", paramMap);
        return "modules/client/ftsClientConfig";
    }

    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = "log4j2")
    public String log4j2(ClientSyn clientSyn, HttpServletRequest request, Model model) {
        clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        ClientCfgFile clientCfgFile = new ClientCfgFile();
        clientCfgFile.setSystem(clientSyn.getName());
        clientCfgFile.setFileName("log4j2.xml.bak");
        clientCfgFile.setNodeType("CLIENT");
        clientCfgFile = clientCfgFileService.findByNameAndFile(clientCfgFile);
        String content = "";
        String cliContent = "";
        String log4j2 = FtFileMsgGen.getLog4j2();
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(log4j2, clientSyn, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            cliContent = resultDto.getData();
            if(clientCfgFile == null){
                content = resultDto.getData();
                clientCfgFileService.bakfiledata(clientSyn, "log4j2.xml.bak",content);
            }else{
                content = clientCfgFile.getContent();
            }
        }else{
            content = clientCfgFile.getContent();
        }
        model.addAttribute("returnMsg",content);
        model.addAttribute("cliContent",cliContent);
        return "modules/client/log4j2";
    }

    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = "quartz")
    public String quartz(ClientSyn clientSyn, HttpServletRequest request, Model model) {
        clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        Map<String,String>paramMap = new LinkedHashMap<>();
        ClientCfgFile clientCfgFile = new ClientCfgFile();
        clientCfgFile.setSystem(clientSyn.getName());
        clientCfgFile.setFileName("quartz.properties");
        clientCfgFile.setNodeType("CLIENT");
        clientCfgFile = clientCfgFileService.findByNameAndFile(clientCfgFile);
        String content = "";
        if(clientCfgFile == null){
            String quartz = FtFileMsgGen.getQuartz();
            TCPAdapter tcpAdapter = new TCPAdapter();
            ResultDto<String> resultDto = tcpAdapter.invoke(quartz, clientSyn, String.class);
            if (ResultDtoTool.isSuccess(resultDto)) {
                content = resultDto.getData();
                clientCfgFileService.bakfiledata(clientSyn,"quartz.properties",content);
            }
        }else{
            content = clientCfgFile.getContent();
        }
        paramMap = StringUtils.toParamMap(content);
        model.addAttribute("returnMsg", paramMap);
        return "modules/client/quartz";
    }

    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = "rule")
    public String rule(ClientSyn clientSyn, HttpServletRequest request, Model model) {
        clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        ClientCfgFile clientCfgFile = new ClientCfgFile();
        clientCfgFile.setSystem(clientSyn.getName());
        clientCfgFile.setFileName("rule.xml");
        clientCfgFile.setNodeType("CLIENT");
        clientCfgFile = clientCfgFileService.findByNameAndFile(clientCfgFile);
        String content = "";
        if(clientCfgFile == null){
            String rule = FtFileMsgGen.getRule();
            TCPAdapter tcpAdapter = new TCPAdapter();
            ResultDto<String> resultDto = tcpAdapter.invoke(rule, clientSyn, String.class);
            if (ResultDtoTool.isSuccess(resultDto)) {
                content = resultDto.getData();
                clientCfgFileService.bakfiledata(clientSyn,"rule.xml",content);
            }
        }else{
            content = clientCfgFile.getContent();
        }
        List<ClientRule> clientRules = StringUtils.readStringXml(content);
        model.addAttribute("returnMsg", clientRules);
        return "modules/client/rule";
    }

    @RequiresPermissions("NodeMonitor:clientVersion:view")
    @RequestMapping(value = {"history"})
    public String history(ClientSyn clientSyn, HttpServletRequest request, HttpServletResponse response, Model model) {
        Date beginDate = null;
        Date endDate = null;
        if (clientSyn.getBeginDate() == null && clientSyn.getEndDate() == null) {
            beginDate = DateHelper.getStartDate();
            endDate = DateHelper.getEndDate();
        }
        if (clientSyn.getBeginDate() != null) {
            beginDate = clientSyn.getBeginDate();
        }
        if (clientSyn.getEndDate() != null) {
            endDate = clientSyn.getEndDate();
        }
        if (clientSyn.getName() != null) {
            request.getSession().setAttribute("clientSyn", clientSyn);
        } else {
            clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        clientSyn.setBeginDate(beginDate);
        clientSyn.setEndDate(endDate);
        model.addAttribute("endDate", sdf.format(clientSyn.getEndDate()));
        model.addAttribute("beginDate", sdf.format(clientSyn.getBeginDate()));
        List<ClientSyn> list = clientSynService.findList(clientSyn);
        PageHelper.getInstance().getPage(clientSyn.getClass(), request, response, model, list);
        return "modules/client/history";
    }

    @RequestMapping(value = "clientStart")
    public String clientStart(ClientSyn clientSyn, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        if (clientSyn.getStatus().equals("running")) {
            addMessage(redirectAttributes, "不能启动正在运行的客户端");
            return "redirect:" + Global.getAdminPath() + "/client/clientSyn";
        }
        String msg = MessageFactory.getInstance().client("startDataServer");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, clientSyn, String.class, true);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "客户端启动成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/client/clientSyn";
    }

    @RequestMapping(value = "clientStop")
    public String clientStop(ClientSyn clientSyn, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        if (clientSyn.getStatus().equals("stopped")) {
            addMessage(redirectAttributes, "不能停止未启动的客户端");
            return "redirect:" + Global.getAdminPath() + "/client/clientSyn";
        }
        String msg = MessageFactory.getInstance().client("stopDataServer");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, clientSyn, String.class, true);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "停止客户端成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/client/clientSyn";
    }

    @RequestMapping(value = "editCfg")
    @ResponseBody
    public String editCfg(String fileName,HttpServletRequest request) {
        ClientSyn clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        String str = request.getParameter("map");
        ClientCfgFile clientCfgFile = new ClientCfgFile();
        clientCfgFile.setSystem(clientSyn.getName());
        clientCfgFile.setFileName(fileName);
        clientCfgFile.setNodeType("CLIENT");
        clientCfgFile = clientCfgFileService.findByNameAndFile(clientCfgFile);
        String content =clientCfgFile.getContent();
        String newContent = "";
        Boolean result;
        List<ClientRule> clientRules = new ArrayList<>();
        if(fileName.equals("log4j2.xml.bak")){
            newContent = str;
        } else if(fileName.equals("rule.xml")) {
            JSONArray jsonArray=JSONArray.fromObject(str);
            for(Object o : jsonArray){
                JSONObject jsonObject2=JSONObject.fromObject(o);
                ClientRule clientRule=(ClientRule)JSONObject.toBean(jsonObject2, ClientRule.class);
                clientRules.add(clientRule);
            }
            newContent = StringUtils.upDateXMLStr(clientRules,content);
        } else {
            Map<String,String> map = GsonUtil.fromJson(str,Map.class);
            newContent = StringUtils.changeContext(content,map);
        }
        clientCfgFile.setContent(newContent);
        result = clientCfgFileService.bakfiledata(clientSyn,clientCfgFile.getFileName(),newContent);
        return result.toString();
    }


    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = "getParam")
    @ResponseBody
    public Object getParam(ClientSyn clientSyn, String fileName, HttpServletRequest request, Model model) {
        clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        String message = FtFileMsgGen.getConfig(fileName);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(message, clientSyn, String.class);
        String result = null;
        Map<String,String> paramMap = new HashMap<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            result = resultDto.getData();
            if(fileName.equals("rule.xml")){
                return StringUtils.readStringXml(result);
            }
            paramMap = StringUtils.toParamMap(result);
        }
        return paramMap;
    }

    @RequiresPermissions("client:clientSyn:view")
    @RequestMapping(value = "sync")
    @ResponseBody
    public String sync(ClientSyn clientSyn, String fileName, HttpServletRequest request, Model model) {
        clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        ClientCfgFile clientCfgFile = new ClientCfgFile();
        clientCfgFile.setSystem(clientSyn.getName());
        clientCfgFile.setIp(clientSyn.getIp());
        clientCfgFile.setPort(clientSyn.getPort());
        clientCfgFile.setFileName(fileName);
        clientCfgFile.setNodeType("CLIENT");
        clientCfgFile = clientCfgFileService.findByNameAndFile(clientCfgFile);
        String content = clientCfgFile.getContent();
        String sync = FtFileMsgGen.getSync(content,fileName);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(sync, clientSyn, String.class);
        log.debug(resultDto.getData());
        return "";
    }
}
