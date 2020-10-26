package com.dc.smarteam.modules.monitor.ftnodemonitor.service.impl;

import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeMonitorMsgEmailMapper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorMsgEmail;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorMsgEmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("FtNodeMonitorMsgEmailServiceImpl")
@Transactional
public class FtNodeMonitorMsgEmailServiceImpl implements FtNodeMonitorMsgEmailService {

  @Resource
  private FtNodeMonitorMsgEmailMapper ftNodeMonitorMsgEmailMapper;


  public List<FtNodeMonitorMsgEmail> getAddMsgAndEmailList(int pageNo, int pageSize){
    return ftNodeMonitorMsgEmailMapper.getAddMsgAndEmailList(pageNo, pageSize);
  }

  public int getAddMsgAndEmailTotal(){
    return ftNodeMonitorMsgEmailMapper.getAddMsgAndEmailTotal();
  }

  public FtNodeMonitorMsgEmail getFtNodeMonitorMsgEmailById(String id){
    return ftNodeMonitorMsgEmailMapper.getFtNodeMonitorMsgEmail(id);
  }

  public int save(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail){
    return ftNodeMonitorMsgEmailMapper.insert(ftNodeMonitorMsgEmail);
  }

  public int delete(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail){
    return ftNodeMonitorMsgEmailMapper.delete(ftNodeMonitorMsgEmail);
  }
}
