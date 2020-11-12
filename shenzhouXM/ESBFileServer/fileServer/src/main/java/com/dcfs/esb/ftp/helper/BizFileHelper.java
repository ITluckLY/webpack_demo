package com.dcfs.esb.ftp.helper;

import com.dcfs.esb.ftp.server.model.FileSaveRecord;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by mocg on 2016/7/15.
 */
public class BizFileHelper {

    public static void setFileNameExt(FileSaveRecord record) {
        record.setFileName(FilenameUtils.getName(record.getFilePath()));
        record.setClientFileName(FilenameUtils.getName(record.getClientFilePath()));
        record.setFileExt(FilenameUtils.getExtension(record.getFileName()));
    }

    private BizFileHelper() {
    }
}
