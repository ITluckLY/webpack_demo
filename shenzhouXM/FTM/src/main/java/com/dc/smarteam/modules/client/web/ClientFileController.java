package com.dc.smarteam.modules.client.web;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.FtFileMsgGen;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.vo.ZTreeNode;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.client.entity.AllPath;
import com.dc.smarteam.modules.client.entity.ClientFile;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import com.dc.smarteam.modules.client.service.ClientFileService;
import com.dc.smarteam.modules.file.entity.FtFile;
import com.dc.smarteam.modules.sys.entity.User;
import com.dcfs.esb.ftp.common.error.FtpException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(value = "${adminPath}/client/clientFile")
public class ClientFileController extends BaseController {
    @Autowired
    private ClientFileService clientFileService;

    @ModelAttribute
    public ClientFile get(@RequestParam(required = false) String id) {
        ClientFile entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = clientFileService.get(id);
        }
        if (entity == null) {
            entity = new ClientFile();
        }
        return entity;
    }

    @RequiresPermissions("client:clientFile:view")
    @RequestMapping(value = {"index"}, method = RequestMethod.GET)
    public String index(ClientSyn clientSyn, User user, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
//        if (null != clientSyn.getState() && clientSyn.getState().trim().equalsIgnoreCase("1")) {
        request.getSession().setAttribute("clientSyn", clientSyn);
        String allPath = FtFileMsgGen.getPath();
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(allPath, clientSyn, String.class);
        List<String> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getClientPath(data);
        }else{
            addMessage(redirectAttributes, "monitor连通失败");
            return "redirect:" + Global.getAdminPath() + "/client/clientSyn/?repage";
        }
        model.addAttribute("allPath",list);
        return "modules/client/clientFile";
    }

    @RequiresPermissions("client:clientFile:view")
    @RequestMapping(value = "list")
    public String list(ClientFile clientFile, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<ClientFile> page = clientFileService.findPage(new Page<ClientFile>(request, response), clientFile);
        model.addAttribute("page", page);
        return "modules/client/clientFileList";
    }


    @RequestMapping(value = "getFileTree")
    public
    @ResponseBody
    List<ZTreeNode> getFileTree(AllPath allPath) throws Throwable {
        List<String> path = allPath.getAllPath();
        clientFileService.setBasePath(path);
        return clientFileService.getFileTree();
    }

    @RequestMapping(value = "/query")
    public String query(ClientFile clientFile, HttpServletRequest request, HttpServletResponse response, Model model) throws FtpException {
        ClientSyn clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        List<ClientFile> list = new ArrayList<>();
        clientFile.setSystemName(clientSyn.getName());
        String queryStr = FtFileMsgGen.getQuery(clientFile);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(queryStr, clientSyn, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getClientContent(data);
        }
        PageHelper.getInstance().getPage(ClientFile.class,request,response,model,list);
        return "modules/client/clientFileList";
    }

    @RequestMapping(value = "getNodeFileTree")
    public
    @ResponseBody
    List<ZTreeNode> getNodeFileTree(String nodeId, HttpServletRequest request, Model model) throws Throwable {
        ClientSyn clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        String fileStr = FtFileMsgGen.getFileMsg();
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(fileStr, clientSyn, String.class);
        List<ZTreeNode> list;
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getFileTreeByTimestamp(data);
        } else {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getFileTree(data);
        }
        return list;
    }
    @RequestMapping(value = "getContent")
    public String getContent(String path, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<ClientFile> page = new Page<>(request, response);
        clientFileService.getContent(path, page);
        model.addAttribute("page", page);
        return "modules/client/clientFileList";
    }
    @RequestMapping(value = "getSubDir")
    @ResponseBody
    public List<ZTreeNode> getSubDir(String path, String treeName, HttpServletRequest request, HttpServletResponse response, Model model) {
        ClientSyn clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        if(path.equals("1")||path.equals("2")||path.equals("3")||path.equals("4")){
            path = treeName.substring(treeName.lastIndexOf("/"));
        }
        model.addAttribute("abPath",treeName);
        String dirStr = FtFileMsgGen.getSubDir(path);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(dirStr, clientSyn, String.class);
        List<ZTreeNode> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getSubDir(data);
        }
        return list;
    }

    @RequestMapping(value = "getNodeContent")
    public String getNodeContent(String path, String treeName, HttpServletRequest request, HttpServletResponse response, Model model) throws FtpException {
        ClientSyn clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        if(path.equals("1")||path.equals("2")||path.equals("3")||path.equals("4")){
            path = treeName.substring(treeName.lastIndexOf("/"));
        }
        String fileStr = FtFileMsgGen.getFileContent(path, null);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(fileStr, clientSyn, String.class);
        List<FtFile> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getContent(data);
        }
        model.addAttribute("path",path);
        PageHelper.getInstance().getPage(FtFile.class,request,response,model,list);
        ClientFile clientFile = new ClientFile();
        clientFile.setParentPath(path);
        query(clientFile,request,response,model);
        return "modules/client/clientFileList";
    }


    @RequestMapping(value = "repush")
    @RequiresPermissions("client:clientFile:repush")
    public String repush(ClientFile clientFile, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ClientSyn clientSyn = (ClientSyn) request.getSession().getAttribute("clientSyn");
        String repushStr = FtFileMsgGen.getRepush(clientFile);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(repushStr, clientSyn, String.class);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "重推成功");
        }else{
            addMessage(redirectAttributes, "重推失败");
        }
        return "redirect:" + Global.getAdminPath() + "/client/clientFile/query";

    }
}
