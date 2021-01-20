package com.dcfs.esc.ftp.comm.scrt.util;

import java.util.UUID;

/**
 * Created by zhudp on 2019/12/10.
 */
public class FlowNoHelper {
    private FlowNoHelper() {
    }

    public static String generateFlowNo() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 流水号(32位) = 文件交易码10位 + 客户端标识(10位) + 秒级时间戳(10位) + 预留(2位)
     * <p>
     * 文件交易码：文件传输平台接入规范里给全行定义的交易码，生产方预先配置好的
     * 客户端标识：客户端所在服务器IPv4地址32位二进制码的16进制表示（8位）+ 端口号后两位数字
     * 秒级时间戳：记录交易开始时间，精确到秒
     */
    public static String generateFlowNo(String tranCode, String ip, int port) {
        StringBuilder sb = new StringBuilder();
        String tranCodeTmp = parseTen(tranCode);
        String clientSign = parseIP(ip) + parsePort(port);
        String timeStamp = parseTen(String.valueOf(System.currentTimeMillis() / 1000));//毫秒转为秒//NOSONAR
        sb.append(tranCodeTmp).append(clientSign).append(timeStamp).append("00");
        return sb.toString();
    }

    /**
     * 将IP地址转换为16进制字符串
     */
    private static String parseIP(String ipStr) {
        StringBuilder sb = new StringBuilder();
        int[] ipInt = new int[4];//NOSONAR
        int position1 = ipStr.indexOf('.');
        int position2 = ipStr.indexOf('.', position1 + 1);
        int position3 = ipStr.indexOf('.', position2 + 1);
        // 将每个.之间的字符串转换成整型
        ipInt[0] = Integer.parseInt(ipStr.substring(0, position1));
        ipInt[1] = Integer.parseInt(ipStr.substring(position1 + 1, position2));
        ipInt[2] = Integer.parseInt(ipStr.substring(position2 + 1, position3));//NOSONAR
        ipInt[3] = Integer.parseInt(ipStr.substring(position3 + 1));//NOSONAR
        // 不足位补0
        String[] ipArray = new String[4];//NOSONAR
        for (int i = 0; i < 4; i++) {//NOSONAR
            ipArray[i] = Integer.toHexString(ipInt[i]);
            if (ipArray[i].length() == 1) ipArray[i] = "0" + ipArray[i];
            sb.append(ipArray[i]);
        }
        return sb.toString();
    }

    /**
     * 取端口号后两位，不足位补0
     */
    private static String parsePort(int port) {
        String subPort = String.valueOf(port % 100);//NOSONAR
        if (subPort.length() == 1) subPort = "0" + subPort;
        return subPort;
    }

    /**
     * 字符串不足10位补0
     */
    private static String parseTen(String inputStr) {
        StringBuilder sb = new StringBuilder(inputStr);
        for (int i = 10; i > inputStr.length(); i--) {//NOSONAR
            sb.insert(0, "0");
        }
        return sb.toString();
    }
}
