package com.dcfs.esb.ftp.server.invoke.filemsg2client;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import net.sf.json.JSONObject;

/**
 * Created by huangzbb on 2017/8/14.
 */
public interface FileMsg2ClientServiceFace {
    ResultDto<String> push(JSONObject jsonObject);
}
