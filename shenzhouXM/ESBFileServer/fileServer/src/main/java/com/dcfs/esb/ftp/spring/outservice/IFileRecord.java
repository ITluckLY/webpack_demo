package com.dcfs.esb.ftp.spring.outservice;

import com.dcfs.esb.ftp.server.model.*;

import java.io.IOException;

/**
 * Created by mocg on 2016/7/26.
 */
public interface IFileRecord {
    void logFileDownload(FileDownloadRecord record) throws IOException;

    void logFileMsgDownloadResult(FileMsgDownloadResultRecord record) throws IOException;

    void logFileUpload(FileUploadRecord record) throws IOException;

    void logSaveFile(FileSaveRecord record) throws IOException;

    void logDeleteFile(FileDeleteRecord record) throws IOException;

    void logDistributeFile(FileDistributeRecord record) throws IOException;

    void send(SameFileDeleteRecord record) throws IOException;

    void logPushFileMsg2Cli(PushFileMsg2CliRecord record) throws IOException;

    void logNodeListGet(NodeListGetRecord record) throws IOException;

    void logClientUnregister(ClientRegisterRecord record) throws IOException;

    void logClientRegister(ClientRegisterRecord record) throws IOException;

    void logUserRegister(UserRegisterRecord record) throws IOException;

//    void fileUploadClear(FileUploadClearRecord record)throws IOException;

}
