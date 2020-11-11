package com.dc.smarteam.modules.protocol.web;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.GsonUtil;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.protocol.entity.FTPUser;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangzbb on 2017/6/9.
 */
@Controller
@RequestMapping(value = "${adminPath}/protocol/sysProtocol")
public class FtpProtocolController extends BaseController {

    @RequestMapping(value = "ftpUserList")
    public String form(SysProtocol sysProtocol, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        List<FTPUser> ftpUserList = new ArrayList<>();
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setIpAddress(sysProtocol.getIp());
        ftServiceNode.setCmdPort("2020");
        String msg = MessageFactory.getInstance().ftpServer(new FTPUser(), "selectUser");
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(msg, ftServiceNode, String.class);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            JsonArray jsonArray = GsonUtil.toJsonArray(data);
            Iterator<JsonElement> iterator = jsonArray.iterator();
            Gson gson = new Gson();
            while (iterator.hasNext()) {
                JsonElement next = iterator.next();
                FTPUser ftpUser = gson.fromJson(next, FTPUser.class);
                ftpUserList.add(ftpUser);
            }
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        PageHelper.getInstance().getPage(FTPUser.class, request, response, model, ftpUserList);
        return "modules/protocol/ftpUserList";
    }
}
