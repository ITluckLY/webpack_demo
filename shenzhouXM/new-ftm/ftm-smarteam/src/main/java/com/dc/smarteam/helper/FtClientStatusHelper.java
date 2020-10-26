package com.dc.smarteam.helper;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * @Author: xuchuang
 * @Date: 2018/8/9 10:17
 */
public class FtClientStatusHelper {
    private static final Logger log = LoggerFactory.getLogger(FtClientStatusHelper.class);

    public static void getOtherConf(HttpServletRequest request, Model model, String getAllStr) {
        FtServiceNode ftServiceNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeOtherConf");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(getAllStr, ftServiceNode, String.class);//发送报文
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
