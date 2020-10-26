package com.dc.smarteam.modules.demo.service.impl;

import com.dc.smarteam.modules.demo.dao.DemoMapper;
import com.dc.smarteam.modules.demo.entity.FtSysInfos;
import com.dc.smarteam.modules.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("DemoServiceImpl")
public class DemoServiceImpl implements DemoService{

  @Autowired
  private DemoMapper demoMapper;

  public FtSysInfos getFtSysInfoByName(String name) {
    return demoMapper.getFtSysInfoByName(name);
  }

}
