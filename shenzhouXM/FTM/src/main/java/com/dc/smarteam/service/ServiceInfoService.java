package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.base.UpdateEntity;
import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.ServiceModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class ServiceInfoService extends AbstractService {
    @Resource
    private CfgFileService cfgFileService;
    @Resource
    private UserService userService;

    public ResultDto<List<ServiceModel.Service>> listAll() {
        ServiceModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getServices());
    }

    public ResultDto<ServiceModel.Service> selByTrancodeAndSysname(FtServiceInfo ftServiceInfo) {
        ServiceModel model = loadModel();
        ServiceModel.Service target = null;
        for (ServiceModel.Service service : model.getServices()) {
            if (StringUtils.equals(service.getTrancode(), ftServiceInfo.getTrancode())
                    && StringUtils.equalsIgnoreCase(service.getSysname(), ftServiceInfo.getSystemName())) {
                target = service;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(target);
    }

    public ResultDto<ServiceModel.Service> selByTrancodeAndGetAuth(FtServiceInfo ftServiceInfo) {
        ServiceModel model = loadModel();
        ServiceModel.Service target = null;
        for (ServiceModel.Service service : model.getServices()) {
            if(StringUtils.equals(service.getTrancode(), ftServiceInfo.getTrancode())){
                List<ServiceModel.AuthUser>  authUserList = service.getGetAuth().getUsers();
                for (int i = 0; i < authUserList.size(); i++){
                String user = authUserList.get(i).getUser().toString();
                if (user.equals(ftServiceInfo.getSystemName())) {
                    target = service;
                    break;
                }
             }
            }
        }
        if (target == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(target);
    }


    public ResultDto<String> add(FtServiceInfo ftServiceInfo) {
        String trancode = ftServiceInfo.getTrancode();
        if (StringUtils.isEmpty(trancode)) {
            return ResultDtoTool.buildError("交易码不能为空");
        }
        if (StringUtils.isEmpty(ftServiceInfo.getSystemName())) {
            return ResultDtoTool.buildError("系统名称不能为空");
        }

        ServiceModel model = loadModel();
        for (ServiceModel.Service service : model.getServices()) {
            if (StringUtils.equalsIgnoreCase(service.getTrancode(), ftServiceInfo.getTrancode())) {
                return ResultDtoTool.buildError("添加失败，已有此交易码");
            }
        }

        ServiceModel.Service target = new ServiceModel.Service();
        CfgModelConverter.convertToWithoutAuth(ftServiceInfo, target);
        model.getServices().add(target);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtServiceInfo ftServiceInfo) {
        ServiceModel.Service target = null;
        ServiceModel model = loadModel();
        for (ServiceModel.Service service : model.getServices()) {
            if (StringUtils.equals(service.getTrancode(), ftServiceInfo.getTrancode())) {
                target = service;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("没有找到指定的交易码信息");
        CfgModelConverter.convertToWithoutAuth(ftServiceInfo, target);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del(FtServiceInfo ftServiceInfo) {
        ServiceModel model = loadModel();
        List<ServiceModel.Service> services = model.getServices();
        for (ServiceModel.Service service : services) {
            if (StringUtils.equals(service.getTrancode(), ftServiceInfo.getTrancode())) {
                services.remove(service);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    public ResultDto<String> del(FtRoute ftRoute) {
        ServiceModel model = loadModel();
        List<ServiceModel.Service> services = model.getServices();
        for (ServiceModel.Service service : services) {
            if (StringUtils.equals(service.getTrancode(), ftRoute.getTran_code())) {
                services.remove(service);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    @UpdateEntity
    public ResultDto<String> savePutAuth(FtServiceInfo ftServiceInfo, PutAuthEntity putAuthEntity) {
        ServiceModel model = loadModel();
        List<ServiceModel.Service> services = model.getServices();
        ServiceModel.Service targetService = null;
        for (ServiceModel.Service service : services) {
            if (StringUtils.equals(service.getTrancode(), putAuthEntity.getTrancode())) {
                targetService = service;
                break;
            }
        }
        if (targetService == null) return ResultDtoTool.buildError("不存在交易码");
        ServiceModel.PutAuth putAuth = targetService.getPutAuth();
        if (putAuth == null) {
            putAuth = new ServiceModel.PutAuth();
            targetService.setPutAuth(putAuth);
        }
        List<ServiceModel.AuthUser> users = putAuth.getUsers();
        if (users == null) {
            users = new ArrayList<>();
            putAuth.setUsers(users);
        }
        ServiceModel.AuthUser authUser = new ServiceModel.AuthUser();
        authUser.setUser(putAuthEntity.getUserName());
        authUser.setDirectoy(putAuthEntity.getDirectoy());
        if (!users.contains(authUser)) users.add(authUser);
        ftServiceInfo.setTrancode(putAuthEntity.getTrancode());
        save(model);
        return ResultDtoTool.buildSucceed("操作成功");
    }

    @UpdateEntity
    public ResultDto<String> delPutAuth(FtServiceInfo ftServiceInfo, PutAuthEntity putAuthEntity) {
        ServiceModel model = loadModel();
        List<ServiceModel.Service> services = model.getServices();
        ServiceModel.Service targetService = null;
        for (ServiceModel.Service service : services) {
            if (StringUtils.equals(service.getTrancode(), putAuthEntity.getTrancode())) {
                targetService = service;
                break;
            }
        }
        if (targetService == null) return ResultDtoTool.buildError("不存在交易码");
        ServiceModel.PutAuth putAuth = targetService.getPutAuth();
        if (putAuth == null) {
            putAuth = new ServiceModel.PutAuth();
            targetService.setPutAuth(putAuth);
        }
        List<ServiceModel.AuthUser> users = putAuth.getUsers();
        if (users == null) {
            users = new ArrayList<>();
            putAuth.setUsers(users);
        }
        ServiceModel.AuthUser authUser = new ServiceModel.AuthUser();
        authUser.setUser(putAuthEntity.getUserName());
        authUser.setDirectoy(putAuthEntity.getDirectoy());
        users.remove(authUser);

        save(model);
        return ResultDtoTool.buildSucceed("操作成功");
    }

    @UpdateEntity
    public ResultDto<String> saveGetAuth(FtServiceInfo ftServiceInfo, GetAuthEntity getAuthEntity) {
        ServiceModel model = loadModel();
        List<ServiceModel.Service> services = model.getServices();
        ServiceModel.Service targetService = null;
        for (ServiceModel.Service service : services) {
            if (StringUtils.equals(service.getTrancode(), getAuthEntity.getTrancode())) {
                targetService = service;
                break;
            }
        }
        if (targetService == null) return ResultDtoTool.buildError("不存在交易码");
        ServiceModel.GetAuth getAuth = targetService.getGetAuth();
        if (getAuth == null) {
            getAuth = new ServiceModel.GetAuth();
            targetService.setGetAuth(getAuth);
        }
        List<ServiceModel.AuthUser> users = getAuth.getUsers();
        if (users == null) {
            users = new ArrayList<>();
            getAuth.setUsers(users);
        }
        ServiceModel.AuthUser authUser = new ServiceModel.AuthUser();
        authUser.setUser(getAuthEntity.getUserName());
        authUser.setDirectoy(null);
        if (!users.contains(authUser)) users.add(authUser);

        save(model);
        return ResultDtoTool.buildSucceed("操作成功");
    }

    @UpdateEntity
    public ResultDto<String> delGetAuth(FtServiceInfo ftServiceInfo, GetAuthEntity getAuthEntity) {
        ServiceModel model = loadModel();
        List<ServiceModel.Service> services = model.getServices();
        ServiceModel.Service targetService = null;
        for (ServiceModel.Service service : services) {
            if (StringUtils.equals(service.getTrancode(), getAuthEntity.getTrancode())) {
                targetService = service;
                break;
            }
        }
        if (targetService == null) return ResultDtoTool.buildError("不存在交易码");
        ServiceModel.GetAuth getAuth = targetService.getGetAuth();
        if (getAuth == null) {
            getAuth = new ServiceModel.GetAuth();
            targetService.setGetAuth(getAuth);
        }
        List<ServiceModel.AuthUser> users = getAuth.getUsers();
        if (users == null) {
            users = new ArrayList<>();
            getAuth.setUsers(users);
        }
        ServiceModel.AuthUser authUser = new ServiceModel.AuthUser();
        authUser.setUser(getAuthEntity.getUserName());
        authUser.setDirectoy(null);
        users.remove(authUser);

        save(model);
        return ResultDtoTool.buildSucceed("操作成功");
    }

    public ResultDto<String> serviceInfoExport() {//NOSONAR
        ResultDto<List<ServiceModel.Service>> resultDto = listAll();
        if (!ResultDtoTool.isSuccess(resultDto)) {
            return ResultDtoTool.buildError("交易码查询出错");
        }
        List<ServiceModel.Service> modelist = resultDto.getData();
        StringBuilder sb = new StringBuilder("交易码," + "系统," + "文件生产方," + "文件消费方," + "交易码描述");
        UserModel userModel = loadUserModel();
        for (ServiceModel.Service service : modelist) {
            String tranCode = service.getTrancode();
            String desc = service.getDescribe();

            ServiceModel.PutAuth putAuth = service.getPutAuth();
            StringBuilder putauthsys = new StringBuilder(); //文件生产方
            if (null != putAuth && null != putAuth.getUsers()) {
                for (ServiceModel.AuthUser user : putAuth.getUsers()) {
                    FtUser ftUser = new FtUser();
                    ftUser.setName(user.getUser());
                    String userdes = ResultDtoTool.isSuccess(userService.selloadedByName(ftUser,userModel)) ? userService.selloadedByName(ftUser,userModel).getData().getUid().getDescribe() : "";
                    putauthsys.append(" (").append(user.getUser()).append(")").append(userdes);
                }
            }

            ServiceModel.GetAuth getAuth = service.getGetAuth();//文件消费方
            if (null != getAuth && null != getAuth.getUsers()) {
                for (ServiceModel.AuthUser user : getAuth.getUsers()) {
                    FtUser ftUser = new FtUser();
                    ftUser.setName(user.getUser());
                    String userdes = ResultDtoTool.isSuccess(userService.selloadedByName(ftUser,userModel)) ? userService.selloadedByName(ftUser,userModel).getData().getUid().getDescribe() : "";
                    String getauth = "(" + user.getUser() + ")" + userdes;
                    sb.append("\r\n");
                    sb.append(tranCode).append(",")
                            .append(service.getSysname()).append(",")
                            .append(putauthsys).append(",")
                            .append(getauth).append(",")
                            .append(desc);
                }
            }
        }
        return ResultDtoTool.buildSucceed(sb.toString());
    }

    public List<FtServiceInfo> getFtServiceInfoList() {
        List<FtServiceInfo> ftServiceInfoList = new ArrayList<>();
        ResultDto<List<ServiceModel.Service>> resultDto = listAll();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<ServiceModel.Service> services = resultDto.getData();
            for (ServiceModel.Service service : services) {
                FtServiceInfo ftServiceInfo = new FtServiceInfo();
                CfgModelConverter.convertToWithoutAuth(service, ftServiceInfo);
                ftServiceInfo.setId(String.valueOf(ftServiceInfoList.size()));
                ftServiceInfoList.add(ftServiceInfo);
            }
        }
        return ftServiceInfoList;
    }

    private static final String CFG_FILE_NAME = "services_info.xml";

    public ServiceModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, ServiceModel.class);
    }

    private void save(ServiceModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }

    private static final String CFG_USER_FILE_NAME = "user.xml";

    private UserModel loadUserModel() {
        return cfgFileService.loadModel4Name(CFG_USER_FILE_NAME, UserModel.class);
    }

    private void saveUser(UserModel model) {
        cfgFileService.saveModel4Name(CFG_USER_FILE_NAME, model);
    }


    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }


    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtServiceInfo ftServiceInfo = (FtServiceInfo)curr;
        if(isNew){
            ServiceModel.Service target = new ServiceModel.Service();
            CfgModelConverter.convertToWithoutAuth(ftServiceInfo,target);
            return XmlBeanUtil.toXml(target);
        }
        ServiceModel.Service target = null;
        ServiceModel model = loadModel();
        for (ServiceModel.Service service : model.getServices()) {
            if (StringUtils.equals(service.getTrancode(), ftServiceInfo.getTrancode())) {
                target = service;
                break;
            }
        }
        if(target==null)return null;
        return XmlBeanUtil.toXml(target);
    }


    private boolean sameUser(String userName, List<ServiceModel.AuthUser> authUserList){
        if(userName==null||userName.length()<5||authUserList.isEmpty()){
            logger.error("new sysname length less than 5");
            return false;
        }
        for(ServiceModel.AuthUser authUser:authUserList){
            String temp = authUser.getUser();
            if(temp==null||"".equals(temp)){
                return false;
            }
            if(authUser.getUser().equals(userName)){
                return true;
            }
        }
        return false;
    }

    public List<String> selAllTranCodeBySameSys(String userName){
        List<String> ftServiceInfoList = new ArrayList<>();
        ServiceModel model = loadModel();
        for(ServiceModel.Service service:model.getServices()){
            ServiceModel.GetAuth getAuth = service.getGetAuth();
            if(getAuth==null||getAuth.getUsers()==null)continue;
            if(sameUser(userName,getAuth.getUsers())){
                ftServiceInfoList.add(service.getTrancode());
            }
        }
        return ftServiceInfoList;
    }

}
