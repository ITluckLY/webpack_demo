package com.dcfs.esb.ftp.server.invoke.nodesync;

import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mocg on 2016/10/10.
 */
public abstract class AbstractCfgFileReceiver {
    private static final Logger log = LoggerFactory.getLogger(AbstractCfgFileReceiver.class);

    public final ResultDto<String> receive(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        if (data == null) return ResultDtoTool.buildError("参数错误");
        String sysName = data.getString("sysName");
        String cfgFileName = data.getString("cfgName");
        String cfgContent = data.getString("cfgContent");
        boolean succ = false;
        try {
            succ = receive(sysName, cfgFileName, cfgContent);
        } catch (IOException e) {
            log.error("", e);
        }
        return ResultDtoTool.buildSucceed(String.valueOf(succ));
    }

    public abstract boolean receive(String sysName, String cfgFileName, String cfgContent) throws IOException;

}
