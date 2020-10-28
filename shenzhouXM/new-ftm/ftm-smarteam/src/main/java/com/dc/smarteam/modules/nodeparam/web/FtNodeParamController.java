/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.nodeparam.web;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 节点参数Controller
 *
 * @author liwang
 * @version 2016-01-11
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/nodeparam/ftNodeParam")
public class FtNodeParamController{

//    @RequiresPermissions("nodeparam:ftNodeParam:view")
    @RequestMapping(value = {"list"})
    public ResultDto<Map<String,Object>> list(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        String message = "";
        FtNodeParam ftNodeParam = new FtNodeParam();
        request.getSession().setAttribute("ftServiceNodeParam", ftServiceNode);
        List<FtNodeParam> list = new ArrayList<FtNodeParam>();
        if (null != ftServiceNode.getState() && ftServiceNode.getState().trim().equalsIgnoreCase("1")) {
            String allStr = MessageFactory.getInstance().nodeParam(ftNodeParam, "select");
            TCPAdapter tcpAdapter = new TCPAdapter();
            ResultDto<String> resultDto = tcpAdapter.invoke(allStr, ftServiceNode, String.class);//发送报文
            if (ResultDtoTool.isSuccess(resultDto)) {
                String data = resultDto.getData();
                List<FtNodeParam> subList = JsonToEntityFactory.getInstance().getNodeParamList(data, ftServiceNode);
                list.addAll(subList);
            } else {
                message = resultDto.getMessage();
                return ResultDtoTool.buildError(message);
            }
        } else {
            message = "节点未连接，请检查连接状态后，重新再试！";
            return ResultDtoTool.buildError(message);
        }
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("ftNodeParamList", list);
        return ResultDtoTool.buildSucceed(message,resultMap);
    }

}