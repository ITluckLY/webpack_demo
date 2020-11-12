package com.dcfs.esb.ftp.msggenerator;

import com.dcfs.esb.ftp.utils.GsonUtil;
import net.sf.json.JSONObject;

/**
 * Created by mocg on 2016/8/29.
 */
public class MsgGenFactory {
    private MsgGenFactory() {
    }

    public static final String NODEPARAM = "node";
    public static final String NODE = "nodes";
    public static final String FILE = "file";
    public static final String USER = "user";
    public static final String AUTH = "auth";
    public static final String IPCONFIG = "user";
    public static final String COMPONENT = "component";
    public static final String FLOW = "flow";
    public static final String TASK = "crontab";
    public static final String SERVICEINFO = "service";
    public static final String SYSINFO = "sysInfo";
    public static final String ROUTE = "route";
    public static final String FILE_CLEAN = "fileClean";
    public static final String FILE_RENAME = "fileRename";

    //文件参数
    public static String fileParam(FileDataParam param, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(FILE);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("key", param.getKey());
        data.put("rename", param.getRename());
        data.put("oldCharset", param.getOldCharset());
        data.put("newCharset", param.getNewCharset());
        data.put("cryptogramType", param.getCryptogramType());
        entity.setData(data);

        return GsonUtil.toJson(entity);
    }
}
