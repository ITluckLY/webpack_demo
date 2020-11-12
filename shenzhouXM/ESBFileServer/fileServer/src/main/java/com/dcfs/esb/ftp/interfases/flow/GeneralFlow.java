package com.dcfs.esb.ftp.interfases.flow;

import com.dcfs.esb.ftp.interfases.service.GeneralService;

import java.util.List;

public interface GeneralFlow {
    public String getName();

    public List<GeneralService> getFlow();

}
