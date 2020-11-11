/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.helper;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.util.XMLDealTool;
import com.google.gson.JsonObject;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FtServiceNodeHelper {//NOSONAR
    private static final Logger log = LoggerFactory.getLogger(FtServiceNodeHelper.class);

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


    public static void getConfComp(FtServiceNode ftServiceNode, FtServiceNode ftServiceNodeNameNode, String getAllStr, String getOtherAllStr, HttpServletRequest request, Model model) {//NOSONAR
        ftServiceNode.setName(ftServiceNodeNameNode.getName());
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(getAllStr, ftServiceNode, String.class);
        String returnMsg = resultDto.getData();
        Document doc = null;
        String asXML = null;
        String asXml2 = null;
        if (returnMsg != null) {
            doc = XMLDealTool.readXml(returnMsg);
            asXML = doc.asXML();
            asXml2 = XMLDealTool.withoutTimestamp(doc).asXML();
        }
        model.addAttribute("returnMsg", asXML);
        String getNodesAllStr = MessageFactory.getInstance().nodes(new FtServiceNode(), "select");//生成查询报文
        TCPAdapter nodesTcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto2 = nodesTcpAdapter.invoke(getNodesAllStr, ftServiceNode, String.class);//发送报文
        List<FtServiceNode> ftServiceNodeList = new ArrayList<>();
        Boolean isSame = false;
        if (null != resultDto2 && ResultDtoTool.isSuccess(resultDto2)) {
            String data1 = resultDto2.getData();
            ftServiceNodeList = JsonToEntityFactory.getInstance().getNodes(data1);
            for (FtServiceNode fsn : ftServiceNodeList) {
                if (fsn.getSystemName() != null) {
                    JSONObject jsonObject = JSONObject.fromObject(fsn.getSystemName());
                    fsn.setSystemName(jsonObject.getString("text"));
                }
            }
        }
        List<FtServiceNode> ftServiceNodeListEnd = new ArrayList<>();
        for (FtServiceNode fsn : ftServiceNodeList) {
            if (null != fsn.getSystemName() && (fsn.getSystemName().equalsIgnoreCase(ftServiceNodeNameNode.getSystemName())) && (fsn.getType().equalsIgnoreCase("datanode"))) {
                ftServiceNodeListEnd.add(fsn);
            }
        }
        List<String> otherFsnConf = new ArrayList<>();
        for (FtServiceNode fsn1 : ftServiceNodeListEnd) {
            if (fsn1.getState().trim().equalsIgnoreCase("1")) {
                TCPAdapter otherTcpAdapter = new TCPAdapter();//NOSONAR
                ResultDto<String> resultDto3 = otherTcpAdapter.invoke(getOtherAllStr, fsn1, String.class);//发送报文
                String data3 = resultDto3.getData();
                otherFsnConf.add(data3);
                //---------------------------------------------------------------
                Document docRm = null;
                String asXmlRm2 = null;
                String asXml2Temp = null;
                String asXmlRm2Temp = null;
                if (asXml2 !=null && resultDto3.getData() != null) {
                    docRm = XMLDealTool.readXml(data3);
                    asXmlRm2 = XMLDealTool.withoutTimestamp(docRm).asXML();
                    asXml2Temp = asXml2.replaceAll(" |\n|\t", "");
                    asXmlRm2Temp = asXmlRm2.replaceAll(" |\n|\t", "");
                }
                if (asXml2 != null && asXmlRm2 != null &&
                        !(asXml2Temp.equalsIgnoreCase(asXmlRm2Temp))) {
                    isSame = true;
                }
            } else if (fsn1.getState().trim().equalsIgnoreCase("0")) {
                otherFsnConf.add("");
            }

        }
        if (isSame) {
            model.addAttribute("resultSyn", "文件不相同，请先核对之后，再进行同步操作。");
        } else {
            model.addAttribute("resultSyn", "文件相同，不需要进行同步操作。");
        }
        model.addAttribute("otherFsnConf", otherFsnConf);
        model.addAttribute("ftServiceNodeList", ftServiceNodeListEnd);
    }

    public static String getCachtFileCfg(String fileName, HttpServletRequest request) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String getAllStr = null;
        String resultType = "true";
        if (!((null == fileName) || fileName.isEmpty())) {
            getAllStr = MessageFactory.getInstance().cfgSync(fileName, "nodeSync", ftServiceNode.getSystemName());//生成查询报文
            TCPAdapter tcpAdapter = new TCPAdapter();
            //发送报文
            ResultDto<String> resultDto = tcpAdapter.invoke(getAllStr, ftServiceNode, String.class);
            String returnMsg = resultDto.getData();
            if (!(null == returnMsg || returnMsg.isEmpty())) {
                if (returnMsg.contains(":true") && ResultDtoTool.isSuccess(resultDto)) {
                    resultType = "true";
                } else if (returnMsg.contains(":false") && !ResultDtoTool.isSuccess(resultDto)) {
                    resultType = "false";
                } else {
                    resultType = resultDto.getMessage();
                }
            }
        }
        return resultType;
    }

    public static void updateStateByZK(FtServiceNode ftServiceNode) {
        if (ftServiceNode == null) return;
        String state = "0";
        String ipPort = ftServiceNode.getIpAddress() + ":" + ftServiceNode.getCmdPort();
        Map<String, JsonObject> map = null;
        String type = ftServiceNode.getType();
        if (NodeType.DATANODE.name().equalsIgnoreCase(type)) map = ZkService.getInstance().getDataNodeMap();
        else if (NodeType.LOGNODE.name().equalsIgnoreCase(type)) map = ZkService.getInstance().getLogNodeMap();
        else if (NodeType.NAMENODE.name().equalsIgnoreCase(type)) map = ZkService.getInstance().getNameNodeMap();
        if (map != null && map.containsKey(ipPort)) state = "1";
        ftServiceNode.setState(state);
    }

}
