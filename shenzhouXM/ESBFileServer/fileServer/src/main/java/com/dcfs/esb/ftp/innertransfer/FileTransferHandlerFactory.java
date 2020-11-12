package com.dcfs.esb.ftp.innertransfer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mocg on 2016/7/25.
 */
public class FileTransferHandlerFactory {
    private static FileTransferHandlerFactory ourInstance = new FileTransferHandlerFactory();
    private Map<String, Class<? extends FileTransferHandler>> classMap = new HashMap<>();

    private FileTransferHandlerFactory() {
    }

    public static FileTransferHandlerFactory getInstance() {
        return ourInstance;
    }

    public Class<? extends FileTransferHandler> get(String key) {
        return classMap.get(key);
    }

    public Class<? extends FileTransferHandler> put(String key, Class<? extends FileTransferHandler> handlerClass) {
        return classMap.put(key, handlerClass);
    }
}
