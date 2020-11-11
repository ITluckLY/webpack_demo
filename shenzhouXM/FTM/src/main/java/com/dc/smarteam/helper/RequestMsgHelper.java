package com.dc.smarteam.helper;


import com.dc.smarteam.common.json.GsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mocg on 2016/10/11.
 */
public class RequestMsgHelper {

    private interface Server {
        // 操作目标 节点管理
        String CFG = "node";
        // 操作目标 节点列表
        String NODES = "nodes";
        // 操作目标 权限管理
        String AUTH = "auth";
        // 操作目标 用户管理
        String USER = "user";
        // 服务管理
        String SERVICE = "service";
        // 流程管理
        String FLOW = "flow";
        // 组件管理
        String COMPONENT = "component";
        // 定时任务
        String CRONTAB = "crontab";
        // 文件管理
        String FILE = "file";
        // 定时任务
        String ROUTE = "route";
        // 文件管理
        String SYSINFO = "sysInfo";
        // 文件清理
        String FILE_CLEAN = "fileClean";
        // 配置同步
        String CFGSYNC = "cfgSync";
        // 文件重命名
        String FILE_RENAME = "fileRename";
        // 版本号
        String VERSION = "version";
        // 目录服务器
        String BIZFILE = "bizFile";
        // 系统名称映射
        String VSYS_MAP = "vsysmap";
        // 客户端状态
        String CLIENT_STATUS = "clientStatusInfo";
        // 秘钥状态
        String KEYS = "keys";
    }

    public static String generate(String target, String operateType, Map<String, ?> data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("target", target);
        map.put("operateType", operateType);
        map.put("data", data);
        return GsonUtil.toJson(map);
    }

    public static String trun2Target(String cfgName) {
        if ("cfg.xml".equalsIgnoreCase(cfgName)) return Server.CFG;
        if ("components.xml".equalsIgnoreCase(cfgName)) return Server.COMPONENT;
        if ("crontab.xml".equalsIgnoreCase(cfgName)) return Server.CRONTAB;
        if ("file.xml".equalsIgnoreCase(cfgName)) return Server.FILE;
        if ("file_clean.xml".equalsIgnoreCase(cfgName)) return Server.FILE_CLEAN;
        if ("file_rename.xml".equalsIgnoreCase(cfgName)) return Server.FILE_RENAME;
        if ("flow.xml".equalsIgnoreCase(cfgName)) return Server.FLOW;
        if ("nodes.xml".equalsIgnoreCase(cfgName)) return Server.NODES;
        if ("route.xml".equalsIgnoreCase(cfgName)) return Server.ROUTE;
        if ("services_info.xml".equalsIgnoreCase(cfgName)) return Server.SERVICE;
        if ("system.xml".equalsIgnoreCase(cfgName)) return Server.SYSINFO;
        if ("user.xml".equalsIgnoreCase(cfgName)) return Server.USER;
        if ("vsysmap.xml".equalsIgnoreCase(cfgName)) return Server.VSYS_MAP;
        if ("client_status.xml".equalsIgnoreCase(cfgName)) return Server.CLIENT_STATUS;
        if ("keys.xml".equalsIgnoreCase(cfgName)) return Server.KEYS;
        return null;
    }
}
