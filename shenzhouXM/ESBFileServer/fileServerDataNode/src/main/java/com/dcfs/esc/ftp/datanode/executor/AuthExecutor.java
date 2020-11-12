package com.dcfs.esc.ftp.datanode.executor;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.helper.FilePropertiesHelper;
import com.dcfs.esb.ftp.impls.uuid.UUIDService;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.file.EsbFileWorker;
import com.dcfs.esc.ftp.comm.dto.*;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.DownloadContextBean;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;
import com.dcfs.esc.ftp.datanode.file.EscFile;
import com.dcfs.esc.ftp.datanode.service.AuthService;
import com.dcfs.esc.ftp.datanode.spring.ServiceBeans;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by mocg on 2017/6/3.
 */
public class AuthExecutor extends BaseBusinessExecutor {

    //authCheck apiversion ip pwd dirAuth tranCodeAuth ResourceCtrlService
    private AuthService authService = ServiceBeans.getAuthService();

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends BaseDto & RspDto, D extends BaseDto & ReqDto> T invoke0(ChannelContext channelContext, D dto, Class<T> tClass) throws Exception {
        BaseDto target = null;
        if (tClass == FileUploadAuthRspDto.class && dto instanceof FileUploadAuthReqDto) {
            FileUploadAuthReqDto reqDto = (FileUploadAuthReqDto) dto;
            FileUploadAuthRspDto rspDto = doAuthFileUpload(channelContext, reqDto);
            target = rspDto;
        } else if (tClass == FileDownloadAuthRspDto.class && dto instanceof FileDownloadAuthReqDto) {
            FileDownloadAuthReqDto reqDto = (FileDownloadAuthReqDto) dto;
            FileDownloadAuthRspDto rspDto = doAuthFileDownload(channelContext, reqDto);
            target = rspDto;
        }

        return (T) target;
    }

    private FileUploadAuthRspDto doAuthFileUpload(ChannelContext channelContext, FileUploadAuthReqDto reqDto) throws FtpException {
        log.info("nano:{}#flowNo:{}#start doAuthFileUpload...", channelContext.getNano(), reqDto.getFlowNo());
        channelContext.setFlowNo(reqDto.getFlowNo());
        UploadContextBean cxtBean = channelContext.cxtBean();
        cxtBean.setSysname(reqDto.getSysname());
        cxtBean.setUid(reqDto.getUid());
        cxtBean.setPasswd(reqDto.getPasswd());
        cxtBean.setFileName(reqDto.getFileName());
        cxtBean.setClientFileName(reqDto.getClientFileName());
        cxtBean.setTranCode(reqDto.getTranCode());
        cxtBean.setTargetSysname(reqDto.getTargetSysname());
        cxtBean.setTargetFileName(reqDto.getTargetFileName());
        cxtBean.setPack(reqDto.isPack());
        cxtBean.setDontRoute(reqDto.isDontRoute());
        cxtBean.setFlowNo(reqDto.getFlowNo());
        cxtBean.setUploadId(reqDto.getUploadId());

        String orgiFileName = reqDto.getFileName();

        FileUploadAuthRspDto rspDto = authService.doAuth(channelContext, cxtBean, reqDto);

        boolean isAuth = rspDto.isAuth();
        boolean redirect = rspDto.isRedirect();
        //认证通过且不重定向
        if (isAuth && !redirect) {
            cxtBean.setFileSize(reqDto.getFileSize());
            cxtBean.setFileRename(reqDto.isFileRename());
            //上传文件将保存到此
            String svrFilePath = cxtBean.getSvrFilePath();
            String fileAbsolutePath = EsbFileWorker.getInstance().getFileAbsolutePath(svrFilePath);
            cxtBean.setAbsFilePath(fileAbsolutePath);
            //如果不指定目标地址，使用此节点上的文件路径svrFilePath
            if (StringUtils.isEmpty(reqDto.getTargetFileName())) cxtBean.setTargetFileName(svrFilePath);

            EscFile escFile = new EscFile(svrFilePath, EsbFile.SERVER, null, cxtBean.getLastTmpFilePath());
            //to-do 暂时继续esbFile
            cxtBean.setEsbFile(escFile);
            escFile.setNano(channelContext.getNano());
            escFile.setFlowNo(channelContext.getFlowNo());
            //使用原文件名时才使用lock
            if (!reqDto.isFileRename() && !escFile.lock()) {
                throw new FtpException(FtpErrCode.LOCK_FILE_ERROR);
            }
            FileMsgBean fileMsgBean = cxtBean.getFileMsgBean();
            long position = cxtBean.getPosition();
            escFile.openForWrite(position);
            long fileVersion = UUIDService.nextId();
            cxtBean.setFileVersion(fileVersion);
            escFile.setFileVersion(fileVersion);
            fileMsgBean.setTarSysName(reqDto.getTargetSysname());
            fileMsgBean.setTarFileName(reqDto.getTargetFileName());
            escFile.setFilePropertie("orgiFileName", orgiFileName);
            escFile.setFilePropertie("nano", String.valueOf(cxtBean.getNano()));
            escFile.setFilePropertie("flowNo", cxtBean.getFlowNo());
            escFile.setFilePropertie("pack", String.valueOf(reqDto.isPack()));
            escFile.setFilePropertie("tags", reqDto.getTags());
            escFile.setFileProperties(fileMsgBean);
            /*Date now = new Date()
            escFile.setFilePropertie("ClientIp", channelContext.getUserIp())
            escFile.setFilePropertie("FileName", filePath)
            escFile.setFilePropertie("ClientFileName", reqDto.getClientFileName())
            escFile.setFilePropertie("User", reqDto.getUid())
            escFile.setFilePropertie("CreateTime", DateFormatUtils.format(now, ContextConstants.DATE_FORMAT_PATT))
            escFile.setFilePropertie("FileSize", Long.toString(reqDto.getFileSize()))
            escFile.setFilePropertie("version", Long.toString(fileVersion == 0 ? now.getTime() : fileVersion))
            escFile.setFilePropertie("tmpFile", escFile.getTmpFileName()) */
            escFile.flushFileProperties();
            if (position > 0) {
                try {
                    escFile.reMdD5ForWrite(position);
                } catch (IOException e) {
                    throw new FtpException(FtpErrCode.FILE_WRITE_ERROR, channelContext.getNano(), e);
                }
            }

            rspDto.setTmpFileName(escFile.getFileName());
            //rspDto.setRemoteFileSize(NullDefTool.defNull(fileMsgBean.getRemoteFileSize(), 0L))
        }
        return rspDto;
    }

