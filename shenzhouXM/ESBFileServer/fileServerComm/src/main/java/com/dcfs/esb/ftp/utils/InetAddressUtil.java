package com.dcfs.esb.ftp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by mocg on 2016/8/3.
 */
public class InetAddressUtil {
    private static final Logger log = LoggerFactory.getLogger(InetAddressUtil.class);

    private InetAddressUtil() {
    }

    private static List<InetAddress> cacheInetAddressList;

    public static String getHostName() {
        return getLocalHost().getHostName();
    }

    public static String getHostAddress() {
        return getLocalHost().getHostAddress();
    }

    public static InetAddress getLocalHost() {
        List<InetAddress> list = getCacheAddressList();
        return list.get(0);
    }

    public static String getHostAddress(InetAddress inetAddress) {
        return inetAddress == null ? null : inetAddress.getHostAddress();
    }

    /**
     * 无网卡不能获取ip时,添加ip 127.0.0.1
     *
     * @return
     */
    public static List<InetAddress> getCacheAddressList() {
        if (cacheInetAddressList != null) return cacheInetAddressList;
        synchronized (InetAddressUtil.class) {
            if (cacheInetAddressList != null) return cacheInetAddressList;
            try {
                cacheInetAddressList = getAddressList();
                //无网卡不能获取ip时,使用ip 127.0.0.1
                if (cacheInetAddressList.isEmpty()) {
                    cacheInetAddressList.add(InetAddress.getByName("127.0.0.1"));//NOSONAR
                }
            } catch (SocketException e) {
                log.error("获取本机IP err", e);
            } catch (UnknownHostException e) {
                log.error("获取本机IP err", e);
            }
        }
        return cacheInetAddressList;
    }

    public static List<InetAddress> getAddressList() throws SocketException {//NOSONAR
        List<InetAddress> addressList = new ArrayList<InetAddress>();
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        while (nis.hasMoreElements()) {//NOSONAR
            NetworkInterface ni = nis.nextElement();
            if (!ni.isUp() || ni.isLoopback()) continue;
            //排除虚拟网卡
            if (ni.getDisplayName() != null && ni.getDisplayName().toLowerCase().contains("virtual")) continue;
            List<InterfaceAddress> list = ni.getInterfaceAddresses();
            for (InterfaceAddress ia : list) {
                if (null != ia.getBroadcast()) {//ip4
                    InetAddress address = ia.getAddress();
                    if (address.isLoopbackAddress()) continue;//127
                    if (address instanceof Inet4Address) {
                        log.info(" 本机实际联网网卡IPv4地址：{} ", address.getHostAddress());
                        addressList.add(address);
                    }
                }
            }
        }
        return addressList;
    }

    public static List<InetAddress> getAddressList0() {
        List<InetAddress> addressList = new ArrayList<InetAddress>();
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                if (addresses.hasMoreElements()) {
                    addressList.add(addresses.nextElement());
                }
            }
        } catch (SocketException e) {
            log.error("Error when getting host ip address", e);
        }
        return addressList;
    }

    public static byte[] ip4ToBytes(String ip) {
        String[] arr = ip.split("\\.");
        byte[] bytes = new byte[4];//NOSONAR
        bytes[0] = (byte) Integer.parseInt(arr[0]);
        bytes[1] = (byte) Integer.parseInt(arr[1]);
        bytes[2] = (byte) Integer.parseInt(arr[2]);//NOSONAR
        bytes[3] = (byte) Integer.parseInt(arr[3]);//NOSONAR
        return bytes;
    }

}
