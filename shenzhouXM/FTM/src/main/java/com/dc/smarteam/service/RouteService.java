package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.cfgmodel.ServiceModel;
import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.util.CollectionUtil;
import com.dc.smarteam.util.XmlBeanUtil;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzbb on 2017/5/9.
 */
@Service
public class RouteService extends AbstractService {

    @Resource
    private CfgFileService cfgFileService;
    @Resource
    private ServiceInfoService serviceInfoService;
    @Resource
    private SysService sysService;

    public ResultDto<List<RouteModel.Route>> listAll() {
        RouteModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getRoutes());
    }


    public ResultDto<String> add(FtRoute ftRoute) {
        RouteModel model = loadModel();
        String trancode = ftRoute.getTran_code();
        if (StringUtils.isEmpty(trancode)) {
            return ResultDtoTool.buildError("交易码不能为空");
        }
        for (RouteModel.Route route : model.getRoutes()) {
            if (StringUtils.equalsIgnoreCase(route.getTranCode(), trancode)
                    && StringUtils.equalsIgnoreCase(route.getUser(), ftRoute.getUser())) {
                return ResultDtoTool.buildError("添加失败，已有此交易码用户组合");
            }
        }
        RouteModel.Route target = new RouteModel.Route();
        CfgModelConverter.convertTo(ftRoute, target);
        model.getRoutes().add(target);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtRoute ftRoute) {
        RouteModel.Route target = null;
        RouteModel model = loadModel();
        for (RouteModel.Route route : model.getRoutes()) {
            if (StringUtils.equals(route.getTranCode(), ftRoute.getTran_code())
                    && StringUtils.equalsIgnoreCase(route.getUser(), ftRoute.getUser())) {
                target = route;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("没有找到指定的路由信息");
        CfgModelConverter.convertTo(ftRoute, target);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del(FtRoute ftRoute) {
        RouteModel model = loadModel();
        List<RouteModel.Route> routes = model.getRoutes();
        for (RouteModel.Route route : routes) {
            if (StringUtils.equals(route.getTranCode(), ftRoute.getTran_code())
                    && StringUtils.equalsIgnoreCase(route.getUser(), ftRoute.getUser())) {
                routes.remove(route);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    public ResultDto<String> del(FtServiceInfo ftServiceInfo) {
        RouteModel model = loadModel();
        List<RouteModel.Route> routes = model.getRoutes();
        for (RouteModel.Route route : routes) {
            if (StringUtils.equals(route.getTranCode(), ftServiceInfo.getTrancode())
                    && StringUtils.equalsIgnoreCase(route.getUser(), ftServiceInfo.getPutAuth())) {
                routes.remove(route);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    public ResultDto<String> delByTranscode(FtRoute ftRoute) {
        RouteModel model = loadModel();
        List<RouteModel.Route> routes = model.getRoutes();
        List<RouteModel.Route> routesDelList = new ArrayList<>();
        for (RouteModel.Route route : routes) {
            if (StringUtils.equals(route.getTranCode(), ftRoute.getTran_code())) {
                routesDelList.add(route);
            }
        }
        routes.removeAll(routesDelList);
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    public ResultDto<RouteModel.Route> selByTranscodeAndUser(FtRoute ftRoute) {
        RouteModel model = loadModel();
        RouteModel.Route target = null;
        for (RouteModel.Route route : model.getRoutes()) {
            if (StringUtils.equals(route.getTranCode(), ftRoute.getTran_code())
                    && StringUtils.equalsIgnoreCase(route.getUser(), ftRoute.getUser())) {
                target = route;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(target);
    }

    public RouteModel.Route selByTranscodeAndUser(String tranCode, String user, RouteModel model) {
        RouteModel.Route target = null;
        for (RouteModel.Route route : model.getRoutes()) {
            if (StringUtils.equals(route.getTranCode(), tranCode)
                    && StringUtils.equalsIgnoreCase(route.getUser(), user)) {
                target = route;
                break;
            }
        }
        return target;
    }

    private static final String CFG_FILE_NAME = "route.xml";

    public RouteModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, RouteModel.class);
    }

    public void save(RouteModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }
    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }

    @Override
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtRoute ftRoute = (FtRoute) curr;
        if(isNew){
            RouteModel.Route target = new RouteModel.Route();
            CfgModelConverter.convertTo(ftRoute,target);
            return XmlBeanUtil.toXml(target);
        }
        RouteModel.Route target = null;
        RouteModel model = loadModel();
        for (RouteModel.Route route : model.getRoutes()) {
            if (StringUtils.equals(route.getTranCode(), ftRoute.getTran_code())
                    && StringUtils.equalsIgnoreCase(route.getUser(), ftRoute.getUser())) {
                target = route;
                break;
            }
        }
        if (target == null) return null;
        return XmlBeanUtil.toXml(target);

	}

    public ResultDto<String> delRoutes(String routeName) {
        RouteModel model = loadModel();
        List<RouteModel.Route> routes = model.getRoutes();
        for (RouteModel.Route route : routes) {
            String[]  sub = route.getDestination().split(",");
            List<String> targets = new ArrayList<>();//NOSONAR
            for(int i = 0; i < sub.length ; i++){
                targets.add(sub[i]);
            }
            for(int i = 0; i < targets.size(); i++){
                if(targets.get(i).equals(routeName)){
                    targets.remove(targets.get(i));
                }
            }
            StringBuilder newRoute = new StringBuilder();
            for(String target:targets){
                newRoute.append(target+",");
            }
            if(newRoute.length() > 0){
                newRoute.deleteCharAt(newRoute.length()-1);
            }
            route.setDestination(newRoute.toString());
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    public ResultDto<String> addRoutes(String routeName) {
        RouteModel model = loadModel();
        List<RouteModel.Route> routes = model.getRoutes();
        ServiceModel serviceModel = serviceInfoService.loadModel();
        List<ServiceModel.Service> services = serviceModel.getServices();
        for(ServiceModel.Service service:services){
            List<ServiceModel.AuthUser> users = new ArrayList<>();
            if(service.getGetAuth() != null && service.getGetAuth().getUsers() != null){
                users = service.getGetAuth().getUsers();
            }else {
                continue;
            }
            for(ServiceModel.AuthUser user:users){
                if(StringUtils.equalsIgnoreCase(routeName.substring(0,5),user.getUser())){
                    for(RouteModel.Route route:routes){
                        if(StringUtils.equalsIgnoreCase(service.getTrancode(),route.getTranCode())){
                            if(route.getDestination() == ""){
                                route.setDestination(routeName);
                                break;
                            }
                            String[] str = route.getDestination().split(",");
                            boolean exist = false;
                            for(int i = 0; i < str.length; i++){
                                if(str[i].equals(routeName)){
                                    exist = true;
                                    break;
                                }
                            }
                            if(!exist){
                                route.setDestination(route.getDestination()+","+routeName);
                            }
                        }
                    }
                }
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("添加路由成功");
    }

    public ResultDto<String> addRouteByGetAuth(GetAuthEntity getAuth) {
        RouteModel model = loadModel();
        List<RouteModel.Route> routes = model.getRoutes();
        List<SystemModel.System> systems = sysService.loadModel().getSystems();
        for(RouteModel.Route route:routes){
            for(SystemModel.System system:systems){
                if(StringUtils.equalsIgnoreCase(getAuth.getTrancode(),route.getTranCode())
                        && StringUtils.equalsIgnoreCase(getAuth.getUserName(),system.getUsername())){
                    route.setDestination(route.getDestination()+","+system.getName());
                }
            }
            route.setDestination(delMulti(route.getDestination().split(",")));

        }
        save(model);
        return ResultDtoTool.buildSucceed("添加路由成功");
    }

    public List<FtRoute> selByDestination(String sysName){
        List<FtRoute> ftRouteList = new ArrayList<>();
        RouteModel model = loadModel();
        for (RouteModel.Route route : model.getRoutes()) {
            FtRoute ftRoute = new FtRoute();
            CfgModelConverter.convertTo(route,ftRoute);
            String temp = CollectionUtil.removeOne(ftRoute.getDestination(),sysName,",");
            if(!ftRoute.getDestination().equals(temp)){
                ftRouteList.add(ftRoute);
            }
        }
        return ftRouteList;
    }

    private  String delMulti(String[] mulitStr) {
        List<String> list = new ArrayList<String>();
        for (int i=0; i<mulitStr.length; i++) {
            if(!list.contains(mulitStr[i]) && !mulitStr[i].equals("")) {
                list.add(mulitStr[i]);
            }
        }
        String[] newStr = list.toArray(new String[1]); //返回一个包含所有对象的指定类型的数组
        StringBuffer sb = new StringBuffer();
        for(String str : newStr){
            sb.append(str).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

}
