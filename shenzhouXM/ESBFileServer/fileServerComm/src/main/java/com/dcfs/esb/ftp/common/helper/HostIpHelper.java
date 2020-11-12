package com.dcfs.esb.ftp.common.helper;


import com.dcfs.esb.ftp.utils.InetAddressUtil;

/**
 * Created by mocg on 2017/1/5.
 */
public class HostIpHelper {
    private HostIpHelper() {
    }

    /**
     * 用于端口监听,外部不一定能访问,如 0.0.0.0
     *
     * @return
     */
    public static String getHostAddress(String hostAddress) {
        if (hostAddress == null || hostAddress.isEmpty() || hostAddress.equals("127.0.0.1")) {//NOSONAR
            hostAddress = InetAddressUtil.getHostAddress();//NOSONAR
        }
        return hostAddress;
    }

    /**
     * 外部可以访问的具体的IP
     *
     * @return
     */
    public static String getHostTrueIp(String hostAddress) {
        String trueIp;
        if (hostAddress == null || hostAddress.isEmpty() || hostAddress.equals("0.0.0.0") || hostAddress.equals("127.0.0.1")) {//NOSONAR
            trueIp = InetAddressUtil.getHostAddress();
        } else trueIp = hostAddress;
        return trueIp;
    }
}
