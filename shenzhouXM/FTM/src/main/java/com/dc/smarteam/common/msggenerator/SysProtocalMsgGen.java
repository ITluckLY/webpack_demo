package com.dc.smarteam.common.msggenerator;

import com.dc.smarteam.modules.protocol.entity.SysProtocol;

/**
 * Created by Administrator on 2016/3/3.
 */
public class SysProtocalMsgGen {

    public static String getSysProtocal() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"sysInfo\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"select\",\n" +
                "\t\t\t\t\t\t\t\"data\":{}}";
        return str;
    }

    public static String updateSysProtocal(SysProtocol sysProtocol) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"sysInfo\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"update\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"name\":\"" + sysProtocol.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"protocol\":\"" + sysProtocol.getProtocol() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"ip\":\"" + sysProtocol.getIp() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"port\":\"" + sysProtocol.getPort() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"username\":\"" + sysProtocol.getUsername() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"password\":\"" + sysProtocol.getPassword() + "\",\n" +
                "\t\t\t\t\t\t}} ";
        return str;
    }

    public static String saveSysProtocal(SysProtocol sysProtocol) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"sysInfo\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"add\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"name\":\"" + sysProtocol.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"protocol\":\"" + sysProtocol.getProtocol() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"ip\":\"" + sysProtocol.getIp() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"port\":\"" + sysProtocol.getPort() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"username\":\"" + sysProtocol.getUsername() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"password\":\"" + sysProtocol.getPassword() + "\",\n" +
                "\t\t\t\t\t\t}} ";
        return str;
    }

    public static String delSysProtocal(SysProtocol sysProtocol) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"sysInfo\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"del\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\"name\":\"" + sysProtocol.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"protocol\":\"" + sysProtocol.getProtocol() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"ip\":\"" + sysProtocol.getIp() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"port\":\"" + sysProtocol.getPort() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"username\":\"" + sysProtocol.getUsername() + "\",\n" +
                "\t\t\t\t\t\t\t\t\"password\":\"" + sysProtocol.getPassword() + "\",\n" +
                "\t\t\t\t\t\t}} ";
        return str;
    }


    /*String str = "{\n" +
            "\t\t\t\t\t\t\t\"target\":\"system\",\n" +
            "\t\t\t\t\t\t\t\"operateType\":\"select\",\n" +
            "\t\t\t\t\t\t\t\"data\":{\n" +
            "\t\t\t\t\t\t\t\t\t\"name\":\"\",\n" +
            "\t\t\t\t\t\t\t\t\t\"protocol\":\"\",\n" +
            "\t\t\t\t\t\t\t\t\t\"ip\":\"\"\n" +
            "\t\t\t\t\t\t\t\t\t\"port\":\"\"\n" +
            "\t\t\t\t\t\t\t\t\t\"username\":\"\"\n" +
            "\t\t\t\t\t\t\t\t\t\"password\":\"\"\n" +
            "\t\t\t\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t\t}  ";*/

}
