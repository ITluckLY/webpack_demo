package com.dc.smarteam.service;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FileRenameModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.file.entity.FtFileRename;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class FileRenameService {
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<FileRenameModel.FileRename>> listAll() {
        FileRenameModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getFileRenames());
    }

    public ResultDto<FileRenameModel.FileRename> selbyIDorType(FtFileRename ftFileRename) {
        FileRenameModel model = loadModel();
        FileRenameModel.FileRename fileRename = null;
        for (FileRenameModel.FileRename fileRename2 : model.getFileRenames()) {
            if (StringUtils.equals(fileRename2.getId(), ftFileRename.getId())) {
                fileRename = fileRename2;
                break;
            }
            if (StringUtils.equals(fileRename2.getType(), ftFileRename.getType())) {
                fileRename = fileRename2;
                break;
            }
        }
        if (fileRename == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(fileRename);
    }

    public ResultDto<String> add(FtFileRename ftFileRename, FtServiceNode ftServiceNode) {
        if (StringUtils.isEmpty(ftFileRename.getId())) {
            return ResultDtoTool.buildError("节点ID不能为空");
        }
        FileRenameModel.FileRename newFileRename = new FileRenameModel.FileRename();
        FileRenameModel model = loadModel();
        for (FileRenameModel.FileRename fileRename : model.getFileRenames()) {
            if (StringUtils.equals(fileRename.getId(), ftFileRename.getId())) {
                return ResultDtoTool.buildError("添加失败，已有此文件命名");
            }
        }
        if (ftFileRename.getSysname() == null) {
            ftFileRename.setSysname(ftServiceNode.getSystemName());
        }
        CfgModelConverter.convertTo(ftFileRename, newFileRename);
        String path = newFileRename.getPath();
        String type = newFileRename.getType();
        if (path == null || type == null){
            return ResultDtoTool.buildError("添加失败，请填入路径及其类型");
        }
        if (!path.startsWith("/")){
            return ResultDtoTool.buildError("添加失败，请填入正确的路径");
        }
        if (path.contains(".") && type.equals("dir")){
            return ResultDtoTool.buildError("添加失败，dir类型不能包含文件名");
        } else if (!path.contains(".") && type.equals("file")) {
            return ResultDtoTool.buildError("添加失败，file类型需包含文件名");
        }
        model.getFileRenames().add(newFileRename);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtFileRename ftFileRename) {
        FileRenameModel.FileRename updateFileRename = null;
        FileRenameModel model = loadModel();
        for (FileRenameModel.FileRename fileRename : model.getFileRenames()) {
            if (StringUtils.equals(fileRename.getId(), ftFileRename.getId())) {
                updateFileRename = fileRename;
                break;
            }
        }
        if (updateFileRename == null) return ResultDtoTool.buildError("没有找到指定的文件清理信息");
        CfgModelConverter.convertTo(ftFileRename, updateFileRename);
        String path = updateFileRename.getPath();
        String type = updateFileRename.getType();
        if (path == null || type == null){
            return ResultDtoTool.buildError("更新失败，请填入路径及其类型");
        }
        if (!path.startsWith("/")){
            return ResultDtoTool.buildError("更新失败，请填入正确的路径");
        }
        if (path.contains(".") && type.equals("dir")){
            return ResultDtoTool.buildError("更新失败，dir类型不能包含文件名");
        } else if (!path.contains(".") && type.equals("file")) {
            return ResultDtoTool.buildError("更新失败，file类型需包含文件名");
        }
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del(FtFileRename ftFileRename) {
        FileRenameModel model = loadModel();
        List<FileRenameModel.FileRename> fileRenames = model.getFileRenames();
        for (FileRenameModel.FileRename fileRename : fileRenames) {
            if (StringUtils.equals(fileRename.getId(), ftFileRename.getId())) {
                fileRenames.remove(fileRename);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    private static final String CFG_FILE_NAME = "file_rename.xml";

    private FileRenameModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, FileRenameModel.class);
    }

    private void save(FileRenameModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }


}
