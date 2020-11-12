package com.dcfs.esb.ftp.distribute;

import com.dcfs.esb.ftp.common.msg.FileMsgBean;

/**
 * Created by mocg on 2016/7/14.
 */
public class DistributeFileMsgBean extends FileMsgBean {
    private Integer result;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
