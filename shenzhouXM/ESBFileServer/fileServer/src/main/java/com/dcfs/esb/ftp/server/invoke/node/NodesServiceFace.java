package com.dcfs.esb.ftp.server.invoke.node;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import net.sf.json.JSONObject;

/**
 * Created by huangzbb on 2016/10/31.
 */
public interface NodesServiceFace {
    ResultDto<String> add(JSONObject jsonObject);

    ResultDto<String> update(JSONObject jsonObject);

    ResultDto<String> del(JSONObject jsonObject);
}
