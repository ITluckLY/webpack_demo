package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.util.XmlBeanUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by huangzbb on 2017/5/8.
 */
@Service
public class SysService extends AbstractService {

    Logger log = LoggerFactory.getLogger(SysService.class);

    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<SystemModel.System>> select(SysProtocol sysProtocol, HttpServletRequest request) {
        SystemModel model = loadModel();
        List<SystemModel.System> systems = model.getSystems();
        return ResultDtoTool.buildSucceed(systems);
    }

    public ResultDto<List<SystemModel.System>> listAll() {
        SystemModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getSystems());
    }

    public ResultDto<SystemModel.System> selByName(SysProtocol sysProtocol) {
        SystemModel model = loadModel();
        SystemModel.System system = null;
        for (SystemModel.System system2 : model.getSystems()) {
            if (StringUtils.equals(system2.getName(), sysProtocol.getName())) {
                system = system2;
                break;
            }
        }
        if (system == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(system);
    }

    public ResultDto<SystemModel.System> selByName(ClientSyn clientSyn) {
        SystemModel model = loadModel();
        SystemModel.System system = null;
        for (SystemModel.System system2 : model.getSystems()) {
            if (StringUtils.equals(system2.getName(), clientSyn.getName())) {
                system = system2;
                break;
            }
        }
        if (system == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(system);
    }

    public ResultDto<List<SystemModel.System>> selByuserName(SysProtocol sysProtocol) {
        SystemModel model = loadModel();
        SystemModel.System system = null;
        List<SystemModel.System> systemList = new ArrayList<>();
        for (SystemModel.System system2 : model.getSystems()) {
            if (StringUtils.equals(system2.getUsername(), sysProtocol.getUsername())) {
                system = system2;
                systemList.add(system);
            }
        }
        return ResultDtoTool.buildSucceed(systemList);
    }

    public ResultDto<String> add(SysProtocol sysProtocol) {
        if (StringUtils.isEmpty(sysProtocol.getName())) {
            return ResultDtoTool.buildError("系统名称不能为空");
        }
        if (StringUtils.isEmpty(sysProtocol.getIp())) {
            return ResultDtoTool.buildError("IP不能为空");
        }
        if (StringUtils.isEmpty(sysProtocol.getPort())) {
            return ResultDtoTool.buildError("端口不能为空");
        }
        if (StringUtils.isEmpty(sysProtocol.getUsername())) {
            return ResultDtoTool.buildError("用户名不能为空");
        }
        if (StringUtils.isEmpty(sysProtocol.getPassword())) {
            return ResultDtoTool.buildError("密码不能为空");
        }

        SystemModel.System newSystem = new SystemModel.System();
        SystemModel model = loadModel();
        for (SystemModel.System system : model.getSystems()) {
            if (StringUtils.equalsIgnoreCase(system.getName(), sysProtocol.getName())) {
                return ResultDtoTool.buildError("添加失败，已有此系统");
            }
        }

        CfgModelConverter.convertTo(sysProtocol, newSystem);
        model.getSystems().add(newSystem);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

//    public ResultDto<JSONObject> test(FtServiceInfo ftServiceInfo) {
//
//        if (StringUtils.isEmpty(ftServiceInfo.getTrancode())) {
//            return ResultDtoTool.buildError("serviceid不能为空");
//        }
//        if (StringUtils.isEmpty(ftServiceInfo.getSystemName())){
//        return ResultDtoTool.buildError("userid不能为空");
//        }
//        String data = " [{ \"filename\":\"/fts0000001/fts01/test02.txt\" }]";
//        JSONObject jsonObject;
//        jsonObject =JSONObject.fromObject(data);
//        return ResultDtoTool.buildSucceed(jsonObject);
//    }


    public ResultDto<String> update(SysProtocol sysProtocol) {
        SystemModel.System updateSystem = null;
        SystemModel model = loadModel();
        for (SystemModel.System system : model.getSystems()) {
            if (StringUtils.equals(system.getName(), sysProtocol.getName())) {
                updateSystem = system;
                break;
            }
        }
        if (updateSystem == null) return ResultDtoTool.buildError("没有找到指定的系统信息");
        CfgModelConverter.convertTo(sysProtocol, updateSystem);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }


    public ResultDto<String> del(SysProtocol sysProtocol) {
        SystemModel model = loadModel();
        List<SystemModel.System> systems = model.getSystems();
        for (SystemModel.System system : systems) {
            if (StringUtils.equals(system.getName(), sysProtocol.getName())) {
                systems.remove(system);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }
    public ResultDto<String> del(ClientSyn clientSyn) {
        SystemModel model = loadModel();
        List<SystemModel.System> systems = model.getSystems();
        for (SystemModel.System system : systems) {
            if (StringUtils.equals(system.getName(), clientSyn.getName())) {
                systems.remove(system);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    private static final String CFG_FILE_NAME = "system.xml";

    public SystemModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, SystemModel.class);
    }

    private void save(SystemModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }


//    @Override
//    public CfgData getPreData(CfgData curr) {
//        SysProtocol sysProtocol = (SysProtocol) curr;
//        SystemModel.System updateSystem = null;
//        SystemModel model = loadModel();
//        for (SystemModel.System system : model.getSystems()) {
//            if (StringUtils.equals(system.getName(), sysProtocol.getName())) {
//                updateSystem = system;
//                break;
//            }
//        }
//        if (updateSystem == null){
//            log.error("没有找到指定的系统信息");
//            return null;
//        }
//        SysProtocol preSysProtocol = new SysProtocol();
//        log.info("old sys name：{},{}",updateSystem.getName(),updateSystem.getIp());
//        CfgModelConverter.convertTo(updateSystem,preSysProtocol);
//        return preSysProtocol;
//    }

    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }

    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        SysProtocol sysProtocol = (SysProtocol)curr;
        if(isNew){
            SystemModel.System system = new SystemModel.System();
            CfgModelConverter.convertTo(sysProtocol,system);
            return XmlBeanUtil.toXml(system);
        }
        SystemModel.System updateSystem = null;
        SystemModel model = loadModel();
        for (SystemModel.System system : model.getSystems()) {
            if (StringUtils.equals(system.getName(), sysProtocol.getName())) {
                updateSystem = system;
                break;
            }
        }
        if (updateSystem == null){
            log.error("没有找到指定的系统信息");
            return null;
        }
        log.info("old sys name：{},{}",updateSystem.getName(),updateSystem.getIp());
        return XmlBeanUtil.toXml(updateSystem);

    }
}
