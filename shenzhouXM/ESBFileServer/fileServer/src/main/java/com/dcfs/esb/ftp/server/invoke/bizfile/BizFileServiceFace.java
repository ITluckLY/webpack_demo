package com.dcfs.esb.ftp.server.invoke.bizfile;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import net.sf.json.JSONObject;

/**
 * Created by huangzbb on 2016/10/19.
 */
public interface BizFileServiceFace {
    ResultDto<String> selByRequestFilePath(JSONObject jsonObject);
}
