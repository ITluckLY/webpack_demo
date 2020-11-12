package com.dcfs.esb.ftp.server.system;

import com.dcfs.esb.ftp.server.system.fsftp.FSFTPManage;
import com.dcfs.esb.ftp.server.system.ftp.FTPManage;
import com.dcfs.esb.ftp.server.system.fts.FtsFileMsgProtocol;
import com.dcfs.esb.ftp.server.system.sftp.SFTPManage;
import com.dcfs.esb.ftp.server.system.tcp.TCPManage;

public class ProtocolFactory {
    private ProtocolFactory() {
    }

    public static IProtocol getProtocol(SystemInfo systemInfo, String localFileName, String remoteFileName) {
        String protocol = systemInfo.getProtocol();
        if ("ftp".equalsIgnoreCase(protocol)) {
            return new FTPManage(systemInfo, localFileName, remoteFileName);
        } else if ("sftp".equalsIgnoreCase(protocol)) {
            return new SFTPManage(systemInfo, localFileName, remoteFileName);
        } else if ("tcp".equalsIgnoreCase(protocol)) {
            return new TCPManage(systemInfo, localFileName, remoteFileName);
        } else if ("fsftp".equalsIgnoreCase(protocol)) {//NOSONAR
            return new FSFTPManage(systemInfo, localFileName, remoteFileName);
        } else if ("ftsfilemsg".equalsIgnoreCase(protocol)) {//NOSONAR
            return new FtsFileMsgProtocol(systemInfo, localFileName, remoteFileName);
        } else {
            return null;
        }
    }

    public static IProtocol getProtocol(SystemInfo systemInfo, String localFileName, String remoteFileName, String name) {
        String protocol = systemInfo.getProtocol();
        if ("ftp".equalsIgnoreCase(protocol)) {
            return new FTPManage(systemInfo, localFileName, remoteFileName, name);
        } else if ("sftp".equalsIgnoreCase(protocol)) {
            return new SFTPManage(systemInfo, localFileName, remoteFileName);
        } else if ("tcp".equalsIgnoreCase(protocol)) {
            return new TCPManage(systemInfo, localFileName, remoteFileName);
        } else if ("fsftp".equalsIgnoreCase(protocol)) {
            return new FSFTPManage(systemInfo, localFileName, remoteFileName);
        } else if ("ftsfilemsg".equalsIgnoreCase(protocol)) {
            return new FtsFileMsgProtocol(systemInfo, localFileName, remoteFileName);
        } else {
            return null;
        }
    }

    public static IProtocol getProtocol(SystemInfo systemInfo, String localFileName, String remoteFileName, String name, FileRouteArgs routeArgs) {
        IProtocol iProtocol = null;
        String protocol = systemInfo.getProtocol();
        if ("ftp".equalsIgnoreCase(protocol)) {
            iProtocol = new FTPManage(systemInfo, localFileName, remoteFileName, name);
        } else if ("sftp".equalsIgnoreCase(protocol)) {
            iProtocol = new SFTPManage(systemInfo, localFileName, remoteFileName);
        } else if ("tcp".equalsIgnoreCase(protocol)) {
            return new TCPManage(systemInfo, localFileName, remoteFileName);
        } else if ("fsftp".equalsIgnoreCase(protocol)) {
            iProtocol = new FSFTPManage(systemInfo, localFileName, remoteFileName);
        } else if ("ftsfilemsg".equalsIgnoreCase(protocol)) {
            iProtocol = new FtsFileMsgProtocol(systemInfo, localFileName, remoteFileName);
        }
        if (iProtocol != null) iProtocol.setFileRouteArgs(routeArgs);
        return iProtocol;
    }
}
