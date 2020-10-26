package com.dc.smarteam.modules.monitor.ftnodemonitor.service;

import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorMsgEmail;

import java.util.List;

public interface FtNodeMonitorMsgEmailService {

  public List<FtNodeMonitorMsgEmail> getAddMsgAndEmailList(int pageNo, int pageSize);

  public int getAddMsgAndEmailTotal();

  public FtNodeMonitorMsgEmail getFtNodeMonitorMsgEmailById(String id);

  public int save(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail);

  public int delete(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail);
}
