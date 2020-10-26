package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FileCleanModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.fileclean.entity.FtFileClean;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class FileCleanService extends AbstractService {
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<FileCleanModel.FileClean>> select(FtFileClean ftFileClean, HttpServletRequest request) {
        FileCleanModel model = loadModel();
        List<FileCleanModel.FileClean> fileCleans = model.getFileCleans();
        return ResultDtoTool.buildSucceed(fileCleans);
    }

    public ResultDto<List<FileCleanModel.FileClean>> listAll() {
        FileCleanModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getFileCleans());
    }

    public ResultDto<FileCleanModel.FileClean> selByID(FtFileClean ftFileClean) {
        FileCleanModel model = loadModel();
        FileCleanModel.FileClean fileClean = null;
        for (FileCleanModel.FileClean fileClean2 : model.getFileCleans()) {
            if (StringUtils.equals(fileClean2.getId(), ftFileClean.getId())) {
                fileClean = fileClean2;
                break;
            }
        }
        if (fileClean == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(fileClean);
    }

    public ResultDto<String> add(FtFileClean ftFileClean, FtServiceNode ftServiceNode) {
        if (StringUtils.isEmpty(ftFileClean.getTargetDir())) {
            return ResultDtoTool.buildError("目标目录不能为空");
        }
        if (StringUtils.isEmpty(ftFileClean.getKeepTime())) {
            return ResultDtoTool.buildError("保留时间不能为空");
        }
        if (ftFileClean.getSystem() == null) {
            ftFileClean.setSystem(ftServiceNode.getSystemName());
        }
        FileCleanModel.FileClean newFileClean = new FileCleanModel.FileClean();
        FileCleanModel model = loadModel();
        for (FileCleanModel.FileClean fileClean : model.getFileCleans()) {
            if (StringUtils.equalsIgnoreCase(fileClean.getSrcPath(), ftFileClean.getTargetDir())) {
                return ResultDtoTool.buildError("添加失败，已有此文件清理");
            }
        }
        CfgModelConverter.convertTo(ftFileClean, newFileClean);
        model.getFileCleans().add(newFileClean);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtFileClean ftFileClean) {
        FileCleanModel.FileClean updateFileClean = null;
        FileCleanModel model = loadModel();
        for (FileCleanModel.FileClean fileClean : model.getFileCleans()) {
            if (StringUtils.equals(fileClean.getId(), ftFileClean.getId())) {
                updateFileClean = fileClean;
                break;
            }
        }
        if (updateFileClean == null) return ResultDtoTool.buildError("没有找到指定的文件清理信息");
        CfgModelConverter.convertTo(ftFileClean, updateFileClean);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del(FtFileClean ftFileClean) {
        FileCleanModel model = loadModel();
        List<FileCleanModel.FileClean> fileCleans = model.getFileCleans();
        for (FileCleanModel.FileClean fileClean : fileCleans) {
            if (StringUtils.equals(fileClean.getId(), ftFileClean.getId())) {
                fileCleans.remove(fileClean);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    private static final String CFG_FILE_NAME = "file_clean.xml";

    private FileCleanModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, FileCleanModel.class);
    }

    private void save(FileCleanModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }


    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }

    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtFileClean ftFileClean = (FtFileClean)curr;
        if(isNew){
            FileCleanModel.FileClean addFileClean = new FileCleanModel.FileClean();
            CfgModelConverter.convertTo(ftFileClean,addFileClean);
            return XmlBeanUtil.toXml(addFileClean);
        }
        FileCleanModel.FileClean updateFileClean = null;
        FileCleanModel model = loadModel();
        for (FileCleanModel.FileClean fileClean : model.getFileCleans()) {
            if (StringUtils.equals(fileClean.getId(), ftFileClean.getId())) {
                updateFileClean = fileClean;
                break;
            }
        }
        if (updateFileClean == null) return null;
        return XmlBeanUtil.toXml(updateFileClean);
    }
}
