package com.dc.smarteam.service;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.VsysmapModel;
import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sysinfo.entity.Vsysmap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by mocg on 2017/3/13.
 */
@Service
public class VsysmapService {

    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<String> query(Vsysmap vsysmap, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String msg = MessageFactory.getInstance().vsysmap(vsysmap, "query");
        TCPAdapter tcpAdapter = new TCPAdapter();
        return tcpAdapter.invoke(msg, ftServiceNode, String.class);
    }

    public ResultDto<List<VsysmapModel.Map>> listAll() {
        VsysmapModel vsysmapModel = loadModel();
        return ResultDtoTool.buildSucceed(vsysmapModel.getMaps());
    }

    public ResultDto<String> add(Vsysmap vsysmap, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String msg = MessageFactory.getInstance().vsysmap(vsysmap, "add");
        TCPAdapter tcpAdapter = new TCPAdapter();
        return tcpAdapter.invoke(msg, ftServiceNode, String.class);
    }

    public ResultDto<String> add(Vsysmap vsysmap) {
        if (StringUtils.isEmpty(vsysmap.getKey()) || StringUtils.isEmpty(vsysmap.getVal())) {
            return ResultDtoTool.buildError("系统名称不能为空");
        }
        VsysmapModel.Map newMap = new VsysmapModel.Map();
        VsysmapModel model = loadModel();
        for (VsysmapModel.Map map : model.getMaps()) {
            if (StringUtils.equalsIgnoreCase(map.getSource(), vsysmap.getKey())) {
                return ResultDtoTool.buildError("添加失败，该系统映射已存在");
            }
        }
        CfgModelConverter.convertTo(vsysmap, newMap);
        model.getMaps().add(newMap);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> del(Vsysmap vsysmap, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String msg = MessageFactory.getInstance().vsysmap(vsysmap, "del");
        TCPAdapter tcpAdapter = new TCPAdapter();
        return tcpAdapter.invoke(msg, ftServiceNode, String.class);
    }

    public ResultDto<String> del(Vsysmap vsysmap) {
        VsysmapModel model = loadModel();
        List<VsysmapModel.Map> maps = model.getMaps();
        for (VsysmapModel.Map map : maps) {
            if (StringUtils.equals(map.getSource(), vsysmap.getKey())) {
                maps.remove(map);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    private static final String CFG_FILE_NAME = "vsysmap.xml";

    private VsysmapModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, VsysmapModel.class);
    }

    private void save(VsysmapModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }
}
