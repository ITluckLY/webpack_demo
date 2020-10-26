package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FlowModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.flow.entity.FtFlow;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class FlowService extends AbstractService {
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<FlowModel.Flow>> listAll() {
        FlowModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getFlows());
    }

    public ResultDto<FlowModel.Flow> selByName(FtFlow ftFlow) {
        FlowModel model = loadModel();
        FlowModel.Flow target = null;
        for (FlowModel.Flow flow : model.getFlows()) {
            if (StringUtils.equals(flow.getName(), ftFlow.getName())) {
                target = flow;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(target);
    }

    public ResultDto<List<FlowModel.Flow>> selBySysname(String sysname) {
        FlowModel model = loadModel();
        List<FlowModel.Flow> retval = new ArrayList<>();
        for (FlowModel.Flow flow : model.getFlows()) {
            if (StringUtils.equals(flow.getSysname(), sysname) || "*".equals(flow.getSysname())) {
                retval.add(flow);
            }
        }
        return ResultDtoTool.buildSucceed(retval);
    }

    public ResultDto<String> add(FtFlow ftFlow) {
        String name = ftFlow.getName();
        if (StringUtils.isEmpty(name)) {
            return ResultDtoTool.buildError("名称不能为空");
        }
        /*if (StringUtils.isEmpty(ftFlow.getComponents())) {
            return ResultDtoTools.buildError("组件不能为空");
        }*/

        FlowModel model = loadModel();
        for (FlowModel.Flow flow : model.getFlows()) {
            if (StringUtils.equalsIgnoreCase(flow.getName(), ftFlow.getName())) {
                return ResultDtoTool.buildError("添加失败，已有此组件");
            }
        }

        FlowModel.Flow target = new FlowModel.Flow();
        CfgModelConverter.convertTo(ftFlow, target);
        model.getFlows().add(target);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtFlow ftFlow) {
        FlowModel.Flow target = null;
        FlowModel model = loadModel();
        for (FlowModel.Flow flow : model.getFlows()) {
            if (StringUtils.equals(flow.getName(), ftFlow.getName())) {
                target = flow;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("没有找到指定的组件信息");
        CfgModelConverter.convertTo(ftFlow, target);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del(FtFlow ftFlow) {
        FlowModel model = loadModel();
        List<FlowModel.Flow> flows = model.getFlows();
        for (FlowModel.Flow flow : flows) {
            if (StringUtils.equals(flow.getName(), ftFlow.getName())) {
                flows.remove(flow);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    private static final String CFG_FILE_NAME = "flow.xml";

    private FlowModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, FlowModel.class);
    }

    private void save(FlowModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }


    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }

    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtFlow ftFlow = (FtFlow)curr;
        if(isNew){
            FlowModel.Flow target = new FlowModel.Flow();
            CfgModelConverter.convertTo(ftFlow,target);
            return XmlBeanUtil.toXml(target);
        }
        FlowModel.Flow target = null;
        FlowModel model = loadModel();
        for (FlowModel.Flow flow : model.getFlows()) {
            if (StringUtils.equals(flow.getName(), ftFlow.getName())) {
                target = flow;
                break;
            }
        }
        if (target == null) return null;
        return XmlBeanUtil.toXml(target);

    }
}
