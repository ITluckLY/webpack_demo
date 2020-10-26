package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.ComponentModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.component.entity.FtComponent;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class ComponentService extends AbstractService {
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<ComponentModel.Service>> select(FtComponent ftComponent, HttpServletRequest request) {
        ComponentModel model = loadModel();
        List<ComponentModel.Service> services = model.getServices();
        return ResultDtoTool.buildSucceed(services);
    }

    public ResultDto<List<ComponentModel.Service>> listAll() {
        ComponentModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getServices());
    }

    public ResultDto<ComponentModel.Service> selByName(FtComponent ftComponent) {
        ComponentModel model = loadModel();
        ComponentModel.Service service = null;
        for (ComponentModel.Service service2 : model.getServices()) {
            if (StringUtils.equals(service2.getName(), ftComponent.getName())) {
                service = service2;
                break;
            }
        }
        if (service == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(service);
    }

    public ResultDto<String> add(FtComponent ftComponent) {
        String name = ftComponent.getName();
        if (StringUtils.isEmpty(name)) {
            return ResultDtoTool.buildError("名称不能为空");
        }
        if (StringUtils.isEmpty(ftComponent.getImplement())) {
            return ResultDtoTool.buildError("生成类不能为空");
        }

        ComponentModel.Service newService = new ComponentModel.Service();
        ComponentModel model = loadModel();
        for (ComponentModel.Service service : model.getServices()) {
            if (StringUtils.equalsIgnoreCase(service.getName(), ftComponent.getName())) {
                return ResultDtoTool.buildError("添加失败，已有此组件");
            }
        }

        CfgModelConverter.convertTo(ftComponent, newService);
        model.getServices().add(newService);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtComponent ftComponent) {
        ComponentModel.Service updateService = null;
        ComponentModel model = loadModel();
        for (ComponentModel.Service service : model.getServices()) {
            if (StringUtils.equals(service.getName(), ftComponent.getName())) {
                updateService = service;
                break;
            }
        }
        if (updateService == null) return ResultDtoTool.buildError("没有找到指定的组件信息");
        CfgModelConverter.convertTo(ftComponent, updateService);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del(FtComponent ftComponent) {
        ComponentModel model = loadModel();
        List<ComponentModel.Service> services = model.getServices();
        for (ComponentModel.Service service : services) {
            if (StringUtils.equals(service.getName(), ftComponent.getName())) {
                services.remove(service);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    private static final String CFG_FILE_NAME = "components.xml";

    private ComponentModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, ComponentModel.class);
    }

    private void save(ComponentModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }


    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }

    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtComponent ftComponent = (FtComponent)curr;
        if(isNew){
            ComponentModel.Service addService = new ComponentModel.Service();
            CfgModelConverter.convertTo(ftComponent,addService);
            return XmlBeanUtil.toXml(addService);
        }
        ComponentModel.Service updateService = null;
        ComponentModel model = loadModel();
        for (ComponentModel.Service service : model.getServices()) {
            if (StringUtils.equals(service.getName(), ftComponent.getName())) {
                updateService = service;
                break;
            }
        }
        if (updateService == null) return null;
        return XmlBeanUtil.toXml(updateService);
    }

}
