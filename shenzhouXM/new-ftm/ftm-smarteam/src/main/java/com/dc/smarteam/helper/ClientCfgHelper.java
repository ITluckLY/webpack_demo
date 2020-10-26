package com.dc.smarteam.helper;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;

public class ClientCfgHelper {
    private static final Logger log = LoggerFactory.getLogger(ClientCfgHelper.class);

    public static void getClientCfg(HttpServletRequest request, Model model, String getAllStr) {
        ClientSyn clientSyn = (ClientSyn) request.getSession().getAttribute("clientCfg");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(getAllStr, clientSyn, String.class);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            try {
                if (resultDto.getData() != null) {
                    model.addAttribute("returnMsg", URLDecoder.decode(resultDto.getData(), "utf-8"));
                } else {
                    model.addAttribute("returnMsg", "");
                }
            } catch (IOException e) {
                log.error("", e);
            }
        } else {
            model.addAttribute("returnMsg", "");
        }
    }
}
