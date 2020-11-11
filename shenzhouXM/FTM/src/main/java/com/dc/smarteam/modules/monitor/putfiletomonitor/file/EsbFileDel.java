package com.dc.smarteam.modules.monitor.putfiletomonitor.file;

import com.dc.smarteam.modules.monitor.putfiletomonitor.client.FtpConnector;

/**
 * 远程文件删除处理命令
 */
public class EsbFileDel implements Runnable {
    private String fileName = null;
    private FtpConnector remoteConnect = null;

    /**
     * 文件删除线程
     *
     * @param fileName
     */
    public EsbFileDel(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 文件删除线程的执行过程，异步通过文件服务器的管理端口进行刪除文件的服务
     */
    public void run() {
//		try
//		{
//			String ip = FtpConfig.getInstance().getRemoteServIP();
//			int port = FtpConfig.getInstance().getRemoteServPort();
//			remoteConnect = new FtpConnector(ip, port);
//			FileMsgBean bean = new FileMsgBean();
//			bean.setFileName(this.fileName);
//			bean.setFileMsgFlag(FileMsgType.DEL);
//			remoteConnect.writeHead(bean);
//			remoteConnect.readHead(bean);
//		} catch (Exception e)
//		{
//			if (log.isInfoEnabled())
//				log.info("删除远程文件失败:" + fileName);
//		} finally
//		{
//			if (remoteConnect != null)
//				try {
//					remoteConnect.close();
//				} catch (FtpException e) {
//					if (log.isErrorEnabled())
//						log.error("关闭远程文件出错", e);
//				}
//		}
    }
}
