package com.dc.smarteam.modules.monitor.ftnodemonitor.service.impl;

import com.dc.smarteam.modules.monitor.ftnodemonitor.dao.FtNodeAlarmLineMapper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeAlarmLine;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeAlarmLineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Random;

@Service("FtNodeAlarmLineServiceImpl")
@Transactional
public class FtNodeAlarmLineServiceImpl implements FtNodeAlarmLineService {

      @Resource
      private FtNodeAlarmLineMapper ftNodeAlarmLineMapper;

      public int saveAlarmLineList(FtNodeAlarmLine ftNodeAlarmLine){
        int change = 0;
        if(ftNodeAlarmLine.getId() == null){
          ftNodeAlarmLine.setId(BigInteger.valueOf(new Random().nextLong()));
          change = ftNodeAlarmLineMapper.insert(ftNodeAlarmLine);
        }else{
          change = ftNodeAlarmLineMapper.update(ftNodeAlarmLine);
        }
        return change;
      }
}
