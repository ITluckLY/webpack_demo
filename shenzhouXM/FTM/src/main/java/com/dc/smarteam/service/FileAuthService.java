package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FileModel;
import com.dc.smarteam.cfgmodel.NettyModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.model.UserAuthModel;
import com.dc.smarteam.util.EmptyUtils;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class FileAuthService extends AbstractService {
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<FileModel.BaseFile>> listAll() {
        FileModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getBaseFiles());
    }
    public ResultDto<List<NettyModel.Prarm>> nettylistAll() {
        NettyModel model = loadNettyModel();
//        return ResultDtoTool.buildSucceed(model.getNettyConfig());
    return null;
    }
    public ResultDto<FileModel.BaseFile> selByUserAndName(UserAuthModel userAuth) {
        FileModel model = loadModel();
        FileModel.BaseFile target = null;
        out:
        for (FileModel.BaseFile baseFile : model.getBaseFiles()) {
            if (StringUtils.equalsIgnoreCase(baseFile.getName(), userAuth.getPath())) {
                List<FileModel.Grant> grants = baseFile.getGrants();
                if (grants == null) break;
                for (FileModel.Grant grant : grants) {
                    if (StringUtils.equals(grant.getUser(), userAuth.getUserName())) {
                        target = baseFile;
                        break out;
                    }
                }
            }
        }
        if (target == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(target);
    }

    public ResultDto<String> add(UserAuthModel userAuth) {
        if (StringUtils.isEmpty(userAuth.getUserName())) {
            return ResultDtoTool.buildError("用户名不能为空");
        }
        if (StringUtils.isEmpty(userAuth.getPath())) {
            return ResultDtoTool.buildError("路径不能为空");
        }
        if (!StringUtils.startsWith(userAuth.getPath(), "/")) {
            return ResultDtoTool.buildError("路径要以/开头");
        }
        if (StringUtils.isEmpty(userAuth.getAuth())) {
            return ResultDtoTool.buildError("权限不能为空");
        }

        FileModel model = loadModel();
        FileModel.BaseFile targetBaseFile = null;
        FileModel.Grant target = null;
        out:
        for (FileModel.BaseFile baseFile : model.getBaseFiles()) {
            if (StringUtils.equalsIgnoreCase(baseFile.getName(), userAuth.getPath())) {
                targetBaseFile = baseFile;
                List<FileModel.Grant> grants = baseFile.getGrants();
                if (grants == null) break;
                for (FileModel.Grant grant : grants) {
                    if (StringUtils.equals(grant.getUser(), userAuth.getUserName())) {
                        target = grant;
                        break out;
                    }
                }
            }
        }
        if (target != null) return ResultDtoTool.buildError("添加失败，用户已配置该目录权限");
        if (targetBaseFile == null) {
            targetBaseFile = new FileModel.BaseFile();
            targetBaseFile.setName(userAuth.getPath());
            targetBaseFile.setPath(userAuth.getPath().substring(1));
            model.getBaseFiles().add(targetBaseFile);
        }
        List<FileModel.Grant> grants = targetBaseFile.getGrants();
        if (grants == null) {
            grants = new ArrayList<>();
            targetBaseFile.setGrants(grants);
        }
        FileModel.Grant grant = new FileModel.Grant();
        grant.setUser(userAuth.getUserName());
        grant.setType(userAuth.getAuth());
        grants.add(grant);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    // 添加netty
    public ResultDto<String> addNetty(UserAuthModel userAuth) {
        if (StringUtils.isEmpty(userAuth.getUserName())) {
            return ResultDtoTool.buildError("用户名不能为空");
        }
        if (StringUtils.isEmpty(userAuth.getReadLimit())) {
            return ResultDtoTool.buildError("readLimit 不能为空！");
        }
        if (StringUtils.isEmpty(userAuth.getWriteLimit())) {
            return ResultDtoTool.buildError("writeLimit 不能为空！");
        }

    /*    NettyModel model = loadNettyModel();
        NettyModel.Prarm targetBaseFile = null;
        NettyModel.ASB target = null;
        out:
        for (NettyModel.Prarm baseFile : model.getPrarmList()) {
            if (!baseFile.getUserName().isEmpty()) {
                targetBaseFile = baseFile;
            }
        }
        if (target != null) return ResultDtoTool.buildError("添加失败!!!");
        if (targetBaseFile == null) {
            targetBaseFile = new NettyModel.Prarm();
            targetBaseFile.setUserName(userAuth.getUserName());
            targetBaseFile.setMaxsPeed(userAuth.getMaxsPeed());
            targetBaseFile.setSleepTime(userAuth.getSleepTime());
            targetBaseFile.setScanTime(userAuth.getScanTime());
            model.getPrarmList().add(targetBaseFile);
        }
        // NettyModel.ASB grants = targetBaseFile.getAsb();
      //  if (grants == null) {
            //targetBaseFile.setAsb(grants);
       // }
        NettyModel.ASB grant = new NettyModel.ASB();
        grant.setReadLimit(userAuth.getReadLimit());
        grant.setWriteLimit(userAuth.getWriteLimit());
        //targetBaseFile.setAsb(grant);
        //model.getPrarmList().add(targetBaseFile);
         savenetty(model);*/
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> del(UserAuthModel userAuth) {
        FileModel model = loadModel();
        List<FileModel.BaseFile> baseFiles = model.getBaseFiles();
        FileModel.BaseFile targetBaseFile = null;
        out:
        for (FileModel.BaseFile baseFile : baseFiles) {
            if (StringUtils.equalsIgnoreCase(baseFile.getName(), userAuth.getPath())) {
                targetBaseFile = baseFile;
                List<FileModel.Grant> grants = baseFile.getGrants();
                if (grants == null) break;
                for (FileModel.Grant grant : grants) {
                    if (StringUtils.equals(grant.getUser(), userAuth.getUserName())) {
                        grants.remove(grant);
                        break out;
                    }
                }
            }
        }
        if (targetBaseFile != null && EmptyUtils.isEmpty(targetBaseFile.getGrants())) {
            baseFiles.remove(targetBaseFile);
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    private static final String CFG_FILE_NAME = "file.xml";
    private static final String NFT_FILE_NAME = "netty.xml";

    private FileModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, FileModel.class);
    }
    private void save(FileModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }

    private NettyModel loadNettyModel() {
        return cfgFileService.loadModel4Name(NFT_FILE_NAME, NettyModel.class);
    }
    private void savenetty(NettyModel model) {
        cfgFileService.saveModel4Name(NFT_FILE_NAME, model);
    }

    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }
    public static String getNftFileName() {
        return NFT_FILE_NAME;
    }
    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        UserAuthModel userAuth = (UserAuthModel)curr;
        if(isNew){
            FileModel.BaseFile targetBaseFile = new FileModel.BaseFile();
            CfgModelConverter.convertTo(userAuth,targetBaseFile);
            return XmlBeanUtil.toXml(targetBaseFile);
        }
        FileModel model = loadModel();
        List<FileModel.BaseFile> baseFiles = model.getBaseFiles();
        FileModel.BaseFile targetBaseFile = null;
        boolean flag = false;
        out:
        for (FileModel.BaseFile baseFile : baseFiles) {
            if (StringUtils.equalsIgnoreCase(baseFile.getName(), userAuth.getPath())) {
                targetBaseFile = baseFile;
                List<FileModel.Grant> grants = baseFile.getGrants();
                if (grants == null) break;
                for (FileModel.Grant grant : grants) {
                    if (StringUtils.equals(grant.getUser(), userAuth.getUserName())) {
                        flag = true;
                        break out;
                    }
                }
            }
        }
        if(flag){
            //存在前置对象
            return XmlBeanUtil.toXml(targetBaseFile);
        }
        return null;
    }

}
