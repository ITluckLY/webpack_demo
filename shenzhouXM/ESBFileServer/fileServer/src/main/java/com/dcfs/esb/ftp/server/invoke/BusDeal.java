package com.dcfs.esb.ftp.server.invoke;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.key.KeyDeal;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.auth.AuthDeal;
import com.dcfs.esb.ftp.server.invoke.bizfile.BizFileDeal;
import com.dcfs.esb.ftp.server.invoke.client.ClientStatusDeal;
import com.dcfs.esb.ftp.server.invoke.component.ComponentDeal;
import com.dcfs.esb.ftp.server.invoke.crontab.CrontabDeal;
import com.dcfs.esb.ftp.server.invoke.file.FileDeal;
import com.dcfs.esb.ftp.server.invoke.fileclean.FileCleanDeal;
import com.dcfs.esb.ftp.server.invoke.filemsg2client.FileMsg2ClientDeal;
import com.dcfs.esb.ftp.server.invoke.filerename.FileRenameDeal;
import com.dcfs.esb.ftp.server.invoke.flow.FlowDeal;
import com.dcfs.esb.ftp.server.invoke.node.NodeDeal;
import com.dcfs.esb.ftp.server.invoke.node.NodesDeal;
import com.dcfs.esb.ftp.server.invoke.nodesync.NodeSyncDeal;
import com.dcfs.esb.ftp.server.invoke.route.RouteDeal;
import com.dcfs.esb.ftp.server.invoke.service.ServiceDeal;
import com.dcfs.esb.ftp.server.invoke.sysinfo.SysInfoDeal;
import com.dcfs.esb.ftp.server.invoke.user.UserDeal;
import com.dcfs.esb.ftp.server.invoke.version.VersionDeal;
import com.dcfs.esb.ftp.server.invoke.vsys.VsysmapDeal;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.ThreadLocalTool;
import com.dcfs.esc.ftp.comm.constant.UnitCons;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class BusDeal implements Runnable {
    // 操作目标 节点管理
    public static final String CFG = "node";
    // 操作目标 节点列表
    public static final String NODES = "nodes";
    // 操作目标 权限管理
    public static final String AUTH = "auth";
    // 操作目标 用户管理
    public static final String USER = "user";
    // 服务管理
    public static final String SERVICE = "service";
    // 流程管理
    public static final String FLOW = "flow";
    // 组件管理
    public static final String COMPONENT = "component";
    // 定时任务
    public static final String CRONTAB = "crontab";
    // 文件管理
    public static final String FILE = "file";
    // 定时任务
    public static final String ROUTE = "route";
    // 文件管理
    public static final String SYSINFO = "sysInfo";
    // 文件清理
    public static final String FILE_CLEAN = "fileClean";
    // 配置同步
    public static final String CFGSYNC = "cfgSync";
    // 文件重命名
    public static final String FILE_RENAME = "fileRename";
    // 版本号
    public static final String VERSION = "version";
    // 目录服务器
    public static final String BIZFILE = "bizFile";
    // 系统名称映射
    public static final String VSYS_MAP = "vsysmap";
    // 客户端状态映射
    public static final String CLIENT_STATUS_INFO = "clientStatusInfo";
    // 停止应用
    public static final String STOP_SERVER = "stopServer";
    // 发送给客户端的文件消费信息
    public static final String FILE_MSG_2_CLIENT = "fileMsg2Client";
    // 秘钥状态
    String KEYS = "keys";
    private static final Logger log = LoggerFactory.getLogger(BusDeal.class);
    private String mess;
    private Socket socket;

    public BusDeal(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream is = socket.getInputStream();
            final int headLen = 6;
            byte[] b = new byte[headLen];
            is.read(b); //NOSONAR
            String messHeader = new String(b, "utf-8");
            if (StringUtils.equals(messHeader, "\u0000\u0000\u0000\u0000\u0000\u0000")) {
                log.debug("请求报文头为空，检测到监控探测#socket:{}", socket);
                return;
            }
            int length = Integer.parseInt(messHeader);
            String orderMess = new String(readNum(is, length), "utf-8");
            if (log.isDebugEnabled()) {
                String busDealThreadFlag = System.currentTimeMillis() + "-" + RandomUtils.nextInt() + "-" + socket.getInetAddress().getHostAddress();
                ThreadLocalTool.getThreadLocalOfBusDeal().set(busDealThreadFlag);
                log.debug(busDealThreadFlag + "-请求报文:" + messHeader + orderMess);
            }
            mess = orderMess;
            runInner();
        } catch (SocketTimeoutException e) {
            log.error("读取请求报文超时", e);
            MessDealTool.sendBackMes(ResultDtoTool.buildError("读取请求报文超时"), socket);
        } catch (NumberFormatException nfe) {
            log.error("请求报文格式错误", nfe);
            MessDealTool.sendBackMes(ResultDtoTool.buildError("不能识别报文信息"), socket);//NOSONAR
        } catch (FtpException e) {
            log.error("处理业务失败", e);
            MessDealTool.sendBackMes(ResultDtoTool.buildError(e.getCode(), e.getMessage()), socket);
        } catch (Exception e) {
            log.error("Socket分配处理异常", e);
            MessDealTool.sendBackMes(ResultDtoTool.buildError("不能识别报文信息"), socket);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("关闭BusDealSer服务socket出错", e);
                }
            }
        }
    }

    private void runInner() throws FtpException {//NOSONAR
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.fromObject(mess);
        } catch (JSONException e) {
            log.debug("报文格式不正确或报文长度错误", e);
            MessDealTool.sendBackMes(ResultDtoTool.buildError("报文格式不正确或报文长度错误"), socket);
            return;
        }

        String target = MessDealTool.getString(jsonObject, "target");

        if (null == target) {
            log.error("操作目标为空");
            MessDealTool.sendBackMes(ResultDtoTool.buildError("操作目标不能为空"), socket);
            return;
        }

        if (target.equalsIgnoreCase(CFG)) {

            new NodeDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(NODES)) {

            new NodesDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(USER)) {

            new UserDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(SERVICE)) {

            new ServiceDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(FLOW)) {

            new FlowDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(COMPONENT)) {

            new ComponentDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(CRONTAB)) {

            new CrontabDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(FILE)) {

            new FileDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(AUTH)) {

            new AuthDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(FILE_CLEAN)) {

            new FileCleanDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(ROUTE)) {

            new RouteDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(SYSINFO)) {

            new SysInfoDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(CFGSYNC)) {

            new NodeSyncDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(FILE_RENAME)) {

            new FileRenameDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(VERSION)) {

            new VersionDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(BIZFILE)) {

            new BizFileDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(FILE_MSG_2_CLIENT)) {

            new FileMsg2ClientDeal().dealMess(jsonObject, socket);

        } else if (target.equalsIgnoreCase(VSYS_MAP)) {

            new VsysmapDeal().dealMess(jsonObject, socket);


        }else if (target.equalsIgnoreCase(CLIENT_STATUS_INFO)) {

            new ClientStatusDeal().dealMess(jsonObject, socket);

        } else if (STOP_SERVER.equals(target)) {
            String hostAddress = socket.getInetAddress().getHostAddress();
            log.info("{}请求关闭应用", hostAddress);
            //只允许本机或管理IP请求关闭应用
            String managerIp = FtpConfig.getInstance().getManagerIp();
            if ("127.0.0.1".equals(hostAddress) || StringUtils.equals(managerIp, hostAddress)) {//NOSONAR
                log.info("准备关闭应用...");
                System.exit(0);
                log.info("关闭应用成功");
                IOUtils.closeQuietly(socket);
            } else {
                MessDealTool.sendBackMes(ResultDtoTool.buildError("无权限"), socket);
            }
        }else if (target.equalsIgnoreCase(KEYS)){
            new KeyDeal().dealMess(jsonObject, socket);
        } else {
            MessDealTool.sendBackMes(ResultDtoTool.buildError("不能识别报文信息"), socket);
        }
    }

    /**
     * 读取一定长度的socket输入数据
     *
     * @param num socket输入数据的长度
     * @return
     * @throws FtpException
     * @throws SocketTimeoutException
     */
    private byte[] readNum(InputStream is, int num) throws FtpException, SocketTimeoutException {
        // 最大读取请求数据长度1M
        if (num > UnitCons.ONE_MB) {
            log.error("请求数据长度过长，length=[{}]", num);
            throw new FtpException(FtpErrCode.READ_REQ_LENGTH_ERROR);
        }

        byte[] data = new byte[num];
        int offset = 0;
        int count = -1;
        try {
            while (-1 != (count = is.read(data, offset, num - offset))) {
                offset += count;
                if (offset == num)
                    break;
            }
        } catch (SocketTimeoutException ste) {
            throw ste;
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        }
        return data;
    }
}
