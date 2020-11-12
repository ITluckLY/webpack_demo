package com.dcfs.esb.ftp.spring.outservice;

import java.io.IOException;

/**
 * Created by mocg on 2016/11/7.
 */
public interface IFileTargetNodeService {

    /**
     * 文件目标节点 ip:port  返回-1:表示没有可用节点，null、0:表示按遍历方式 1:表示继续使用本连接
     *
     * @param filePath
     * @return ip:port of target dataNode
     */
    String findTargetNodeAddrByFilePath(String filePath) throws IOException;
}
