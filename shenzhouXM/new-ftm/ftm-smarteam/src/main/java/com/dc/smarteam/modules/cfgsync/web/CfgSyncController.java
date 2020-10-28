package com.dc.smarteam.modules.cfgsync.web;

import com.dc.smarteam.cfgmodel.NodesModel;
import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.common.zk.CfgZkService;
import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.RequestMsgHelper;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.NodesService;
import com.dc.smarteam.util.XMLDealTool;
import com.google.gson.JsonObject;
import org.dom4j.Document;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  lyy
 *   2020.10.22
 */
@RestController
@RequestMapping(value = "${adminPath}/cfgsync")
public class CfgSyncController extends BaseController {
    @Resource
    private CfgFileService cfgFileService;
    @Resource
    private CfgZkService cfgZkService;
    @Resource
    private NodesService nodesService;


    @RequestMapping(value = {"index", ""})
    public ResultDto synConf(HttpServletRequest request) {
        Map<String,Object> rs = new HashMap();
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return  ResultDtoTool.buildError("请先设置节点。");
        }
        rs.put("ftServiceNode", ftServiceNode);

        return  ResultDtoTool.buildSucceed(rs);
    }

    /**
     *  节点同步 页面
     * @param fileName
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "confComp", produces = "application/json;charset=UTF-8")
    public Object confComp(@RequestParam String fileName, HttpServletRequest request) throws IOException {
        Map<String,Object> mapm =new HashMap<>();
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return ResultDtoTool.buildError("请先设置节点组！！！");
        }
        String sysname = ftServiceNode.getSystemName();
        String currCfgContent = getCurrCfgContent(sysname, fileName, false);

        List<FtServiceNode> activeNodeList = new ArrayList<>();
        List<FtServiceNode> inactiveNodeList = new ArrayList<>();
        ResultDto<List<NodesModel.Node>> resultDto = nodesService.listAll(null, request);
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<NodesModel.Node> nodeList = resultDto.getData();
            for (NodesModel.Node node : nodeList) {
                if (NodeType.DATANODE.name().equalsIgnoreCase(node.getType()) && StringUtils.equals(sysname, node.getSystem().getName())) {
                    //boolean isActive = "1".equals(node.getState());//NOSONAR
                    //根据zk获取节点状态
                    String ipPort = node.getIp() + ":" + node.getCmdPort();
                    Map<String, JsonObject> map = ZkService.getInstance().getDataNodeMap();
                    boolean isActive = map != null && map.containsKey(ipPort);
                    if (isActive) {
                        FtServiceNode ftNode = new FtServiceNode();
                        ftNode.setName(node.getName());
                        ftNode.setIpAddress(node.getIp());
                        ftNode.setCmdPort(node.getCmdPort());
                        ftNode.setName(node.getName());
                        ftNode.setState(node.getState());
                        activeNodeList.add(ftNode);
                    } else {
                        FtServiceNode ftNode = new FtServiceNode();
                        ftNode.setName(node.getName());
                        ftNode.setIpAddress(node.getIp());
                        ftNode.setCmdPort(node.getCmdPort());
                        ftNode.setName(node.getName());
                        ftNode.setState(node.getState());
                        inactiveNodeList.add(ftNode);
                    }
                }
            }
        }

        Map<String, String> cfgContentMap = new HashMap<>();
        for (FtServiceNode node : activeNodeList) {
            String msg = RequestMsgHelper.generate(RequestMsgHelper.trun2Target(fileName), "print", null);
            TCPAdapter otherTcpAdapter = new TCPAdapter();
            ResultDto<String> dto = otherTcpAdapter.invoke(msg, node, String.class);
            if (ResultDtoTool.isSuccess(dto)) {
                cfgContentMap.put(node.getName(), dto.getData());
            } else {
                cfgContentMap.put(node.getName(), null);
            }
        }

        String currCfgContentNoSpace = currCfgContent.replaceAll(" |\n|\t", "");
        List<String> notSameList = new ArrayList<>();
        for (Map.Entry<String, String> entry : cfgContentMap.entrySet()) {
            String value = entry.getValue();
            if (value == null) continue;
            Document docRm = XMLDealTool.readXml(value);
            String asXmlRm = XMLDealTool.withoutTimestamp(docRm).asXML();
            String asXml2Temp = asXmlRm.replaceAll(" |\n|\t", "");
            if (!StringUtils.equals(currCfgContentNoSpace, asXml2Temp)) {
                notSameList.add(entry.getKey());
            }
        }

        if (!notSameList.isEmpty()) {
            mapm.put("resultSyn", "文件不相同，请先核对之后，再进行同步操作。");
        } else {
            mapm.put("resultSyn", "文件相同，不需要进行同步操作。");
        }

        mapm.put("otherFsnConfMap", cfgContentMap);
        mapm.put("ftServiceNodeList", activeNodeList);
        mapm.put("fileName", fileName);
        mapm.put("currCfgContent", currCfgContent);
        mapm.put("inactiveNodeList", inactiveNodeList);

        return   ResultDtoTool.buildSucceed(mapm);
    }

    /**
     *    同步 操作
     * @param fileName
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "sync", produces = "application/json;charset=UTF-8")
    public Object sync(@RequestParam String fileName, HttpServletRequest request) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return ResultDtoTool.buildError("请先设置节点组！！！");
        }
        String sysname = ftServiceNode.getSystemName();
        String content = getCurrCfgContent(sysname, fileName, true);
        cfgZkService.write(sysname, fileName, content.trim());
        return   ResultDtoTool.buildSucceed("成功。");

    }

    /**
     *  所以一起同步
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value ="syncAll", produces = "application/json;charset=UTF-8")
    public Object syncAll(HttpServletRequest request) throws IOException {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return  ResultDtoTool.buildError("请先设置节点组！！！");
        }
        String sysname = ftServiceNode.getSystemName();
        List<String> cfgFileNameList = new ArrayList<>();
        cfgFileNameList.add("components.xml");
        cfgFileNameList.add("crontab.xml");
        cfgFileNameList.add("file.xml");
        cfgFileNameList.add("file_clean.xml");
        cfgFileNameList.add("file_rename.xml");
        cfgFileNameList.add("flow.xml");
        cfgFileNameList.add("nodes.xml");
        cfgFileNameList.add("route.xml");
        cfgFileNameList.add("services_info.xml");
        cfgFileNameList.add("system.xml");
        cfgFileNameList.add("user.xml");
        cfgFileNameList.add("vsysmap.xml");
        cfgFileNameList.add("client_status.xml");
        cfgFileNameList.add("keys.xml");
        for (String fileName : cfgFileNameList) {
            String content = getCurrCfgContent(sysname, fileName, true);
            cfgZkService.write(sysname, fileName, content);
        }

        return  ResultDtoTool.buildSucceed("成功。");
    }

    private String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException {
        return cfgFileService.getCurrCfgContent(sysname, fileName, hasTimestamp);
    }

}
