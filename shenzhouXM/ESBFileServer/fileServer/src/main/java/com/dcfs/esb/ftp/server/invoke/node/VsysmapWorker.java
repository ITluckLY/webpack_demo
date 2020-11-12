package com.dcfs.esb.ftp.server.invoke.node;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mocg on 2016/7/15.
 */
public class VsysmapWorker {
    private static final Object lock = new Object();
    private static VsysmapWorker instance = null;
    private Element root;
    private Map<String, String> vsysmap = new HashMap<>();

    private VsysmapWorker() {
        load();
        parse();
    }

    public static VsysmapWorker getInstance() {
        if (instance != null) return instance;
        synchronized (lock) {
            if (instance == null) {
                instance = new VsysmapWorker();
            }
        }
        return instance;
    }

    public static void reload() {
        try {
            CachedCfgDoc.getInstance().reloadVsysmap();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        instance = new VsysmapWorker();
    }

    private void load() {
        Document doc;
        try {
            doc = CachedCfgDoc.getInstance().loadVsysmap();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    private void parse() {
        List nodes = root.selectNodes("/root/map");
        for (Object obj : nodes) {
            Element ele = (Element) obj;
            vsysmap.put(ele.attributeValue("key"), ele.attributeValue("val"));
        }
    }

    public Map<String, String> getVsysmap() {
        return vsysmap;
    }
}
