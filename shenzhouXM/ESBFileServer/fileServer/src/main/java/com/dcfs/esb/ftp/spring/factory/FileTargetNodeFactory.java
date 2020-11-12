package com.dcfs.esb.ftp.spring.factory;

import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esb.ftp.spring.outservice.IFileTargetNodeService;

/**
 * Created by mocg on 2016/11/7.
 */
public class FileTargetNodeFactory {
    private static FileTargetNodeFactory ourInstance = new FileTargetNodeFactory();
    private SpringContext context = SpringContext.getInstance();

    private FileTargetNodeFactory() {
    }

    public static FileTargetNodeFactory getInstance() {
        return ourInstance;
    }

    public IFileTargetNodeService getFileTargetNodeService() {
        return context.getFileTargetNodeService();
    }
}
