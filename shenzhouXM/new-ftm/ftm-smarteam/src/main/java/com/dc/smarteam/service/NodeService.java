package com.dc.smarteam.service;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.cfgfile.entity.CfgFile;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class NodeService {
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<String> select(FtServiceNode node, HttpServletRequest request) {
        CfgFile cfgFile = new CfgFile();
        cfgFile.setFileName("nodes.xml");
        cfgFile.setNodeType("NAMENODE");
        cfgFileService.save(cfgFile);

        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String saveStr = MessageFactory.getInstance().nodes(node, "select");
        TCPAdapter tcpAdapter = new TCPAdapter();
        return tcpAdapter.invoke(saveStr, ftServiceNode, String.class);//发送报文
    }
}
