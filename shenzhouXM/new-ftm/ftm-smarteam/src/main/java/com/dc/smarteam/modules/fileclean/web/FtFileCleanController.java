/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.fileclean.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FileCleanModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.common.zk.CfgZkService;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.fileclean.entity.FtFileClean;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.FileCleanService;
import com.dc.smarteam.util.PublicRepResultTool;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 文件清理 Controller
 *
 * @author lyy
 * @version 2020.10.22
 */
@RestController
@RequestMapping(value = "${adminPath}/fileclean/ftFileClean")
public class FtFileCleanController extends BaseController {
    @Resource
    private FileCleanService fileCleanService;

    @Autowired
    private CfgFileService cfgFileService;
    @Resource
    private CfgZkService cfgZkService;

    @RequestMapping(value = "list", produces="application/json;charset=UTF-8")
    public Object list(FtFileClean ftFileClean, HttpServletRequest request, HttpServletResponse response,  Map map) throws Exception {

       /* FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            //return  PublicRepResultTool.sendResult("9999","请先设置节点组！！！",null);
        }*/

        ResultDto<List<FileCleanModel.FileClean>> resultDto = fileCleanService.listAll();
        List<FtFileClean> list = new ArrayList<FtFileClean>();
        List<FtFileClean> list2 = new ArrayList<FtFileClean>();
        List<FtFileClean> listTemp = new ArrayList<FtFileClean>();

        if (ResultDtoTool.isSuccess(resultDto)) {
            List<FileCleanModel.FileClean> filecleans = resultDto.getData();
            for (FileCleanModel.FileClean fileClean : filecleans) {
                FtFileClean ftFileClean2 = new FtFileClean();
                CfgModelConverter.convertTo(fileClean, ftFileClean2);
                list.add(ftFileClean2);
            }

            for (FtFileClean ffc : list) {  //ftServiceNode.getSystemName()
                if (ffc.getSystem() != null && ffc.getSystem().equalsIgnoreCase("comm001")) {
                    listTemp.add(ffc);
                }
            }
            boolean targetDir = false;
            boolean state = false;
            if (ftFileClean.getTargetDir() != null && !"".equals(ftFileClean.getTargetDir())) {
                targetDir = true;
            }
            if (ftFileClean.getState() != null && !"".equals(ftFileClean.getState())) {
                state = true;
            }
            if (targetDir || state) {
                for (int i = 0; i < listTemp.size(); i++) {
                    FtFileClean ffc = listTemp.get(i);
                    if (targetDir && !ffc.getTargetDir().toLowerCase().contains(ftFileClean.getTargetDir().toLowerCase())) {
                        continue;
                    }
                    if (state && !ffc.getState().toLowerCase().contains(ftFileClean.getState().toLowerCase())) {
                        continue;
                    }
                    list2.add(listTemp.get(i));
                }
            } else {
                list2 = listTemp;
            }
        } else {
            return  PublicRepResultTool.sendResult("9999",resultDto.getMessage(),null);
        }

        PageHelper.getInstance().getPage(ftFileClean.getClass(), request, response, map, list2);
        return  PublicRepResultTool.sendResult("0000","成功",list2);
    }



    @RequestMapping(value = "form", produces="application/json;charset=UTF-8")
    public Object form(FtFileClean ftFileClean) {
        Map<String,Object> resMap = new HashMap<>();
        if (!(ftFileClean.getId() == null || ftFileClean.getId().equals(""))) {
            ResultDto<FileCleanModel.FileClean> resultDto = fileCleanService.selByID(ftFileClean);
            List<FtFileClean> list = new ArrayList<FtFileClean>();
            if (ResultDtoTool.isSuccess(resultDto)) {
                FileCleanModel.FileClean fileClean = resultDto.getData();
                FtFileClean ftFileClean2 = new FtFileClean();
                CfgModelConverter.convertTo(fileClean, ftFileClean2);
                list.add(ftFileClean2);

                for (FtFileClean ffc : list) {
                    if (ffc.getId().equals(ftFileClean.getId())) {
                        ftFileClean = ffc;
                    }
                }
            } else {
                return  PublicRepResultTool.sendResult("0000",resultDto.getMessage(),null);
            }
        }
        resMap.put("ftFileClean", ftFileClean);
        return   PublicRepResultTool.sendResult("0000","成功",resMap);
    }


