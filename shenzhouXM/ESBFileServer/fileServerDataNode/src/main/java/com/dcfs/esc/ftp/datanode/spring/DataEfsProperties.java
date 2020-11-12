package com.dcfs.esc.ftp.datanode.spring;

import com.dcfs.esb.ftp.spring.EfsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by mocg on 2017/8/25.
 */
@Component
public class DataEfsProperties extends EfsProperties {

    @Value("${test.defNodeListStr:}")
    private String defNodeListStrForTest;

    @Value("${netty.handlerLogLevelName:}")
    private String handlerLogLevelName;

    @PostConstruct
    public void init() {
        if (defNodeListStrForTest != null && defNodeListStrForTest.length() == 0) defNodeListStrForTest = null;
    }

    //getter

    public String getDefNodeListStrForTest() {
        return defNodeListStrForTest;
    }

    public String getHandlerLogLevelName() {
        return handlerLogLevelName;
    }
}
