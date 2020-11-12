package com.dcfs.esb.ftp.impls.component;

import com.dcfs.esb.ftp.adapter.TCPAdapter;
import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.cons.NodeState;
import com.dcfs.esb.ftp.distribute.DistributeConnector;
import com.dcfs.esb.ftp.distribute.DistributeFilePut;
import com.dcfs.esb.ftp.distribute.DistributeFilePutByStream;
import com.dcfs.esb.ftp.helper.BizFileHelper;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.impls.filetail.FileTailer;
import com.dcfs.esb.ftp.impls.filetail.FileTailerManager;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.impls.uuid.UUIDService;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.msggenerator.FileDataParam;
import com.dcfs.esb.ftp.msggenerator.MsgGenFactory;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.cmd.DoReceive;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.file.EsbFileManager;
import com.dcfs.esb.ftp.server.helper.NetworkSpeedCtrlHelper;
import com.dcfs.esb.ftp.server.invoke.file.FileDeal;
import com.dcfs.esb.ftp.server.invoke.node.NodeWorker;
import com.dcfs.esb.ftp.server.invoke.node.NodesWorker;
import com.dcfs.esb.ftp.server.model.FileDistributeRecord;
import com.dcfs.esb.ftp.server.model.FileSaveRecord;
import com.dcfs.esb.ftp.server.model.SameFileDeleteRecord;
import com.dcfs.esb.ftp.server.system.IProtocol;
import com.dcfs.esb.ftp.server.system.ProtocolFactory;
import com.dcfs.esb.ftp.server.system.SystemInfo;
import com.dcfs.esb.ftp.server.system.SystemManage;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.ThreadSleepUtil;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by mocg on 2016/10/21.
 */
public class UploadFileComponent implements IFileComponent {
    private static final Logger log = LoggerFactory.getLogger(UploadFileComponent.class);
    public static boolean forTest = false;//NOSONAR
    public static int sleepSeconds = 5;//NOSONAR
    private static final long DEF_USE_TAILER_FILE_SIZE = 1024L * 1024;//文件大于 1M 才能使用tail方式
    private static long useTailerFileSize = DEF_USE_TAILER_FILE_SIZE;
    private CachedContext context;
    private FileMsgBean bean;
    private FtpConnector conn;
    private EsbFile esbFile;
    private boolean isLastPiece;
    private long fileVersion;
    private String sysname;
    private String nodeName;
    private FileTailer tailer;
    private FileTailerManager tailerManager;
    private boolean distributeByTailerErr = false;//通过tailer文件分发出错
    private DistributeFilePutByStream filePutByStream;
    private String toNodeName;
    private long nano;
    private ContextBean cxtBean;

    @Override
    public EsbFile create(CachedContext context, FileMsgBean bean, FtpConnector conn) throws FtpException {
        this.context = context;
        this.bean = bean;
        this.conn = conn;
        cxtBean = context.getCxtBean();
        nano = cxtBean.getNano();
        sysname = cxtBean.getSysname();
        nodeName = cxtBean.getNodeName();
        String fn = bean.getFileName();
        //重命名，加上随机数,防止重复
        //fn=renameByRandom(fn); 改成流程实现
        //bean.setFileName(fn);//NOSONAR
        log.debug("nano:{}#上传文件：{}", nano, bean.getFileName());
        esbFile = new EsbFile(fn, EsbFile.SERVER, bean, context);
        cxtBean.setEsbFile(esbFile);
        return esbFile;
    }

    @Override
    public void preProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {
        //使用原文件名时才使用lock
        if (!cxtBean.isFileRename() && !file.lock()) {
            throw new FtpException(FtpErrCode.LOCK_FILE_ERROR);
        }
        file.openForWrite(bean.getOffset());
        file.setFileVersion(UUIDService.nextId());
        file.setFileProperties(bean);
        file.flushFileProperties();
        //tailer
        if (bean.getFileSize() > useTailerFileSize) {
            tailer = new FileTailer(file.getTmpFile(), 1000, true);//NOSONAR
            tailer.setNano(nano);
            tailer.addTailListener(new FileTailListener4Upload(this, context, bean, file));
            tailer.setFinishListener(new FileTailFinishListener(this, context, bean, file));
            tailer.setMaxFilePointer(bean.getFileSize());
            tailer.setWaitingFinish(true);
            tailerManager = new FileTailerManager(tailer);
        } else distributeByTailerErr = true;
        //上传条件准备完毕
        if (context.getCxtBean().isPreProcessReturn()) conn.writeHead(bean);
    }

