package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.ClientStatusModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.client.entity.FtClientStatus;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xuchuang on 2018/6/13.
 */
@Service
public class ClientStatusService extends AbstractService {

    @Resource
    private CfgFileService cfgFileService;

    private static final String CFG_FILE_NAME = "client_status.xml";
    private ClientStatusModel loadModel(){
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, ClientStatusModel.class);
    }

    private void save(ClientStatusModel model) {
        try{
            cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public ResultDto<List<ClientStatusModel.Client>> listAll(){
        ClientStatusModel clientStatusModel = loadModel();
        return ResultDtoTool.buildSucceed(clientStatusModel.getClients());
    }

    public ResultDto<List<ClientStatusModel.Client>> listBySel(FtClientStatus ftClientStatus) {
        List<ClientStatusModel.Client> list = new ArrayList<>();
        ClientStatusModel model = loadModel();
        for(ClientStatusModel.Client client:model.getClients()){
            FtClientStatus clientStatus = new FtClientStatus();
            CfgModelConverter.convertTo(client,clientStatus);
            if(clientStatus.isSel(ftClientStatus)){
                list.add(client);
            }
        }
        return ResultDtoTool.buildSucceed(list);
    }


    public ResultDto<String> add(FtClientStatus ftClientStatus){
        if(ftClientStatus.getId()==null||"".equals(ftClientStatus.getId())){
            return ResultDtoTool.buildError("信息不完整");
        }
        ClientStatusModel model = loadModel();
        for(ClientStatusModel.Client client:model.getClients()){
            if(client.getId().equals(ftClientStatus.getId())){
                return ResultDtoTool.buildError("对象已存在,添加失败");
            }
        }
        ClientStatusModel.Client target = new ClientStatusModel.Client();
        CfgModelConverter.convertTo(ftClientStatus,target);
        model.getClients().add(target);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtClientStatus ftClientStatus){
        if(ftClientStatus.getId()==null||"".equals(ftClientStatus.getId())){
            return ResultDtoTool.buildError("信息不完整");
        }
        ClientStatusModel model = loadModel();
        ClientStatusModel.Client target = null;
        for(ClientStatusModel.Client client:model.getClients()){
            if(ftClientStatus.getId().equals(client.getId())){
                target = client;
                break;
            }
        }
        if(target==null){
            return ResultDtoTool.buildError("没有找到对应的客户端");
        }
        CfgModelConverter.convertTo(ftClientStatus,target);
        save(model);
        return ResultDtoTool.buildSucceed("修改成功");
    }

    public ResultDto<String> del(FtClientStatus ftClientStatus){

        if(ftClientStatus.getId()==null||"".equals(ftClientStatus.getId())){
            return ResultDtoTool.buildError("信息不完整");
        }
        boolean flag = false;
        ClientStatusModel model = loadModel();
        for(Iterator<ClientStatusModel.Client> iter = model.getClients().iterator(); iter.hasNext();){
            ClientStatusModel.Client client = iter.next();
            if(client.getId().equals(ftClientStatus.getId())){
                iter.remove();
                flag = true;
                break;
            }
        }
        if(flag){
            save(model);
            return ResultDtoTool.buildSucceed("删除成功");
        }else{
            return ResultDtoTool.buildError("不存在的对象,删除失败");
        }

    }

    public ResultDto<ClientStatusModel.Client> selById(FtClientStatus ftClientStatus){
        if(ftClientStatus.getId()==null||"".equals(ftClientStatus.getId())){
            return ResultDtoTool.buildError("信息不完整");
        }
        ClientStatusModel model = loadModel();
        ClientStatusModel.Client target = null;

        for(ClientStatusModel.Client client:model.getClients()){
            if(client.getId().equals(ftClientStatus.getId())){
                target = client;
                break;
            }
        }
        if(target==null){
            return ResultDtoTool.buildError("对象不存在");
        }
        return ResultDtoTool.buildSucceed(target);
    }

    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }

    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtClientStatus ftClientStatus = (FtClientStatus)curr;
        if(isNew){
            ClientStatusModel.Client client = new ClientStatusModel.Client();
            CfgModelConverter.convertTo(ftClientStatus,client);
            return XmlBeanUtil.toXml(client);
        }
        ClientStatusModel model = loadModel();
        ClientStatusModel.Client target = null;
        for(ClientStatusModel.Client client:model.getClients()){
            if(client.getId().equals(ftClientStatus.getId())){
                target = client;
                break;
            }
        }
        if(target==null){
            return null;
        }
        return XmlBeanUtil.toXml(target);
    }

}
