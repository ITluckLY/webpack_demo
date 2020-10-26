package com.dc.smarteam.service;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.NodesModel;
import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface NodesService {

    //获取nodeName的最后三位数字
    public static final Pattern nodeIdPatt = Pattern.compile("^.*([\\d]{3})$");

    public static final String CFG_FILE_NAME = "nodes.xml";

    ResultDto<String> select(FtServiceNode node, HttpServletRequest request);

    ResultDto<List<NodesModel.Node>> listAll(FtServiceNode ftServiceNode, HttpServletRequest request);

    ResultDto<String> selByName0(FtServiceNode node, HttpServletRequest request);

    ResultDto<NodesModel.Node> selByName(FtServiceNode ftServiceNode, HttpServletRequest request);

    ResultDto<String> add0(FtServiceNode node, HttpServletRequest request);

    ResultDto<String> add(FtServiceNode ftServiceNode, HttpServletRequest request);

    ResultDto<String> update0(FtServiceNode node, HttpServletRequest request);

    ResultDto<String> update(FtServiceNode ftServiceNode, HttpServletRequest request);

    ResultDto<String> del0(FtServiceNode node, HttpServletRequest request);

    ResultDto<String> del(FtServiceNode ftServiceNode, HttpServletRequest request);

    ResultDto<Boolean> switchSystemModel(FtServiceNode ftServiceNode);

    NodesModel loadModel();

    void save(NodesModel model);
}
