/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.FtFileMsgGen;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.vo.ZTreeNode;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.file.entity.FtFile;
import com.dc.smarteam.modules.file.service.FtFileService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sys.entity.User;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/file/ftFile")
public class FtFileController extends BaseController {

    @Resource(name = "FtFileServiceImpl")
    private FtFileService ftFileService;

    @ModelAttribute
    public FtFile get(@RequestParam(required = false) String id) {
        FtFile entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = ftFileService.get(id);
        }
        if (entity == null) {
            entity = new FtFile();
        }
        return entity;
    }

    @RequestMapping(value = {"index"}, method = RequestMethod.GET)
    public Object index(FtServiceNode ftServiceNode, User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        log.debug("ftServiceNode:{}",ftServiceNode);
        Map<String,Object> resultMap = new HashMap<>();
        if (null != ftServiceNode.getState() && ftServiceNode.getState().trim().equalsIgnoreCase("1")) {
            request.getSession().setAttribute("ftServiceNodeFtFile", ftServiceNode);
            resultMap.put("nodeName", ftServiceNode.getName());
        } else {
            String message = "节点未连接，请检查连接状态后，重新再试！";
            log.debug("message:{}",message);
            return ResultDtoTool.buildError(message);
        }

        return ResultDtoTool.buildSucceed(resultMap);
    }

    @RequiresPermissions("file:ftFile:view")
    @RequestMapping(value = "list")
    public String list(FtFile ftFile, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<FtFile> page = ftFileService.findPage(new Page<FtFile>(request, response), ftFile);
        model.addAttribute("page", page);
        return "modules/file/ftFileList";
    }

    @RequiresPermissions("file:ftFile:view")
    @RequestMapping(value = "edit")
    public String form(FtFile ftFile, Model model, HttpServletRequest request) {
        Page page = new Page();
        FtServiceNode node = CurrNameNodeHelper.getCurrNameNode(request);
        TCPAdapter tcpAdapter = new TCPAdapter();
        String getOneStr = FtFileMsgGen.getFileContent(ftFile.getId(), page);
        ResultDto<String> resultDto = tcpAdapter.invoke(getOneStr, node, String.class);
        List<FtFile> ftFiles;
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            ftFiles = JsonToEntityFactory.getInstance().getContent(data);
            ftFile = ftFiles.get(0);
        }
        model.addAttribute("ftFile", ftFile);
        return "modules/file/ftFileEditForm";
    }

    @RequiresPermissions("file:ftFile:edit")
    @RequestMapping(value = "save")
    public String save(FtFile ftFile, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        ftFileService.save(ftFile);
        addMessage(redirectAttributes, "保存文件管理成功");
        return "redirect:" + Global.getAdminPath() + "/file/ftFile/?repage";
    }

    @RequiresPermissions("file:ftFile:edit")
    @RequestMapping(value = "delete")
    public String delete(FtFile ftFile, RedirectAttributes redirectAttributes) {
        ftFileService.delete(ftFile);
        addMessage(redirectAttributes, "删除文件管理成功");
        return "redirect:" + Global.getAdminPath() + "/file/ftFile/?repage";
    }

    //获取用户目录
    @RequestMapping(value = "getFileTree")
    public
    @ResponseBody
    List<ZTreeNode> getFileTree() throws Throwable {
        return ftFileService.getFileTree();
    }

    //获取目录下文件和子目录
    @RequestMapping(value = "getContent")
    public String getContent(String path, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<FtFile> page = new Page<>(request, response);
        ftFileService.getContent(path, page);
        model.addAttribute("page", page);
        return "modules/file/ftFileList";
    }

    //获取服务节点上配置的文件路径
    @RequestMapping(value = "getNodeFileTree")
    public
    @ResponseBody
    List<ZTreeNode> getNodeFileTree(String nodeId, HttpServletRequest request, Model model) throws Throwable {
        FtServiceNode node = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeFtFile");
        String fileStr = FtFileMsgGen.getFileMsg();
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(fileStr, node, String.class);
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

    //获取服务节点上目录下子目录
    @RequestMapping(value = "getSubDir")
    @ResponseBody
    public List<ZTreeNode> getSubDir(String path, String nodeId, HttpServletRequest request, HttpServletResponse response, Model model) {

        FtServiceNode node = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeFtFile");

        String dirStr = FtFileMsgGen.getSubDir(path);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(dirStr, node, String.class);
        List<ZTreeNode> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getSubDir(data);
        }
        return list;
    }

    //获取服务节点上目录下文件
    @RequestMapping(value = "getNodeContent")
    public String getNodeContent(String path, String nodeId, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode node = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeFtFile");
        String fileStr = FtFileMsgGen.getFileContent(path, null);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(fileStr, node, String.class);
        List<FtFile> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getContent(data);
        }
        model.addAttribute("path",path);
       PageHelper.getInstance().getPage(FtFile.class,request,response,model,list);
        return "modules/file/ftFileList";
    }

    //获取服务节点上目录下文件
    @RequestMapping(value = "/query")
    public String query(FtFile ftFile, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode node = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeFtFile");
        ftFile.setSystemName(node.getSystemName());
        String queryStr = FtFileMsgGen.getQuery(ftFile);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(queryStr, node, String.class);
        List<FtFile> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getContent(data);
        }
        PageHelper.getInstance().getPage(FtFile.class,request,response,model,list);
        return "modules/file/ftFileList";
    }

    //文件下载
    @RequestMapping("download")
    public ResponseEntity<byte[]> download(FtServiceNode targetServiceNode, String fileId, String fileName, HttpServletRequest request, HttpServletResponse res) throws IOException {
        targetServiceNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeFtFile");
        String fileStr = FtFileMsgGen.download(fileId);
        TCPAdapter tcpAdapter = new TCPAdapter();
        //-------------------------没有使用ResultDto<String>---------------------------------
        File fm = tcpAdapter.fileInvoke(fileStr, targetServiceNode);//返回ResultDto
        if (fm != null) {
            File file = new File(fm.getAbsolutePath());
            String name = java.net.URLEncoder.encode(fileName, "UTF-8");

            OutputStream os = res.getOutputStream();
            try {
                res.reset();
                res.setHeader("Content-Disposition", "attachment; filename=" + name);
                res.setContentType("application/octet-stream");
                os.write(FileUtils.readFileToByteArray(file));
                os.flush();
            } finally {
                if (os != null) {
                    os.close();
                }
            }
        }
        //----------------------------------------------------------
        return null;
    }

    public String rename(FtFile ftFile, String nodeId, HttpServletRequest request, HttpServletResponse response, Model model) {//NOSONAR
        Page<FtFile> page = new Page<>(request, response);
        model.addAttribute("page", page);

        FtServiceNode node = CurrNameNodeHelper.getCurrNameNode(request);
        String queryStr = FtFileMsgGen.getQuery(ftFile);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(queryStr, node, String.class);
        List<FtFile> list = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getContent(data);
        }
        page.setList(list);
        page.setCount(list.size());
        return "modules/file/ftFileList";
    }


    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String getAllStr = MessageFactory.getInstance().file(new FtFile(), "print");//生成查询报文

        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/file/ftFileOtherConf";
    }


    @RequiresPermissions("file:ftFile:view")
    @RequestMapping(value = {"confComp"})
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("file", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().file(new FtFile(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/file/ftFileConfComp";
    }

    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }

}