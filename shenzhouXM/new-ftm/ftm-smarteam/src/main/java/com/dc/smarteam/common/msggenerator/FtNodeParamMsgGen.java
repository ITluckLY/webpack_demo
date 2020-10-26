package com.dc.smarteam.common.msggenerator;

import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;

/**
 * Created by Administrator on 2016/3/3.
 */
public class FtNodeParamMsgGen {

    public static final String NETWORK = "network";

    //查询节点参数
    public static String getAllStr() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"node\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"select\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    //添加节点
    public static String add(FtNodeParam ftNodeParam) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"node\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"add\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"" + ftNodeParam.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"" + ftNodeParam.getDes() + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"" + ftNodeParam.getValue() + "\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }

    //修改节点
    public static String select(FtNodeParam ftNodeParam) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"node\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"selByKey\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"" + ftNodeParam.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"" + ftNodeParam.getDes() + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"" + ftNodeParam.getValue() + "\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }

    //修改节点
    public static String update(FtNodeParam ftNodeParam) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"node\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"update\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"" + ftNodeParam.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"" + ftNodeParam.getDes() + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"" + ftNodeParam.getValue() + "\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }

    //删除节点
    public static String del(FtNodeParam ftNodeParam) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"node\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"del\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"" + ftNodeParam.getName() + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"" + ftNodeParam.getDes() + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"" + ftNodeParam.getValue() + "\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }

    //删除节点
    public static String currResource(String network) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"node\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"currResource\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"" + NETWORK + "\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t} ";
        return str;
    }
}
