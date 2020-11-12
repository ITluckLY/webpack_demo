package com.dcfs.esb.ftp.common.helper;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.msg.FileXmlUtil;
import com.dcfs.esb.ftp.common.scrt.Des;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.StringTool;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mocg on 2016/7/20.
 */
public class FileMsgBeanHelper {
    private static final Logger log = LoggerFactory.getLogger(FileMsgBeanHelper.class);

    private FileMsgBeanHelper() {
    }

    public static void copyJsonToHead(FileMsgBean bean, String json) {
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#convertJsonToBean ->> {}", bean.getNano(), bean.getFlowNo(), json);
        }
        FileMsgBean bean2 = GsonUtil.fromJson(json, FileMsgBean.class);
        copyByNoNull(bean2, bean);
        //
        if (bean2.getPasswd() != null && bean2.getPasswd().startsWith("${3DES}")) {
            bean.setPasswd(ScrtUtil.decrypt3DES(bean2.getPasswd()));
        }
        //加解密
        if (bean2.getDesKeyStr() != null) {
            byte[] scrtKey = ScrtUtil.decodeBase64(bean2.getDesKeyStr());
            bean.setDesKey(Des.decrypt3DES(scrtKey, Des.getDesKey()));
        }
    }

    public static String converToJsonNoFileCont(FileMsgBean bean) {
        FileMsgBean tempBean = new FileMsgBean();
        copyByNoNull(bean, tempBean);
        if (FileMsgTypeHelper.isNeedShowUidPwd(tempBean.getFileMsgFlag())) {
            tempBean.setPasswd(ScrtUtil.encrypt3DES(bean.getPasswd()));
        } else {
            tempBean.setUid(null);
            tempBean.setPasswd(null);
            tempBean.setServerName(null);
            //tempBean.setAuthFlag(null);//NOSONAR
            //tempBean.setScrtFlag(null);//NOSONAR
        }
        tempBean.setFileCont2(null);
        //加解密
        if (tempBean.getDesKey() != null) {
            byte[] scrtKey = Des.encrypt3DES(tempBean.getDesKey(), Des.getDesKey());
            tempBean.setDesKeyStr(ScrtUtil.encodeBase64(scrtKey));
            tempBean.setDesKey(null);
        }
        return GsonUtil.toJson(tempBean);
    }

    public static void copyByNoNull(FileMsgBean src, FileMsgBean target) {//NOSONAR
        if (src.getSysname() != null) target.setSysname(src.getSysname());
        if (src.getClientIp() != null) target.setClientIp(src.getClientIp());
        if (src.getServerIp() != null) target.setServerIp(src.getServerIp());
        if (src.getFileName() != null) target.setFileName(src.getFileName());
        if (src.getClientFileName() != null) target.setClientFileName(src.getClientFileName());
        if (src.getServerName() != null) target.setServerName(src.getServerName());
        if (src.getUid() != null) target.setUid(src.getUid());
        if (src.getPasswd() != null) target.setPasswd(src.getPasswd());
        if (src.getFileMsgFlag() != null) target.setFileMsgFlag(src.getFileMsgFlag());
        if (src.getFileRetMsg() != null) target.setFileRetMsg(src.getFileRetMsg());
        if (src.getErrCode() != null) target.setErrCode(src.getErrCode());
        if (src.getNano() != null) target.setNano(src.getNano());
        if (src.getMd5() != null) target.setMd5(src.getMd5());
        if (src.getSessionMD5() != null) target.setSessionMD5(src.getSessionMD5());
        //
        if (src.getDesKey() != null) target.setDesKey(src.getDesKey());
        if (src.getTarSysName() != null) target.setTarSysName(src.getTarSysName());
        if (src.getTarFileName() != null) target.setTarFileName(src.getTarFileName());
        if (src.getTranCode() != null) target.setTranCode(src.getTranCode());
        if (src.getCompressFlag() != null) target.setCompressFlag(src.getCompressFlag());
        if (src.getFileRenameCtrl() != null) target.setFileRenameCtrl(src.getFileRenameCtrl());
        if (src.getTargetNodeAddr() != null) target.setTargetNodeAddr(src.getTargetNodeAddr());
        if (src.getNodeList() != null) target.setNodeList(src.getNodeList());
        if (src.getLastRemoteFileName() != null) target.setLastRemoteFileName(src.getLastRemoteFileName());
        if (src.getRemoteFileSize() != null) target.setRemoteFileSize(src.getRemoteFileSize());
        if (src.getClientApiVersion() != null) target.setClientApiVersion(src.getClientApiVersion());
        if (src.getServerApiVersion() != null) target.setServerApiVersion(src.getServerApiVersion());
        if (src.getClientNodelistVersion() != null) target.setClientNodelistVersion(src.getClientNodelistVersion());
        if (src.getServerNodelistVersion() != null) target.setServerNodelistVersion(src.getServerNodelistVersion());
        if (src.getMountNodeName() != null) target.setMountNodeName(src.getMountNodeName());
        if (src.getVsysmap() != null) target.setVsysmap(src.getVsysmap());
        //
        target.setAuthFlag(src.isAuthFlag());
        target.setFileSize(src.getFileSize());
        target.setFileIndex(src.getFileIndex());
        target.setPieceNum(src.getPieceNum());
        target.setLastPiece(src.isLastPiece());
        target.setScrtFlag(src.isScrtFlag());
        target.setEbcdicFlag(src.isEbcdicFlag());
        target.setOffset(src.getOffset());
        target.setFileExists(src.isFileExists());
        //
        target.setContLen2(src.getContLen());//setContLen2
        if (src.getFileCont2() != null) target.setFileCont2(src.getFileCont2());
    }

    public static void convertXmlToHead(FileMsgBean bean, String head) throws FtpException {//NOSONAR
        CapabilityDebugHelper.markCurrTime("convertXmlToHeadBegin");
        copyJsonToHead(bean, head);
        CapabilityDebugHelper.markCurrTime("convertXmlToHeadEnd");
    }

    public static String convertHeadToXml(FileMsgBean bean) throws FtpException {//NOSONAR
        CapabilityDebugHelper.markCurrTime("convertHeadToXmlBegin");
        String ret = converToJsonNoFileCont(bean);
        CapabilityDebugHelper.markCurrTime("convertHeadToXmlEnd");
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#convertBeanToString ->> {}", bean.getNano(), bean.getFlowNo(), ret);
        }
        return ret;
    }

    public static void convertXmlToHead0(FileMsgBean bean, String head) throws FtpException {//NOSONAR
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#convertXmlToBean ->> {}", bean.getNano(), bean.getFlowNo(), head);
        }
        CapabilityDebugHelper.markCurrTime("parseXmlBegin");
        Map<String, String> beanMap = FileXmlUtil.parseXml(head);
        CapabilityDebugHelper.markCurrTime("parseXmlEnd");

        if (beanMap.get("ServerName") != null) bean.setServerName(beanMap.get("ServerName"));//NOSONAR
        if (beanMap.get("Uid") != null) bean.setUid(beanMap.get("Uid"));
        if (beanMap.get("Passwd") != null) {//NOSONAR
            bean.setPasswd(beanMap.get("Passwd"));
            if (bean.getPasswd() != null && bean.getPasswd().startsWith("${3DES}")) {
                bean.setPasswd(ScrtUtil.decrypt3DES(bean.getPasswd()));
            }
        }
        if (beanMap.get("FileMsgFlag") != null) bean.setFileMsgFlag(beanMap.get("FileMsgFlag"));//NOSONAR
        if (beanMap.get("AuthFlag") != null) bean.setAuthFlag(BooleanUtils.toBoolean(beanMap.get("AuthFlag")));//NOSONAR
        if (beanMap.get("ScrtFlag") != null) bean.setScrtFlag(BooleanUtils.toBoolean(beanMap.get("ScrtFlag")));//NOSONAR
        if (beanMap.get("Md5") != null) bean.setMd5(beanMap.get("Md5"));
        if (beanMap.get("sessionMD5") != null) bean.setSessionMD5(beanMap.get("sessionMD5"));//NOSONAR
        if (beanMap.get("FileName") != null) bean.setFileName(beanMap.get("FileName"));//NOSONAR
        if (beanMap.get("ClientFileName") != null) bean.setClientFileName(beanMap.get("ClientFileName"));//NOSONAR
        if (beanMap.get("LastPiece") != null) bean.setLastPiece(BooleanUtils.toBoolean(beanMap.get("LastPiece")));//NOSONAR
        if (beanMap.get("FileSize") != null) bean.setFileSize(Long.parseLong(beanMap.get("FileSize")));//NOSONAR
        if (beanMap.get("FileIndex") != null) bean.setFileIndex(Integer.parseInt(beanMap.get("FileIndex")));//NOSONAR
        if (beanMap.get("PieceNum") != null) bean.setPieceNum(Integer.parseInt(beanMap.get("PieceNum")));//NOSONAR
        if (beanMap.get("DesKey") != null) {//NOSONAR
            byte[] scrtKey = ScrtUtil.decodeBase64(beanMap.get("DesKey"));
            bean.setDesKey(Des.decrypt3DES(scrtKey, Des.getDesKey()));
            log.debug("nano:{}#flowNo:{}#密钥同步成功", bean.getNano(), bean.getFlowNo());
        }
        if (beanMap.get("tarSysName") != null) bean.setTarSysName(beanMap.get("tarSysName"));//NOSONAR
        if (beanMap.get("tarFileName") != null) bean.setTarFileName(beanMap.get("tarFileName"));//NOSONAR
        if (beanMap.get("sysname") != null) bean.setSysname(beanMap.get("sysname"));//NOSONAR
        if (beanMap.get("tranCode") != null) bean.setTranCode(beanMap.get("tranCode"));//NOSONAR
        if (beanMap.get("offset") != null) bean.setOffset(Long.parseLong(beanMap.get("offset")));//NOSONAR
        if (beanMap.get("compressFlag") != null) bean.setCompressFlag(beanMap.get("compressFlag"));//NOSONAR
        if (beanMap.get("fileRetMsg") != null) bean.setFileRetMsg(beanMap.get("fileRetMsg"));//NOSONAR
        bean.setErrCode(beanMap.get("errCode"));
        if (beanMap.get("nano") != null) bean.setNano(StringTool.toLong(beanMap.get("nano")));
        if (beanMap.get("flowNo") != null) bean.setFlowNo(beanMap.get("flowNo"));//NOSONAR
        if (beanMap.get("fileRenameCtrl") != null) bean.setFileRenameCtrl(beanMap.get("fileRenameCtrl"));//NOSONAR
        if (beanMap.get("targetNodeAddr") != null) bean.setTargetNodeAddr(beanMap.get("targetNodeAddr"));//NOSONAR
        if (beanMap.get("nodeList") != null) bean.setNodeList(beanMap.get("nodeList"));//NOSONAR
        if (beanMap.get("fileExists") != null) bean.setFileExists(BooleanUtils.toBoolean(beanMap.get("fileExists")));//NOSONAR
        bean.setLastRemoteFileName(beanMap.get("lastRemoteFileName"));
        if (beanMap.get("remoteFileSize") != null) bean.setRemoteFileSize(Long.valueOf(beanMap.get("remoteFileSize")));//NOSONAR
        if (beanMap.get("clientApiVersion") != null) bean.setClientApiVersion(beanMap.get("clientApiVersion"));//NOSONAR
        if (beanMap.get("serverApiVersion") != null) bean.setServerApiVersion(beanMap.get("serverApiVersion"));//NOSONAR
        if (beanMap.get("clientNodelistVersion") != null) bean.setClientNodelistVersion(beanMap.get("clientNodelistVersion"));//NOSONAR
        if (beanMap.get("serverNodelistVersion") != null) bean.setServerNodelistVersion(beanMap.get("serverNodelistVersion"));//NOSONAR
        if (beanMap.get("clientVersion") != null) bean.setClientVersion(beanMap.get("clientVersion"));//NOSONAR
        bean.setMountNodeName(beanMap.get("mountNodeName"));
    }

    public static String convertHeadToXml0(FileMsgBean bean) throws FtpException {
        HashMap<String, String> beanMap = new HashMap<String, String>();

        String fileMsgFlag = bean.getFileMsgFlag();
        beanMap.put("FileMsgFlag", fileMsgFlag);

        if (FileMsgTypeHelper.isNeedShowUidPwd(fileMsgFlag)) {
            beanMap.put("Uid", bean.getUid());
            beanMap.put("ServerName", bean.getServerName());
            if (bean.getPasswd() != null) beanMap.put("Passwd", ScrtUtil.encrypt3DES(bean.getPasswd()));
            if (BooleanTool.toBoolean(bean.isAuthFlag())) beanMap.put("AuthFlag", Boolean.toString(bean.isAuthFlag()));
            if (BooleanTool.toBoolean(bean.isScrtFlag())) beanMap.put("ScrtFlag", Boolean.toString(bean.isScrtFlag()));

            beanMap.put("ClientFileName", bean.getClientFileName());
            if (bean.getFileSize() > -1) beanMap.put("FileSize", Long.toString(bean.getFileSize()));
            if (bean.getDesKey() != null) {
                log.debug("nano:{}#flowNo:{}#进行密钥的同步", bean.getNano(), bean.getFlowNo());
                byte[] scrtKey = Des.encrypt3DES(bean.getDesKey(), Des.getDesKey());
                beanMap.put("DesKey", ScrtUtil.encodeBase64(scrtKey));
            }
        }

        beanMap.put("FileName", bean.getFileName());
        beanMap.put("Md5", bean.getMd5());
        beanMap.put("sessionMD5", bean.getSessionMD5());
        if (bean.getFileIndex() > -1) beanMap.put("FileIndex", Integer.toString(bean.getFileIndex()));
        if (bean.getPieceNum() > -1) beanMap.put("PieceNum", Integer.toString(bean.getPieceNum()));
        if (BooleanTool.toBoolean(bean.isLastPiece())) beanMap.put("LastPiece", Boolean.toString(bean.isLastPiece()));
        beanMap.put("tarSysName", bean.getTarSysName());
        beanMap.put("tarFileName", bean.getTarFileName());
        beanMap.put("sysname", bean.getSysname());
        beanMap.put("tranCode", bean.getTranCode());
        if (bean.getOffset() > 0) beanMap.put("offset", String.valueOf(bean.getOffset()));
        beanMap.put("compressFlag", bean.getCompressFlag());
        beanMap.put("fileRetMsg", bean.getFileRetMsg());
        beanMap.put("errCode", bean.getErrCode());
        beanMap.put("nano", StringTool.toString(bean.getNano()));
        beanMap.put("flowNo", bean.getFlowNo());
        beanMap.put("fileRenameCtrl", bean.getFileRenameCtrl());
        beanMap.put("targetNodeAddr", bean.getTargetNodeAddr());
        beanMap.put("nodeList", bean.getNodeList());
        beanMap.put("fileExists", Boolean.toString(BooleanTool.toBoolean(bean.isFileExists())));
        beanMap.put("lastRemoteFileName", bean.getLastRemoteFileName());
        beanMap.put("remoteFileSize", StringTool.toString(bean.getRemoteFileSize()));
        beanMap.put("clientApiVersion", bean.getClientApiVersion());
        beanMap.put("serverApiVersion", bean.getServerApiVersion());
        beanMap.put("clientNodelistVersion", bean.getClientNodelistVersion());
        beanMap.put("serverNodelistVersion", bean.getServerNodelistVersion());
        beanMap.put("mountNodeName", bean.getMountNodeName());
        beanMap.put("clientVersion", bean.getClientVersion());
        String xml = FileXmlUtil.createXML(beanMap);

        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#convertBeanToXml ->> {}", bean.getNano(), bean.getFlowNo(), xml);
        }
        return xml;
    }

    public static String toStringIgnoreEx(FileMsgBean bean) {
        try {
            return convertHeadToXml(bean);
        } catch (Exception e) {
            return null;
        }
    }
}
