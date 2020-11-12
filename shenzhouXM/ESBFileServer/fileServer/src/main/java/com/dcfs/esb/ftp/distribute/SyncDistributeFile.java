package com.dcfs.esb.ftp.distribute;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.server.file.EsbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2016/7/14.
 */
public class SyncDistributeFile extends EsbFile {
    private static final Logger log = LoggerFactory.getLogger(SyncDistributeFile.class);

    public SyncDistributeFile(String fileName, int type) throws FtpException {
        super(fileName, type);
    }

    @Override
    public void write(FileMsgBean bean) throws FtpException {
        log.debug("写入文件[{}]的第[{}]个分片", bean.getFileName(), bean.getFileIndex());
        try {
            byte[] fileCont = bean.getFileCont();
            if (bean.getCompressFlag() != null) {
                byte[] src = new byte[bean.getContLen()];
                System.arraycopy(fileCont, 0, src, 0, bean.getContLen());
                byte[] dest = decompress(src, bean.getCompressFlag());
                writer.write(dest, 0, dest.length);
                bean.setContLen(dest.length);
                bean.setFileCont(dest);
            } else {
                writer.write(fileCont, 0, bean.getContLen());
            }
            md5Alm.update(bean.getFileCont(), 0, bean.getContLen());
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.FILE_WRITE_ERROR, e);
        }
    }
}
