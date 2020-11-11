package com.dc.smarteam.modules.monitor.putfiletomonitor.msg;

import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpException;
import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.Des;
import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.ScrtUtil;
import com.dc.smarteam.util.FileXmlUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by mocg on 2016/7/20.
 */
public class FileMsgBeanHelper {
    private static final Logger log = LoggerFactory.getLogger(FileMsgBeanHelper.class);

    public static void convertXmlToHead(FileMsgBean bean, String head) throws FtpException {
        log.debug("convertXmlToBean ->> {}", head);

        HashMap<String, String> beanMap = FileXmlUtil.parseXml(head);

        if (beanMap.get("ServerName") != null) bean.setServerName(beanMap.get("ServerName"));
        if (beanMap.get("Uid") != null) bean.setUid(beanMap.get("Uid"));
        if (beanMap.get("Passwd") != null) {
            bean.setPasswd(beanMap.get("Passwd"));
            if (bean.getPasswd() != null && bean.getPasswd().startsWith("${3DES}")) {
                bean.setPasswd(ScrtUtil.decrypt3DES(bean.getPasswd()));
            }
        }
        if (beanMap.get("FileMsgFlag") != null) bean.setFileMsgFlag(beanMap.get("FileMsgFlag"));
        if (beanMap.get("AuthFlag") != null) bean.setAuthFlag(BooleanUtils.toBoolean(beanMap.get("AuthFlag")));
        if (beanMap.get("ScrtFlag") != null) bean.setScrtFlag(BooleanUtils.toBoolean(beanMap.get("ScrtFlag")));
        if (beanMap.get("Md5") != null) bean.setMd5(beanMap.get("Md5"));
        if (beanMap.get("sessionMD5") != null) bean.setSessionMD5(beanMap.get("sessionMD5"));
        if (beanMap.get("FileName") != null) bean.setFileName(beanMap.get("FileName"));
        if (beanMap.get("ClientFileName") != null) bean.setClientFileName(beanMap.get("ClientFileName"));
        if (beanMap.get("LastPiece") != null) bean.setLastPiece(BooleanUtils.toBoolean(beanMap.get("LastPiece")));
        if (beanMap.get("FileSize") != null) bean.setFileSize(Long.parseLong(beanMap.get("FileSize")));
        if (beanMap.get("FileIndex") != null) bean.setFileIndex(Integer.parseInt(beanMap.get("FileIndex")));
        if (beanMap.get("PieceNum") != null) bean.setPieceNum(Integer.parseInt(beanMap.get("PieceNum")));
        if (beanMap.get("DesKey") != null) {
            byte[] scrtKey = ScrtUtil.decodeBase64(beanMap.get("DesKey"));
            bean.setDesKey(Des.decrypt3DES(scrtKey, Des.getDesKey()));
            log.info("密钥同步成功");
        }
        if (beanMap.get("tarSysName") != null) bean.setTarSysName(beanMap.get("tarSysName"));
        if (beanMap.get("tarFileName") != null) bean.setTarFileName(beanMap.get("tarFileName"));
        if (beanMap.get("tran_code") != null) bean.setTranCode(beanMap.get("tran_code"));
        if (beanMap.get("offset") != null) bean.setOffset(Long.parseLong(beanMap.get("offset")));
        if (beanMap.get("compressFlag") != null) bean.setCompressFlag(beanMap.get("compressFlag"));
        if (beanMap.get("fileRetMsg") != null) bean.setFileRetMsg(beanMap.get("fileRetMsg"));
        if (beanMap.get("fileRenameCtrl") != null) bean.setFileRenameCtrl(beanMap.get("fileRenameCtrl"));
        if (beanMap.get("fileRoute") != null) bean.setFileRoute(beanMap.get("fileRoute"));
        if (beanMap.get("nodeList") != null) bean.setNodeList(beanMap.get("nodeList"));
        if (beanMap.get("fileExists") != null) bean.setFileExists(BooleanUtils.toBoolean(beanMap.get("fileExists")));
        bean.setLastRemoteFileName(beanMap.get("lastRemoteFileName"));
        if (beanMap.get("remoteFileSize") != null) bean.setRemoteFileSize(Long.valueOf(beanMap.get("remoteFileSize")));
        if (beanMap.get("clientApiVersion") != null) bean.setClientApiVersion(beanMap.get("clientApiVersion"));
        if (beanMap.get("serverApiVersion") != null) bean.setServerApiVersion(beanMap.get("serverApiVersion"));
        if (beanMap.get("clientNodelistVersion") != null)
            bean.setClientNodelistVersion(beanMap.get("clientNodelistVersion"));
        if (beanMap.get("serverNodelistVersion") != null)
            bean.setServerNodelistVersion(beanMap.get("serverNodelistVersion"));
        if (beanMap.get("bakFileName") != null) bean.setBakFileName(beanMap.get("bakFileName"));
        if (beanMap.get("realFileName") != null) bean.setRealFileName(beanMap.get("realFileName"));
    }

