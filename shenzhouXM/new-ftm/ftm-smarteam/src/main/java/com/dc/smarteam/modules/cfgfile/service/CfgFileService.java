package com.dc.smarteam.modules.cfgfile.service;

import com.dc.smarteam.cfgmodel.BaseModel;
import com.dc.smarteam.modules.cfgfile.entity.CfgFile;

import java.io.IOException;


public interface CfgFileService {

    void save(CfgFile cfgFile);

    CfgFile findOne(CfgFile cfgFile);

    void updateByNameAndNodeType(CfgFile cfgFile);

    String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException;

    <T extends BaseModel> T loadModel4Name(String cfgFileName, Class<? extends T> tclass);

    <T extends BaseModel> void saveModel4Name(String cfgFileName, T model);

}
