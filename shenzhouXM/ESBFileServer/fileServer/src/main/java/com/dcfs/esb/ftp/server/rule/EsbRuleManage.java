package com.dcfs.esb.ftp.server.rule;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EsbRuleManage {
    private static final Logger log = LoggerFactory.getLogger(EsbRuleManage.class);
    private static final Object lock = new Object();
    private static EsbRuleManage instance = null;
    private List<FileRuleBase> ruleList = new ArrayList<>();

    private EsbRuleManage() {
    }

    public static EsbRuleManage getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    reload();
                }
            }
        }
        return instance;
    }

    public static void reload() {
        try {
            CachedCfgDoc.getInstance().reloadRule();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        EsbRuleManage inst = new EsbRuleManage();
        inst.load();
        instance = inst;
    }

    public boolean doFileRule(String fileName) {
        for (FileRuleBase base : ruleList) {
            boolean flag = base.checkRule(fileName);
            if (flag) {
                base.doRule(fileName);
            }
        }
        return true;
    }

    public void load() {
        log.debug("加载规则信息...");
        try {
            Document doc = CachedCfgDoc.getInstance().loadRule();
            Element root = doc.getRootElement();
            Element fileInfo;
            for (Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
                fileInfo = (Element) i.next();
                String name = fileInfo.attributeValue("name");
                FileRuleBase base = new FileRuleBase(name);
                List<?> list = fileInfo.elements("rule");
                for (Object aList : list) {
                    Element grant = (Element) aList;
                    String className = grant.attributeValue("class");
                    Object object = Class.forName(className)
                            .newInstance();
                    base.addRule((IFileRule) object);
                }
                ruleList.add(base);
            }
        } catch (Exception e) {
            log.error("加载规则信息失败", e);
        }
        log.debug("加载规则信息成功:{}", ruleList.size());
    }
}