    @Override
    public void process(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {
        fileVersion = file.getFileVersion();//StringTool.tolong(file.getFilePropertie("version"));//NOSONAR
        context.getCxtBean().setFileVersion(fileVersion);
        if (tailerManager != null) tailerManager.start();
        upload();
    }

    @Override
    public void afterProcess(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) throws FtpException {//NOSONAR
        CapabilityDebugHelper.markCurrTime("UploadFileComponent-getSystemInfo0");
        SystemInfo systemInfo = SystemManage.getInstance().getSystemInfo(bean.getTarSysName());
        CapabilityDebugHelper.markCurrTime("UploadFileComponent-getSystemInfo1");
        if (systemInfo != null) {
            String localFileName = EsbFileManager.getInstance().getFileAbsolutePath(bean.getFileName());
            File f = new File(localFileName);
            if (f.exists()) {
                log.debug("nano:{}#文件{}上传到系统:{}#ip:{}", nano, localFileName, systemInfo.getName(), systemInfo.getIp());
                String remoteFileName = bean.getTarFileName();
                IProtocol protocol = ProtocolFactory.getProtocol(systemInfo, localFileName, remoteFileName);
                if (protocol != null) protocol.uploadBySync();
                else log.error("protocol is null");
            }
        }
        //使用原文件名上传时,删除其他节点上的同名文件
        if ("0".equals(bean.getFileRenameCtrl()) || "00".equals(bean.getFileRenameCtrl())) {
            boolean delSuss = delSameFileFromOthers(context, bean, file);
            if (!delSuss) {
                bean.setErrCode(FtpErrCode.DEL_SAME_FILE_FROM_OTHERS_ERROR);
                bean.setFileRetMsg("删除其他节点同名文件失败#nano:" + nano);
                file.deleteFile();
                return;
            }
        }

        if (isLastPiece) {
            CapabilityDebugHelper.markCurrTime("UploadFileComponent-isLastPiece0");
            try {
                if (tailerManager != null) tailerManager.stopTailing4ReplaceFile();
                file.finish();
                if (tailerManager != null) tailerManager.reopenFile(file.getFile());
                if (tailerManager != null) tailerManager.setWaitingFinish(false);
            } catch (Exception e) {
                throw new FtpException(FtpErrCode.FILE_LAST_PIECE_ERROR, e);
            }
            //保存操作记录
            saveOptLog(context, bean);
            //如果tailer分发失败，则重新开始以filePut方式分发到其他节点
            if (distributeByTailerErr) distributeToOthers(context, bean, file);
            CapabilityDebugHelper.markCurrTime("UploadFileComponent-isLastPiece1");
        }
    }

    @Override
    public void finish(CachedContext context, FileMsgBean bean, FtpConnector conn, EsbFile file) {
        SysContent.getInstance().minusNetworkSpeed(context);
        try {
            finish2(conn, bean);
        } catch (Exception e) {
            log.error("nano:{}#finish返回结果err", nano, e);
        }
        //
        if (log.isDebugEnabled()) {
            log.debug("nano:{}#tailerState#{}", nano, tailer == null ? "tailer is null" : tailer.state());
        }
        if (FLowHelper.hasError(context) && tailerManager != null) tailerManager.stopTailing(true);
        if (file != null) {
            try {
                file.close();
            } catch (Exception e) {
                log.error("nano:{}#资源回收失败", nano, e);
            }
        }
    }

    /**
     * 返回finish响应的头信息
     *
     * @param conn
     * @param bean
     * @throws FtpException
     */
    private void finish2(FtpConnector conn, FileMsgBean bean) throws FtpException {
        conn.writeHead(bean, false);
    }

    private void upload() throws FtpException {
        // 在这里循环读取文件分片大小，这可以在设置当前渠道睡眠以及获取当前时间速度
        //实时保护,第一次和每隔distance次做一次网速计算
        long contLenSum = 0;
        long maxFileSize = cxtBean.getMaxFileSize();
        boolean first = true;
        SysContent sysContent = SysContent.getInstance();
        long index = 0;
        long index2 = 0;
        final int distance = 5;
        long startTime = System.currentTimeMillis();
        long contLenSum2 = 0;//间隔内数据总大小
        while (true) {
            if (contLenSum > maxFileSize) { //超过最大文件大小
                throw new FtpException(FtpErrCode.OUT_OF_SIZE_ERROR);
            }
            //实时保护
            NetworkSpeedCtrlHelper.sleep(context);
            if (index - index2 == distance) sysContent.minusNetworkSpeed(context);

            // 读取请求的头信息，第一次readHead()在上面已经做了
            if (!first) conn.readHead(bean);
            // 根据头信息进行相关的处理
            upProcess(context, conn, bean, esbFile);
            contLenSum += bean.getContLen();
            contLenSum2 += bean.getContLen();

            //第一次和每隔distance次做一次网速计算
            if (index == 0 || index - index2 == distance) {
                long now = System.currentTimeMillis();
                long usedTime = now - startTime + 1;
                long speed = contLenSum2 * 1000 / usedTime;//byte/s //NOSONAR
                //当前请求结束后会自动减去speed
                sysContent.addNetworkSpeed(context, speed);
                index2 = index;
                startTime = now;
                contLenSum2 = 0;
            }

            //for test
            if (forTest) ThreadSleepUtil.sleepSecondIngoreEx(sleepSeconds);
            // 最后一个分片，则退出
            if (BooleanTool.toBoolean(bean.isLastPiece())) {
                log.debug("nano:{}#处理到最后一个分片，结束处理", nano);
                break;
            }
            first = false;
            index++;
        }
    }

    private void upProcess(CachedContext context, FtpConnector conn, FileMsgBean bean, EsbFile file) throws FtpException {
        conn.readCont(bean);
        DoReceive rece = new DoReceive();
        rece.doCommand(bean, file);
        if (BooleanTool.toBoolean(bean.isLastPiece())) {
            file.setFilePropertie(ContextConstants.ORIGINAL_FILE_PATH, (String) context.get(ContextConstants.ORIGINAL_FILE_PATH));
            file.setFilePropertie(ContextConstants.FILE_POWER_PATH, (String) context.get(ContextConstants.FILE_POWER_PATH));
            Date uploadStartTime = new Date(cxtBean.getTimestamp1());
            file.setFilePropertie(ContextConstants.UPLOAD_START_TIME, DateFormatUtils.format(uploadStartTime, ContextConstants.DATE_FORMAT_PATT));
            Date uploadEndTime = new Date();
            context.put(ContextConstants.UPLOAD_END_TIME, uploadEndTime);
            file.setFilePropertie(ContextConstants.UPLOAD_END_TIME, DateFormatUtils.format(uploadEndTime, ContextConstants.DATE_FORMAT_PATT));
            file.checkMd5(bean.getMd5());
            isLastPiece = true;
        }

        if (context.getCxtBean().isProcessReturn()) conn.writeHead(bean, false);
    }


    /**
     * 使用原文件名上传时,删除其他节点上的同名文件
     *
     * @param context
     * @param bean
     * @param file
     */
    private boolean delSameFileFromOthers(CachedContext context, FileMsgBean bean, EsbFile file) {//NOSONAR
        log.debug("nano:{}#开始删除其他节点上的同名文件...", nano);
        final String fileName = bean.getFileName();
        final String nodeName = cxtBean.getNodeName();//NOSONAR
        //String sysname = fileName.substring(1, fileName.indexOf('/', 1));//NOSONAR
        String sysname = NodeWorker.getInstance().getSysName();//NOSONAR
        List<Node> nodeList = NodesWorker.getInstance().getDataNodesBySystemExcludeNode(sysname, nodeName, 1);
        FileDataParam param = new FileDataParam();
        param.setKey(fileName);
        String delFileParam = MsgGenFactory.fileParam(param, FileDeal.DEL_FILE);
        boolean delAllSucc = true;
        boolean hasIOException = false;
        for (Node node : nodeList) {
            int cmdPort = node.getCmdPort();
            String ip = node.getIp();
            try {
                TCPAdapter tcpAdapter = new TCPAdapter(ip, cmdPort, 1000);//NOSONAR
                ResultDto<String> dto = tcpAdapter.invoke(delFileParam, String.class);
                log.debug("nano:{}#删除其他节点上的同名文件invoke#fileName:{},ip:{},port:{},data:{}", nano, fileName, ip, cmdPort, dto.getData());
                if (CommGlobalCons.SUCC_CODE.equals(dto.getCode())) {
                    String data = dto.getData();
                    if (!("notExists".equals(data) || "delSuss".equals(data) || "sameVersion".equals(data))) {
                        delAllSucc = false;
                    }
                } else delAllSucc = false;
            } catch (Exception e) {
                //delAllSucc = false;//NOSONAR
                hasIOException = true;
                log.error("nano:{}#删除其他节点上的同名文件err#ip:{},port:{}", nano, ip, cmdPort, e);
            }
        }
        log.debug("nano:{}#删除其他节点上的同名文件结果:{}#{}", nano, delAllSucc, fileName);
        int stopNodeCount = 0;
        if (!hasIOException) {
            stopNodeCount = NodesWorker.getInstance().countNodes(sysname, NodeState.STOP);
            log.debug("nano:{}#删除其他节点上的同名文件#停止节点数:{}#{}", nano, stopNodeCount, sysname);
        }
        //如果有节点离线或网络出错则发送kafka消息 record.getSysname().toUpperCase() + "_" +KfkTopic.EFS_FILE_DEL_SAME
        if (hasIOException || stopNodeCount > 0) {
            SameFileDeleteRecord record = new SameFileDeleteRecord();
            record.setFromNodeName(nodeName);
            record.setSysname(sysname);
            record.setFilePath(fileName);
            record.setNewVersion(fileVersion);
            record.setDelTime(new Date());
            EsbFileService.getInstance().send(record);
        }
        return delAllSucc;
    }

    /**
     * 保存操作记录
     *
     * @param context
     * @param bean
     */
    private void saveOptLog(CachedContext context, FileMsgBean bean) {
        String nodeName = FtpConfig.getInstance().getNodeName();//NOSONAR
        String sysname = cxtBean.getSysname();//NOSONAR
        String uname = bean.getUid();
        long timeStamp1 = cxtBean.getTimestamp1();

        FileSaveRecord saveRecord = new FileSaveRecord();
        saveRecord.setNodeName(nodeName);
        saveRecord.setSystemName(sysname);
        saveRecord.setClientUserName(uname);
        saveRecord.setFilePath(bean.getFileName());
        saveRecord.setRequestFilePath(bean.getFileName());
        //saveRecord.setFileName(getFileNameByPath(bean.getFileName()));//NOSONAR
        saveRecord.setClientFilePath(bean.getClientFileName());
        //saveRecord.setClientFileName(getFileNameByPath(bean.getClientFileName()));//NOSONAR
        saveRecord.setOriginalFilePath((String) context.get(ContextConstants.ORIGINAL_FILE_PATH));
        saveRecord.setClientIp(bean.getClientIp());
        saveRecord.setFileSize(bean.getFileSize());
        //saveRecord.setFileExt(FilenameUtils.getExtension(bean.getFileName()));//NOSONAR
        saveRecord.setUploadStartTime(new Date(timeStamp1));
        saveRecord.setUploadEndTime((Date) context.get(ContextConstants.UPLOAD_END_TIME));
        saveRecord.setState(0);
        //saveRecord.setFileMd5(ScrtUtil.encryptEsb(bean.getMd5()));//NOSONAR
        saveRecord.setFileVersion(fileVersion);
        BizFileHelper.setFileNameExt(saveRecord);
        saveRecord.setNano(bean.getNano());
        saveRecord.setFlowNo(bean.getFlowNo());
        EsbFileService.getInstance().save(saveRecord);
    }

    /**
     * 分发到其他节点
     *
     * @param context
     * @param bean
     * @param file
     */
    private void distributeToOthers(CachedContext context, final FileMsgBean bean, final EsbFile file) {//NOSONAR
        boolean hasError = FLowHelper.hasError(context);
        log.debug("nano:{}#开始文件分发...context hasError:{}", nano, hasError);
        if (hasError) return;
        final String realFileName = file.getRealFileName();
        final String fileName = bean.getFileName();
        //final String nodeName = (String) context.get(ContextConstants.NODE_NAME);//NOSONAR
        //final String sysname = fileName.substring(1, fileName.indexOf('/', 1));//NOSONAR
        //final String sysname = NodeManager.getInstance().getSysName();//NOSONAR
        new Thread(new Runnable() {
            @Override
            public void run() {
                //分发到同一个系统的其他节点，随机一个
                List<Node> nodeList = getAliveSameSysNodes(sysname, nodeName);
                FileDistributeRecord distributeRecord = new FileDistributeRecord();
                distributeRecord.setSysname(sysname);
                distributeRecord.setNodeName(nodeName);
                distributeRecord.setFileName(fileName);
                distributeRecord.setRealFileName(realFileName);
                distributeRecord.setFileVersion(fileVersion);
                if (nodeList.isEmpty()) {
                    log.debug("nano:{}#文件分发#在同一个系统中没有其他节点#sysname:{},fileVersion:{}", nano, sysname, fileVersion);
                    distributeRecord.setState(0);
                } else {
                    boolean distributeSuss = false;
                    //直到有一个节点分发成功
                    while (!nodeList.isEmpty()) {
                        int index = RandomUtils.nextInt(nodeList.size());
                        Node node = nodeList.get(index);
                        nodeList.remove(index);
                        String ip = node.getIp();
                        int receivePort = node.getReceivePort();
                        try {
                            boolean suss = distributeToOne(ip, receivePort, realFileName, fileName);
                            log.debug("nano:{}#分发到其他节点成功?{}#ip:{},fileName:{},fileVersion:{}"
                                    , nano, suss, ip, fileName, fileVersion);
                            if (suss) {
                                distributeSuss = true;
                                break;
                            }
                        } catch (IOException e) {
                            log.error("nano:{}#分发到其他节点出错#ip:{},receivePort:{},fileName:{},fileVersion:{}"
                                    , nano, ip, receivePort, fileName, fileVersion, e);
                        }
                    }
                    distributeRecord.setState(distributeSuss ? 1 : -1);
                }
                log.debug("nano:{}#文件分发结束.fileName:{},fileVersion:{}", nano, fileName, fileVersion);
                EsbFileService.getInstance().save(distributeRecord);
            }
        }).start();
    }

    private List<Node> getAliveSameSysNodes(String sysname, String nodeName) {
        return NodesWorker.getInstance().getDataNodesBySystemExcludeNode(sysname, nodeName, 1);
    }

    private boolean distributeToOne(String ip, int receivePort, String localFileName, String remoteFileName) throws IOException {
        DistributeConnector conn = new DistributeConnector(ip, receivePort);//NOSONAR
        DistributeFilePut filePut = new DistributeFilePut(localFileName, remoteFileName, conn);
        return filePut.doPut();
    }

    public static long getUseTailerFileSize() {
        return useTailerFileSize;
    }

    public static void setUseTailerFileSize(long useTailerFileSize) {
        UploadFileComponent.useTailerFileSize = useTailerFileSize;
    }

    private static class FileTailListener4Upload implements FileTailer.TailListener {
        private final UploadFileComponent component;
        private final CachedContext context;
        private final FileMsgBean bean;
        private final EsbFile file;
        private String sysname;
        private String nodeName;
        private String toNodeName = null;
        private boolean inited;
        private DistributeConnector conn;
        private DistributeFilePutByStream filePutByStream;


        public FileTailListener4Upload(UploadFileComponent uploadFileComponent, CachedContext context, FileMsgBean bean, EsbFile file) {
            this.component = uploadFileComponent;
            this.context = context;
            this.bean = bean;
            this.file = file;
        }

        private void init() {//NOSONAR
            if (inited) return;
            inited = true;
            sysname = component.sysname;
            nodeName = component.nodeName;

            List<Node> nodeList = NodesWorker.getInstance().getDataNodesBySystemExcludeNode(sysname, nodeName, 1);
            if (nodeList.isEmpty()) {
                component.distributeByTailerErr = true;
                if (component.tailerManager != null) component.tailerManager.stopTailing(true);
            } else {
                boolean succ = false;
                for (Node node : nodeList) {
                    try {
                        conn = new DistributeConnector(node.getIp(), node.getReceivePort());
                        filePutByStream = new DistributeFilePutByStream(bean.getFileSize(), bean.getFileName(), conn);
                        succ = filePutByStream.doAuth();
                    } catch (IOException e) {
                        log.debug("nano:{}#conn fail#toNodeName:{},ip:{}", component.nano, node.getName(), node.getIp(), e);
                    } finally {
                        if (!succ) {
                            try {
                                if (conn != null) conn.close();
                            } catch (IOException ignored) {
                                //nothing
                            }
                        }
                    }
                    if (succ) {
                        toNodeName = node.getName();
                        log.debug("nano:{}#conn succ#toNodeName:{},ip:{},receivePort:{}", component.nano, toNodeName, node.getIp(), node.getReceivePort());
                        break;
                    }
                }
                if (succ) {
                    component.filePutByStream = filePutByStream;
                } else {
                    component.distributeByTailerErr = true;
                    if (component.tailerManager != null) component.tailerManager.stopTailing(true);
                }
            }
            log.debug("nano:{}#toNodeName:{}", component.nano, toNodeName);
            component.toNodeName = toNodeName;
        }

        @Override
        public void fireMoreTail(byte[] bytes, int start, int len) {
            if (!inited) init();
            if (component.distributeByTailerErr) return;
            //log.debug("fireMoreTail#len:{},toNodeName:{}", len, toNodeName);//NOSONAR
            try {
                filePutByStream.putFile(bytes, start, len);
            } catch (Exception e) {
                component.distributeByTailerErr = true;
                if (component.tailerManager != null) component.tailerManager.stopTailing(true);
                log.error("nano:{}#", component.nano, e);
            }
        }
    }

    private static class FileTailFinishListener implements FileTailer.FinishListener {
        private final UploadFileComponent component;
        private final CachedContext context;
        private final FileMsgBean bean;
        private final EsbFile file;

        public FileTailFinishListener(UploadFileComponent uploadFileComponent, CachedContext context, FileMsgBean bean, EsbFile file) {
            this.component = uploadFileComponent;
            this.context = context;
            this.bean = bean;
            this.file = file;
        }

        @Override
        public void finish() {
            log.debug("nano:{}#FileTailFinishListener finish...toNodeName:{}", component.nano, component.toNodeName);
            if (component.distributeByTailerErr) return;
            if (FLowHelper.hasError(context)) return;
            boolean succ = false;
            if (component.filePutByStream != null) {
                try {
                    component.filePutByStream.putFile(new byte[0], 0, 0);
                    component.filePutByStream.putPropertiesFile(file.getCfgFile());
                    Integer result = component.filePutByStream.finishReceive();
                    succ = result != null && result == 1;
                } catch (Exception e) {
                    log.error("nano:{}#FileTailFinishListener putFile err", component.nano, e);
                } finally {
                    component.filePutByStream.close();
                }
            }
            log.debug("nano:{}#distributeSucc:{},toNodeName:{}#{}", component.nano, succ, component.toNodeName, bean.getFileName());
            //如果tailer分发失败，则重新开始以filePut方式分发到其他节点
            if (!succ) {
                component.distributeToOthers(context, bean, file);
            }
        }
    }
}
