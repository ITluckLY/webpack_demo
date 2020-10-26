package com.dc.smarteam.modules.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FtSysInfos {

    private String id;
    private String name;
    private String des;
    private String admin;
    private String adminId;
    private String sysNodeModel;
    private String switchModel;
    private String storeModel;
    private String createBy;
    private String createDate;
    private String updateBy;
    private String updateDate;
    private String remarks;
    private String delFlag;

}
