package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.KeysModel;
import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.keys.entity.FtKey;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaona
 * 2019/12/05
 * 公私钥管理
 */

@Service
public class KeyService extends AbstractService{
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<KeysModel.Key>> listAll() {
        KeysModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getKeys());
    }

    public ResultDto<String> add(FtKey ftKey) {
        String user = ftKey.getUser();
        String type = ftKey.getType();
        String content = ftKey.getContent();
        if (StringUtils.isEmpty(user)) {
            return ResultDtoTool.buildError("用户名不能为空");
        }
        if (StringUtils.isEmpty(type)) {
            return ResultDtoTool.buildError("秘钥类型不能为空");
        }
        if (StringUtils.isEmpty(content)) {
            return ResultDtoTool.buildError("秘钥内容不能为空");
        }
        KeysModel model = loadModel();
        for (KeysModel.Key key : model.getKeys()) {
            if (StringUtils.equalsIgnoreCase(key.getType(), type)
                    && StringUtils.equalsIgnoreCase(key.getUser(), user)) {
                return ResultDtoTool.buildError("添加失败，该用户已有此类型秘钥");
            }
        }
        KeysModel.Key key = new KeysModel.Key();
        CfgModelConverter.convertTo(key,ftKey);
        model.getKeys().add(key);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> del(FtKey ftKey) {
        KeysModel model = loadModel();
        List<KeysModel.Key> keys = model.getKeys();
        for (KeysModel.Key key : keys) {
            if (StringUtils.equals(key.getUser(), ftKey.getUser())
                    && StringUtils.equalsIgnoreCase(key.getType(), ftKey.getType())) {
                keys.remove(key);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    public ResultDto<String> update(FtKey ftKey) {
        KeysModel.Key target = null;
        KeysModel model = loadModel();
        for (KeysModel.Key key : model.getKeys()) {
            if (StringUtils.equals(key.getUser(), ftKey.getUser())
                    && StringUtils.equalsIgnoreCase(key.getType(), ftKey.getType())) {
                target = key;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("没有找到指定的秘钥");
        CfgModelConverter.convertTo(target,ftKey);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<KeysModel.Key> selByUser(FtKey ftKey) {
        KeysModel keysModel = loadModel();
        KeysModel.Key key = null;
        for (KeysModel.Key key1 : keysModel.getKeys()) {
            if (StringUtils.equals(key1.getUser(), ftKey.getUser())) {
                key = key1;
                break;
            }
        }
        if (key == null) return ResultDtoTool.buildError("没有找到指定的用户");
        return ResultDtoTool.buildSucceed(key);
    }

    private static final String CFG_FILE_NAME = "keys.xml";

    private KeysModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, KeysModel.class);
    }
    public void save(KeysModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }

    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }

    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtKey ftKey = (FtKey) curr;
        if(isNew){
            KeysModel.Key target = new KeysModel.Key();
            CfgModelConverter.convertTo(target,ftKey);
            return XmlBeanUtil.toXml(target);
        }
        KeysModel.Key target = null;
        KeysModel model = loadModel();
        for (KeysModel.Key key : model.getKeys()) {
            if (StringUtils.equals(key.getType(), ftKey.getType())
                    && StringUtils.equalsIgnoreCase(key.getUser(), ftKey.getUser())) {
                target = key;
                break;
            }
        }
        if (target == null) return null;
        return XmlBeanUtil.toXml(target);
    }
}