    private FileDownloadAuthRspDto doAuthFileDownload(ChannelContext channelContext, FileDownloadAuthReqDto reqDto) throws FtpException, FileNotFoundException {
        log.info("nano:{}#flowNo:{}#start doAuthFileDownload...", channelContext.getNano(), reqDto.getFlowNo());
        channelContext.setFlowNo(reqDto.getFlowNo());
        DownloadContextBean cxtBean = channelContext.cxtBean();
        cxtBean.setSysname(reqDto.getSysname());
        cxtBean.setUid(reqDto.getUid());
        cxtBean.setPasswd(reqDto.getPasswd());
        cxtBean.setFileName(reqDto.getFileName());
        cxtBean.setClientFileName(reqDto.getClientFileName());
        cxtBean.setTranCode(reqDto.getTranCode());
        cxtBean.setTargetSysname(reqDto.getTargetSysname());
        cxtBean.setTargetFileName(reqDto.getTargetFileName());
        cxtBean.setFlowNo(reqDto.getFlowNo());
        cxtBean.setDownloadId(reqDto.getDownloadId());

        FileDownloadAuthRspDto rspDto = authService.doAuth(channelContext, cxtBean, reqDto);
        boolean isAuth = rspDto.isAuth();
        boolean fileExists = rspDto.isFileExists();
        boolean redirect = rspDto.isRedirect();
        //认证通过文件存在且不重定向
        if (isAuth && fileExists && !redirect) {
            String absFilePath = cxtBean.getAbsFilePath();
            File file = new File(absFilePath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            cxtBean.setRaf(raf);
            FilePropertiesHelper helper = new FilePropertiesHelper();
            helper.load(absFilePath);
            long fileVersion = helper.getFileVersion();
            boolean pack = helper.isPack();
            cxtBean.setFileVersion(fileVersion);
            cxtBean.setPack(pack);
            rspDto.setFileVersion(fileVersion);
            rspDto.setPack(pack);
            rspDto.setTags(helper.getTags());
            rspDto.setFileSize(file.length());
            //todo jwt
            //String authSeq = String.valueOf(new Random().nextLong())
            //rspDto.setAuthSeq(authSeq)
        }
        return rspDto;
    }
}
