package com.dcfs.esb.ftp.server.config;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import org.dom4j.Document;

/**
 * Created by huangzbb on 2016/9/5.
 */
public interface CfgDocServiceFace {

    ResultDto<Void> setCfgDoc(String docXml, String filename, String system);

    Document getCfgDoc(String filename, String system);
}
