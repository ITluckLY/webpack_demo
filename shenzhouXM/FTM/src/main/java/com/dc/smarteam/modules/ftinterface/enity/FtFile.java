package com.dc.smarteam.modules.ftinterface.enity;

import com.dc.smarteam.common.persistence.LongDataEntity;

/**
 * Created by Administrator on 2019/12/13.
 */
public class FtFile extends LongDataEntity<FtFile> {

    private String tranCode;

    private String fileName;

    public FtFile (){

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

}