    public static String convertHeadToXml(FileMsgBean bean) throws FtpException {
        HashMap<String, String> beanMap = new HashMap<String, String>();

        beanMap.put("FileMsgFlag", bean.getFileMsgFlag());

        if (FileMsgType.PUT_AUTH.equals(bean.getFileMsgFlag())
                || FileMsgType.GET_AUTH.equals(bean.getFileMsgFlag())) {
            beanMap.put("Uid", bean.getUid());
            beanMap.put("ServerName", bean.getServerName());
            if (bean.getPasswd() != null) beanMap.put("Passwd", ScrtUtil.encrypt3DES(bean.getPasswd()));
            if (bean.isAuthFlag()) beanMap.put("AuthFlag", Boolean.toString(bean.isAuthFlag()));
            if (bean.isScrtFlag()) beanMap.put("ScrtFlag", Boolean.toString(bean.isScrtFlag()));

            beanMap.put("ClientFileName", bean.getClientFileName());
            if (bean.getFileSize() > -1) beanMap.put("FileSize", Long.toString(bean.getFileSize()));
            if (bean.getDesKey() != null) {
                log.info("进行密钥的同步");
                byte[] scrtKey = Des.encrypt3DES(bean.getDesKey(), Des.getDesKey());
                beanMap.put("DesKey", ScrtUtil.encodeBase64(scrtKey));
            }
        }

        beanMap.put("FileName", bean.getFileName());
        beanMap.put("Md5", bean.getMd5());
        beanMap.put("sessionMD5", bean.getSessionMD5());
        if (bean.getFileIndex() > -1) beanMap.put("FileIndex", Integer.toString(bean.getFileIndex()));
        if (bean.getPieceNum() > -1) beanMap.put("PieceNum", Integer.toString(bean.getPieceNum()));
        if (bean.isLastPiece()) beanMap.put("LastPiece", Boolean.toString(bean.isLastPiece()));
        beanMap.put("tarSysName", bean.getTarSysName());
        beanMap.put("tarFileName", bean.getTarFileName());
        beanMap.put("tran_code", bean.getTranCode());
        if (bean.getOffset() > 0) beanMap.put("offset", String.valueOf(bean.getOffset()));
        beanMap.put("compressFlag", bean.getCompressFlag());
        beanMap.put("fileRetMsg", bean.getFileRetMsg());
        beanMap.put("fileRenameCtrl", bean.getFileRenameCtrl());
        beanMap.put("fileRoute", bean.getFileRoute());
        beanMap.put("nodeList", bean.getNodeList());
        beanMap.put("fileExists", Boolean.toString(bean.isFileExists()));
        beanMap.put("lastRemoteFileName", bean.getLastRemoteFileName());
        if (bean.getRemoteFileSize() != null) beanMap.put("remoteFileSize", Long.toString(bean.getRemoteFileSize()));
        beanMap.put("clientApiVersion", bean.getClientApiVersion());
        beanMap.put("serverApiVersion", bean.getServerApiVersion());
        beanMap.put("clientNodelistVersion", bean.getClientNodelistVersion());
        beanMap.put("serverNodelistVersion", bean.getServerNodelistVersion());
        beanMap.put("bakFileName", bean.getBakFileName());
        beanMap.put("realFileName", bean.getRealFileName());
        String xml = FileXmlUtil.createXML(beanMap);

        log.debug("convertBeanToXml ->> {}", xml);
        return xml;
    }
}
