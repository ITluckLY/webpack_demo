package com.dcfs.esb.ftp.server.invoke.nodesync;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import net.sf.json.JSONObject;

/**
 * Created by huangzbb on 2016/10/25.
 */
public interface NodeSyncServiceFace {
    ResultDto<String> nodeSync(JSONObject jsonObject);

    ResultDto<String> makeSyncCfg(JSONObject jsonObject);

    ResultDto<String> generateSyncCfgXml(JSONObject jsonObject);
}
