package com.dcfs.esc.ftp.datanode.process.handler;

import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.distribute.DistributeConnector;
import com.dcfs.esb.ftp.distribute.DistributeFilePut;
import com.dcfs.esb.ftp.distribute.DistributeFilePutByStream;
import com.dcfs.esb.ftp.impls.filetail.FileTailer;
import com.dcfs.esb.ftp.impls.filetail.FileTailerManager;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.invoke.node.NodesWorker;
import com.dcfs.esb.ftp.server.model.FileDistributeRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.EmptyUtils;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import com.dcfs.esc.ftp.comm.constant.UnitCons;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by mocg on 2017/6/6.
 */
public class FileDistributeByTailHandler extends UploadProcessHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(FileDistributeByTailHandler.class);

    private static long useTailerFileSize = UnitCons.ONE_MB;//文件大于 1M 才能使用tail方式
    private FileTailerManager tailerManager;
    private long nano;
    private String flowNo;

    private String sysname;
    private String nodeName;
    private long fileVersion;
    private boolean distributeByTailerErr = false;//通过tailer文件分发出错
    private DistributeFilePutByStream filePutByStream;
    private String toNodeName;

    private UploadContextBean cxtBean;
    private ProcessHandlerContext ctx;

    @Override
    public void start(ProcessHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        nano = ctx.getChannelContext().getNano();
        flowNo = ctx.getChannelContext().getFlowNo();
        String tailFilePath = (String) ctx.getMap().get(CommGlobalCons.TAIL_FILE_PATH_KEY);
        if (tailFilePath == null) return;
        cxtBean = ctx.getChannelContext().cxtBean();
        sysname = cxtBean.getSysname();
        nodeName = FtpConfig.getInstance().getNodeName();
        fileVersion = cxtBean.getEsbFile().getFileVersion();
        FileMsgBean fileMsgBean = cxtBean.getFileMsgBean();
        fileMsgBean.setFileSize(cxtBean.getFileSize());
        //tailer
        if (cxtBean.getFileSize() > useTailerFileSize) {
            File tailFile = new File(tailFilePath);
            final int sleepInterval = 2000;
            FileTailer tailer = new FileTailer(tailFile, sleepInterval, true);
            tailer.setNano(nano);
            tailer.setFlowNo(flowNo);
            tailer.setWaitingFinish(true);
            tailer.setMaxFilePointer(cxtBean.getFileSize());
            tailer.addTailListener(new FileTailListener4Upload(this, fileMsgBean));
            tailer.setFinishListener(new FileTailFinishListener(this, cxtBean.getEsbContext(), fileMsgBean, cxtBean.getEsbFile()));
            tailerManager = new FileTailerManager(tailer);
            ctx.getMap().put(SvrGlobalCons.TAILER_MANAGER_KEY, tailerManager);
            ctx.setDistributeByTail(true);
            tailerManager.start();
            ctx.setDistributeResult(1);
        }
    }

    @Override
    public void finish(final ProcessHandlerContext ctx) throws Exception {
        if (ctx.isFileloadSucc() && !ctx.isDistributeByTail()) {
            distributeToOthers(cxtBean.getEsbContext(), cxtBean.getFileMsgBean(), cxtBean.getEsbFile());
        }
    }

    @Override
    public void exceptionCaught(ProcessHandlerContext ctx, Throwable cause) throws Exception {
        if (tailerManager != null) tailerManager.stopTailing(true);
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        if (tailerManager != null) {
            tailerManager.stopTailingAndWait();
        }
    }

    private List<Node> getAliveSameSysNodes(String sysname, String nodeName) {
        return NodesWorker.getInstance().getDataNodesBySystemExcludeNode(sysname, nodeName, 1);
    }

    private boolean distributeToOne(String ip, int receivePort, String localFileName, String remoteFileName) throws IOException {
        DistributeConnector conn = new DistributeConnector(ip, receivePort);
        DistributeFilePut filePut = new DistributeFilePut(localFileName, remoteFileName, conn);
        filePut.setNano(nano);
        filePut.setFlowNo(flowNo);
        return filePut.doPut();
    }

    /**
     * 使用tail方式分发至同系统下其他节点
     */
    private static class FileTailListener4Upload implements FileTailer.TailListener {
        private final FileDistributeByTailHandler component;
        private final FileMsgBean bean;
        private String toNodeName = null;
        private boolean inited;
        private DistributeConnector conn;
        private DistributeFilePutByStream filePutByStream;


        public FileTailListener4Upload(FileDistributeByTailHandler component, FileMsgBean bean) {
            this.component = component;
            this.bean = bean;
        }

        private void init() {//NOSONAR
            if (inited) return;
            inited = true;
            String sysname = component.sysname;
            String nodeName = component.nodeName;

            List<Node> nodeList = NodesWorker.getInstance().getDataNodesBySystemExcludeNode(sysname, nodeName, 1);
            if (nodeList.isEmpty()) {
                component.distributeByTailerErr = true;
                if (component.tailerManager != null) {
                    log.debug("nano:{}#flowNo:{}#没有找到同系统的其他有效的节点，关闭tailer#sysname:{},nodeName:{}", component.nano, component.flowNo, sysname, nodeName);
                    component.tailerManager.stopTailing(true);
                }
            } else {
                boolean succ = false;
                for (Node node : nodeList) {
                    try {
                        conn = new DistributeConnector(node.getIp(), node.getReceivePort());
                        filePutByStream = new DistributeFilePutByStream(bean.getFileSize(), bean.getFileName(), conn);
                        succ = filePutByStream.doAuth();
                    } catch (IOException e) {
                        log.debug("nano:{}#flowNo:{}#conn fail#toNodeName:{},ip:{}", component.nano, component.flowNo, node.getName(), node.getIp(), e);
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
                        log.debug("nano:{}#flowNo:{}#conn succ#toNodeName:{},ip:{},receivePort:{}", component.nano, component.flowNo, toNodeName, node.getIp(), node.getReceivePort());
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
            log.debug("nano:{}#flowNo:{}#toNodeName:{}", component.nano, component.flowNo, toNodeName);
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
                log.error("nano:{}#flowNo:{}#", component.nano, component.flowNo, e);
            }
        }
    }

    /**
     * 非tail方式分发至同系统下其他节点
     *
     * @param context
     * @param bean
     * @param file
     */
    private void distributeToOthers(CachedContext context, final FileMsgBean bean, final EsbFile file) {//NOSONAR
        boolean hasError = FLowHelper.hasError(context);
        log.debug("nano:{}#flowNo:{}#开始文件分发...context hasError:{}", nano, flowNo, hasError);
        if (hasError) {
            ctx.setDistributeResult(-1);
            return;
        }
        final String realFileName = file.getRealFileName();
        final String fileName = bean.getFileName();
        //final String nodeName = (String) context.get(ContextConstants.NODE_NAME);//NOSONAR
        //final String sysname = fileName.substring(1, fileName.indexOf('/', 1));//NOSONAR
        //final String sysname = NodeManager.getInstance().getSysName();//NOSONAR
        new Thread(new Runnable() {
            private int result = 0;

            @Override
            public void run() {
                try {
                    ctx.setDistributeResult(1);
                    doRun();
                } finally {
                    if (result == 1) result = -2;
                    //设置结果，才能跳出同步线程
                    ctx.setDistributeResult(result);
                }
            }

            private void doRun() {
                int distributeNodeNum = FtpConfig.getInstance().getDistributeNodeNum();
                int count = 0;
                //随机分发到同一个系统的其他节点
                List<Node> nodeList = getAliveSameSysNodes(sysname, nodeName);
                FileDistributeRecord distributeRecord = new FileDistributeRecord();
                distributeRecord.setSysname(sysname);
                distributeRecord.setNodeName(nodeName);
                distributeRecord.setFileName(fileName);
                distributeRecord.setRealFileName(realFileName);
                distributeRecord.setFileVersion(fileVersion);
                if (nodeList.isEmpty()) {
                    log.debug("nano:{}#flowNo:{}#文件分发#在同一个系统中没有其他节点#sysname:{},fileVersion:{}", nano, flowNo, sysname, fileVersion);
                    distributeRecord.setState(0);
                    EsbFileService.getInstance().save(distributeRecord);
                } else {
                    while (EmptyUtils.isNotEmpty(nodeList) && count < distributeNodeNum) {
                        count++;
                        int index = RandomUtils.nextInt(nodeList.size());
                        Node node = nodeList.get(index);
                        nodeList.remove(index);
                        String ip = node.getIp();
                        int receivePort = node.getReceivePort();
                        try {
                            boolean suss = distributeToOne(ip, receivePort, realFileName, fileName);
                            log.debug("nano:{}#flowNo:{}#分发到其他节点成功?{}#ip:{},fileName:{},fileVersion:{}"
                                    , nano, flowNo, suss, ip, fileName, fileVersion);
                            if (suss) {
                                distributeRecord.setState(1);
                                EsbFileService.getInstance().save(distributeRecord);
                            }
                        } catch (IOException e) {
                            log.error("nano:{}#flowNo:{}#分发到其他节点出错#ip:{},receivePort:{},fileName:{},fileVersion:{}"
                                    , nano, flowNo, ip, receivePort, fileName, fileVersion, e);
                            distributeRecord.setState(-1);
                            EsbFileService.getInstance().save(distributeRecord);
                        }
                    }
                }
                log.debug("nano:{}#flowNo:{}#文件分发结束.fileName:{},fileVersion:{}", nano, flowNo, fileName, fileVersion);
                result = 2;
            }
        }).start();
    }

    public static long getUseTailerFileSize() {
        return useTailerFileSize;
    }

    public static void setUseTailerFileSize(long useTailerFileSize) {
        FileDistributeByTailHandler.useTailerFileSize = useTailerFileSize;
    }

    /**
     * 使用tail方式分发结束后，分发cfg文件
     */
    private static class FileTailFinishListener implements FileTailer.FinishListener {
        private final FileDistributeByTailHandler component;
        private final CachedContext context;
        private final FileMsgBean bean;
        private final EsbFile file;

        public FileTailFinishListener(FileDistributeByTailHandler component, CachedContext context, FileMsgBean bean, EsbFile file) {
            this.component = component;
            this.context = context;
            this.bean = bean;
            this.file = file;
        }

        @Override
        public void finish() {
            log.debug("nano:{}#flowNo:{}#FileTailFinishListener finish...toNodeName:{}", component.nano, component.flowNo, component.toNodeName);
            if (component.distributeByTailerErr || FLowHelper.hasError(context)) {
                component.ctx.setDistributeResult(-1);
                return;
            }
            boolean succ = false;
            if (component.filePutByStream != null) {
                try {
                    component.filePutByStream.putFile(new byte[0], 0, 0);
                    component.filePutByStream.putPropertiesFile(file.getCfgFile());
                    Integer result = component.filePutByStream.finishReceive();
                    succ = result != null && result == 1;
                } catch (Exception e) {
                    log.error("nano:{}#flowNo:{}#FileTailFinishListener putFile err", component.nano, component.flowNo, e);
                } finally {
                    component.filePutByStream.close();
                }
            }
            log.debug("nano:{}#flowNo:{}#distributeSucc:{},toNodeName:{}#{}", component.nano, component.flowNo, succ, component.toNodeName, bean.getFileName());
            //如果tailer分发失败，则重新开始以filePut方式分发到其他节点
            if (!succ) {
                component.ctx.setDistributeResult(1);
                component.distributeToOthers(context, bean, file);
            } else component.ctx.setDistributeResult(2);
        }
    }
}