    /**
     *   新增保存
     * @param request
     * @param ftFileClean
     * @return
     */
    @RequestMapping(value = "save" , produces="application/json;charset=UTF-8")
    public Object save(HttpServletRequest request, FtFileClean ftFileClean) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        ResultDto<String> resultDto = null;
        String message ="";
        if (StringUtils.isNoneEmpty(ftFileClean.getId())) {
            ResultDto<FileCleanModel.FileClean> resultDto1 = fileCleanService.selByID(ftFileClean);
            if (ResultDtoTool.isSuccess(resultDto1)) {
                FileCleanModel.FileClean fileClean = resultDto1.getData();
                ftFileClean.setState(fileClean.getState());
            }
            resultDto = fileCleanService.update(ftFileClean);
        } else {
            ftFileClean.setId(UUID.randomUUID().toString());
            ftFileClean.setState(FtFileClean.WAITING);
            resultDto = fileCleanService.add(ftFileClean, ftServiceNode);
        }

        if (ResultDtoTool.isSuccess(resultDto)) {
            message="保存文件清理成功";
        } else {
            message =resultDto.getMessage();
        }
        return   PublicRepResultTool.sendResult("0000",message,null);
    }

    @RequestMapping(value = "delete", produces="application/json;charset=UTF-8")
    public Object delete( FtFileClean ftFileClean) {
        ResultDto<String> resultDto = fileCleanService.del(ftFileClean);
        String msg= "";

        if (ResultDtoTool.isSuccess(resultDto)) {
            msg = "删除文件清理成功";
        } else {
            msg =resultDto.getMessage();
        }
        return  PublicRepResultTool.sendResult("0000",msg ,null);
    }

    /**
     *  启动
     * @param ftFileClean
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "startFileClean" , produces="application/json;charset=UTF-8")
    public Object startFileClean(FtFileClean ftFileClean, HttpServletRequest request) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String message = "";
        if (!(ftFileClean.getId() == null || ftFileClean.getId().equals(""))) {
            ResultDto<FileCleanModel.FileClean> resultDto = fileCleanService.selByID(ftFileClean);
            if (ResultDtoTool.isSuccess(resultDto)) {
                FileCleanModel.FileClean fileClean = resultDto.getData();
                FtFileClean ftFileClean2 = new FtFileClean();
                CfgModelConverter.convertTo(fileClean, ftFileClean2);
                ftFileClean2.setState("1");
                ResultDto<String> resultDto2 = fileCleanService.update(ftFileClean2);
                if (ResultDtoTool.isSuccess(resultDto2)) {
                    String fileName = "file_clean.xml";
                    String sysname = ftServiceNode.getSystemName();
                    String content = getCurrCfgContent(sysname, fileName, true);
                    cfgZkService.write(sysname, fileName, content.trim());
                    message ="同步文件清理成功";
                } else {
                    message = "同步文件清理失败";
                }
            }
        }
        return  PublicRepResultTool.sendResult("0000",message,null);
    }

    /**
     *     停止
     * @param ftFileClean
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "stopFileClean", produces="application/json;charset=UTF-8")
    public Object stopFileClean(FtFileClean ftFileClean, HttpServletRequest request ) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String ms="";
        //---------后台缺少处理流程-----------
        if (!(ftFileClean.getId() == null || ftFileClean.getId().equals(""))) {
            ResultDto<FileCleanModel.FileClean> resultDto = fileCleanService.selByID(ftFileClean);
            if (ResultDtoTool.isSuccess(resultDto)) {
                FileCleanModel.FileClean fileClean = resultDto.getData();
                FtFileClean ftFileClean2 = new FtFileClean();
                CfgModelConverter.convertTo(fileClean, ftFileClean2);
                ftFileClean2.setState("0");
                ResultDto<String> resultDto2 = fileCleanService.update(ftFileClean2);
                if (ResultDtoTool.isSuccess(resultDto2)) {
                    String fileName = "file_clean.xml";
                    String sysname = ftServiceNode.getSystemName();
                    String content = getCurrCfgContent(sysname, fileName, true);
                    cfgZkService.write(sysname, fileName, content.trim());
                    ms ="同步文件清理成功";
                } else {
                    ms ="同步文件清理失败";
                }
            }
        }
        return  PublicRepResultTool.sendResult("0000",ms,null);
    }


    /***
     *      有点小问题 ？？？？ 这个model  后期得改公共的东西 model
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "otherConf", produces="application/json;charset=UTF-8")
    public Object otherConf( HttpServletRequest request, Model model) {
        String getAllStr = MessageFactory.getInstance().fileClean(new FtFileClean(), "print");//生成查询报文
        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);

        return  PublicRepResultTool.sendResult("0000","",getAllStr);
    }

    @RequestMapping(value = "confComp", produces="application/json;charset=UTF-8")
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("file_clean", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().fileClean(new FtFileClean(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/fileclean/ftFileCleanConfComp";
    }

    @RequestMapping(value = "catchFileCfg", produces="application/json;charset=UTF-8")
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }

    private String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException {
        return cfgFileService.getCurrCfgContent(sysname, fileName, hasTimestamp);
    }
}