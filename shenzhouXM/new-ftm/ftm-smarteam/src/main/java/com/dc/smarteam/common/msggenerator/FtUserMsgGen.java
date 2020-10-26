package com.dc.smarteam.common.msggenerator;

import com.dc.smarteam.modules.user.entity.FtUser;

/**
 * Created by Administrator on 2016/3/3.
 */
public class FtUserMsgGen {

    public static String getAll() {
        String str = "{\n" +
                "    \"target\": \"user\",\n" +
                "    \"operateType\": \"select\",\n" +
                "    \"data\": {\n" +
                "    }\n" +
                "}";
        return str;
    }

    public static String selectUserBySys(FtUser ftUser) {
        String str = "{\n" +
                "    \"target\": \"user\",\n" +
                "    \"operateType\": \"selectUserBySys\",\n" +
                "    \"data\": {\n" +
                "       \"sysname\":\"" + ftUser.getSystemName() + "\"\n" +
                "    }\n" +
                "}";
        return str;
    }

    public static String add(FtUser ftUser) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"user\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"addUser\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"uid\":\"" + ftUser.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"home\":\"" + ftUser.getUserDir() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"sysname\":\"" + ftUser.getSystemName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"describe\":\"" + ftUser.getDes() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"passwd\":\"" + ftUser.getPwd() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"grant\":\"" + ftUser.getPermession() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"IP\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"status\":\"0\",\n" +
                "\t\t\t\t\t\t\t\t\"IPDescribe\":\"\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }

    public static String edit(FtUser ftUser) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"user\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"updateUser\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"uid\":\"" + ftUser.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"home\":\"" + ftUser.getUserDir() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"sysname\":\"" + ftUser.getSystemName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"describe\":\"" + ftUser.getDes() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"passwd\":\"" + ftUser.getPwd() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"grant\":\"" + ftUser.getPermession() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"IP\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\"status\":\"0\",\n" +
                "\t\t\t\t\t\t\t\t\"IPDescribe\":\"\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }

    public static String select(FtUser ftUser) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"user\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"selOneUser\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"uid\":\"" + ftUser.getName() + "\"\n" +
                "\t\t\t\t\t\t\t\t,\"sysname\":\"" + ftUser.getSystemName() + "\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }

    public static String delete(FtUser ftUser) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"user\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"delUser\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"uid\":\"" + ftUser.getName() + "\"\n" +
                "\t\t\t\t\t\t\t\t,\"sysname\":\"" + ftUser.getSystemName() + "\"\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }

    public static String queryAuthDirOfUser(String sysname, String userName) {
        return "{" +
                "\"target\":\"auth\"," +
                "\"operateType\":\"queryAuthDirOfUser\"," +
                "\"data\":{" +
                "\"name\":\"" + sysname + "_" + userName + "\"" +
                "}" +
                "} ";
    }

    public static String queryAuthUserOfSys(String sysname) {
        return "{" +
                "\"target\":\"auth\"," +
                "\"operateType\":\"queryAuthUserOfSys\"," +
                "\"data\":{" +
                "\"name\":\"" + sysname + "\"" +
                "}" +
                "} ";
    }

    public static String addUserAuth(String uname, String path, String auth) {
        String permession = uname + "=" + auth;
        return "{" +
                "\"target\":\"auth\"," +
                "\"operateType\":\"addUserAuth\"," +
                "\"data\":{" +
                "\"name\":\"" + path + "\"," +
                "\"permession\":\"" + permession + "\"" +
                "}" +
                "} ";
    }

    public static String delUserAuth(String uname, String path, String auth) {
        String permession = uname + "=" + auth;
        return "{" +
                "\"target\":\"auth\"," +
                "\"operateType\":\"delUserAuth\"," +
                "\"data\":{" +
                "\"name\":\"" + path + "\"," +
                "\"permession\":\"" + permession + "\"" +
                "}" +
                "} ";
    }

    public static String queryAllAuth() {
        return "{" +
                "\"target\":\"auth\"," +
                "\"operateType\":\"queryAllOrderbySys\"," +
                "\"data\":{" +
                "}" +
                "} ";
    }
}
