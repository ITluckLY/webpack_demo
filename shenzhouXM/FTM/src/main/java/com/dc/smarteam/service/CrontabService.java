package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.CrontabModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.timingtask.entity.FtTimingTask;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by huangzbb on 2017/5/9.
 */
@Service
public class CrontabService extends AbstractService {
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<CrontabModel.Task>> select(FtTimingTask ftTimingTask, HttpServletRequest request) {
        CrontabModel model = loadModel();
        List<CrontabModel.Task> tasks = model.getTasks();
        return ResultDtoTool.buildSucceed(tasks);
    }

    public ResultDto<List<CrontabModel.Task>> listAll() {
        CrontabModel crontabModel = loadModel();
        return ResultDtoTool.buildSucceed(crontabModel.getTasks());
    }


    public ResultDto<CrontabModel.Task> selByID(FtTimingTask ftTimingTask) {
        CrontabModel crontabModel = loadModel();
        CrontabModel.Task target = null;
        for (CrontabModel.Task task : crontabModel.getTasks()) {
            if (StringUtils.equals(task.getId(), ftTimingTask.getId())) {
                target = task;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(target);
    }

    public ResultDto<String> add(FtTimingTask ftTimingTask) {
        if (StringUtils.isEmpty(ftTimingTask.getId())) {
            return ResultDtoTool.buildError("编号不能为空");
        }
        if (StringUtils.isEmpty(ftTimingTask.getNodeNameTemp())) {
            return ResultDtoTool.buildError("节点名不能为空");
        }
        if (StringUtils.isEmpty(ftTimingTask.getCount())) {
            return ResultDtoTool.buildError("执行次数不能为空");
        }
        if (StringUtils.isEmpty(ftTimingTask.getState())) {
            return ResultDtoTool.buildError("执行状态不能为空");
        }
        if (StringUtils.isEmpty(ftTimingTask.getFlowId())) {
            return ResultDtoTool.buildError("调用流程不能为空");
        }
        if (StringUtils.isEmpty(ftTimingTask.getParams())) {
            return ResultDtoTool.buildError("参数不能为空");
        }
        if (StringUtils.isEmpty(ftTimingTask.getTaskName())) {
            return ResultDtoTool.buildError("任务名称不能为空");
        }

        CrontabModel.Task newTask = new CrontabModel.Task();
        CrontabModel model = loadModel();
        for (CrontabModel.Task task : model.getTasks()) {
            if (StringUtils.equalsIgnoreCase(task.getId(), ftTimingTask.getId())) {
                return ResultDtoTool.buildError("添加失败，编号重复");
            }
        }
        CfgModelConverter.convertTo(ftTimingTask, newTask);
        model.getTasks().add(newTask);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtTimingTask ftTimingTask) {
        CrontabModel.Task updateTask = null;
        CrontabModel model = loadModel();
        for (CrontabModel.Task task : model.getTasks()) {
            if (StringUtils.equals(task.getId(), ftTimingTask.getId())) {
                updateTask = task;
                break;
            }
        }
        if (updateTask == null) return ResultDtoTool.buildError("没有找到指定的任务");
        CfgModelConverter.convertTo(ftTimingTask, updateTask);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del(FtTimingTask ftTimingTask) {
        CrontabModel model = loadModel();
        List<CrontabModel.Task> tasks = model.getTasks();
        for (CrontabModel.Task task : tasks) {
            if (StringUtils.equals(task.getId(), ftTimingTask.getId())) {
                tasks.remove(task);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    public ResultDto<String> start(FtTimingTask ftTimingTask) {
        CrontabModel model = loadModel();
        CrontabModel.Task startTask = null;
        for (CrontabModel.Task task : model.getTasks()) {
            if (StringUtils.equals(task.getId(), ftTimingTask.getId())) {
                startTask = task;
                break;
            }
        }
        if (startTask == null) return ResultDtoTool.buildError("没有找到指定的任务");
        startTask.setStatus("1");
        save(model);
        return ResultDtoTool.buildSucceed("执行成功");
    }

    public ResultDto<String> stop(FtTimingTask ftTimingTask) {
        CrontabModel model = loadModel();
        CrontabModel.Task stopTask = null;
        for (CrontabModel.Task task : model.getTasks()) {
            if (StringUtils.equals(task.getId(), ftTimingTask.getId())) {
                stopTask = task;
                break;
            }
        }
        if (stopTask == null) return ResultDtoTool.buildError("没有找到指定的任务");
        stopTask.setStatus("0");
        save(model);
        return ResultDtoTool.buildSucceed("执行成功");
    }

    private static final String CFG_FILE_NAME = "crontab.xml";

    private CrontabModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, CrontabModel.class);
    }

    private void save(CrontabModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }

    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }


    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtTimingTask ftTimingTask = (FtTimingTask)curr;
        if(isNew){
            CrontabModel.Task addTask = new CrontabModel.Task();
            CfgModelConverter.convertTo(ftTimingTask,addTask);
            return XmlBeanUtil.toXml(addTask);
        }
        CrontabModel.Task updateTask = null;
        CrontabModel model = loadModel();
        for (CrontabModel.Task task : model.getTasks()) {
            if (StringUtils.equals(task.getId(), ftTimingTask.getId())) {
                updateTask = task;
                break;
            }
        }
        if (updateTask == null) return null;
        return XmlBeanUtil.toXml(updateTask);
    }
}
