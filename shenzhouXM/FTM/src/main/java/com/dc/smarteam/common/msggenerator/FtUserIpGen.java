package com.dc.smarteam.common.msggenerator;

import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;

/**
 * Created by Administrator on 2016/3/3.
 */
public class FtUserIpGen {
    public static String getAllStr() {
        String str = "{\n" +
                "    \"target\": \"user\",\n" +
                "    \"operateType\": \"select\",\n" +
                "    \"data\": {\n" +
                "    }\n" +
                "}";
        return str;
    }

    public static String addIP(FtUserIp ftUserIp) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"user\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"addIP\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"uid\":\"" + ftUserIp.getUser() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"sysname\":\"" + ftUserIp.getSystemName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"home\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"name\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"passwd\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"IP\":\"" + ftUserIp.getIpAddress() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"status\":\"" + ftUserIp.getState() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"IPDescribe\":\"" + ftUserIp.getDes() + "\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}";
        return str;
    }

    public static String updateIP(FtUserIp ftUserIp) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"user\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"updateIP\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"uid\":\"" + ftUserIp.getFtUserId() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"sysname\":\"" + ftUserIp.getSystemName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"home\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"name\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"passwd\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"IP\":\"" + ftUserIp.getIpAddress() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"status\":\"" + ftUserIp.getState() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"IPDescribe\":\"" + ftUserIp.getDes() + "\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}";
        return str;
    }


    public static String delIP(FtUserIp ftUserIp) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"user\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"delIP\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"uid\":\"" + ftUserIp.getFtUserId() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"sysname\":\"" + ftUserIp.getSystemName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"home\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"name\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"passwd\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"IP\":\"" + ftUserIp.getIpAddress() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"status\":\"" + ftUserIp.getState() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"IPDescribe\":\"" + ftUserIp.getDes() + "\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}";
        return str;
    }

    public static String selByUser(FtUserIp ftUserIp, String sysname) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"user\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"selByUser\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"uid\":\"" + ftUserIp.getFtUserId() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"sysname\":\"" + sysname + "\",\n" +
                "\t\t\t\t\t\t\t\t\"home\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"name\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"passwd\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"IP\":\"" + ftUserIp.getIpAddress() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"status\":\"" + ftUserIp.getState() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"IPDescribe\":\"" + ftUserIp.getDes() + "\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}";
        return str;
    }
}
