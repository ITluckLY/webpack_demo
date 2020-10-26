package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.ICfgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuchuang on 2018/5/28.
 */
public abstract class AbstractService implements ICfgService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getNodeType() {
        return "NAMENODE";
    }

    @Override
    public String getSystem() {
        return "UNDEFINED";
    }

}
