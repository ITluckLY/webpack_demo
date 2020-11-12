package com.dcfs.esc.ftp.datanode.factory;

import com.dcfs.esb.ftp.server.invoke.filemsg2client.FileMsg2ClientServiceFace;
import com.dcfs.esb.ftp.server.invoke.filemsg2client.FileMsg2ClientServiceFactoryFace;
import com.dcfs.esc.ftp.datanode.service.FileMsg2ClientService;

/**
 * Created by huangzbb on 2017/8/14.
 */
public class FileMsg2ClientServiceFactory implements FileMsg2ClientServiceFactoryFace {

    public FileMsg2ClientServiceFace getFileMsg2ClientService() {
        return FileMsg2ClientService.getInstance();
    }
}
