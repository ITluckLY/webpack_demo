package com.dcfs.esc.ftp.datanode.executor;

import com.dcfs.esc.ftp.comm.dto.*;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.process.NodeListProcess;

/**
 * Created by mocg on 2017/6/3.
 */
public class NodeListExecutor extends BaseBusinessExecutor {

    private NodeListProcess process = new NodeListProcess();

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends BaseDto & RspDto, D extends BaseDto & ReqDto> T invoke0(ChannelContext channelContext, D dto, Class<T> tClass) throws Exception {
        NodeListReqDto reqDto = (NodeListReqDto) dto;
        log.debug("nano:{}#start doNodeList...", channelContext.getNano());
        NodeListRspDto rspDto = process.doProcess(channelContext, reqDto);
        return (T) rspDto;
    }
}
