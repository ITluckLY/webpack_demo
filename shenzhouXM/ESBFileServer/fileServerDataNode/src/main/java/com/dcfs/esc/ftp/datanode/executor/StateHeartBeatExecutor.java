package com.dcfs.esc.ftp.datanode.executor;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.model.ClientRegisterRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BeanConverter;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.StateHeartbeatReqDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.StateHeartbeatRspDto;
import com.dcfs.esc.ftp.datanode.constant.DatanodeGlobalCons;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.ContextBean;
import com.dcfs.esc.ftp.datanode.context.HeartBeatContextBean;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * Created on 2017/11/29.
 */
public class StateHeartBeatExecutor extends BaseBusinessExecutor {

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends BaseDto & RspDto, D extends BaseDto & ReqDto> T invoke0(ChannelContext channelContext, D dto, Class<T> tClass) throws Exception {
        StateHeartbeatReqDto reqDto = (StateHeartbeatReqDto) dto;
        log.debug("#reqMsg{}", GsonUtil.toJson(reqDto));
        registerCliByCfg(reqDto, channelContext);
        StateHeartbeatRspDto rspDto = new StateHeartbeatRspDto();
        rspDto.setSucc(true);
        rspDto.setAlive(true);
        return (T) rspDto;
    }

    /**
     * 通过配置文件注册
     *
     * @param reqDto
     * @param channelContext
     * @return
     */
    public boolean registerCliByCfg(StateHeartbeatReqDto reqDto, ChannelContext channelContext) {
        if (reqDto == null
                || StringUtils.isEmpty(reqDto.getConfIp())
                || reqDto.getConfPort() <= 0) {
            log.info("StateHeartbeatReqDto is null");
            return false;
        }
        ContextBean cxtBean = channelContext.cxtBean();
        HeartBeatContextBean heartBeatContextBean = null;
        if (cxtBean instanceof HeartBeatContextBean) {
            heartBeatContextBean = (HeartBeatContextBean) cxtBean;
        } else {
            log.error("unknown cxtBean type{}", cxtBean.getClass());
            return false;
        }
        //1.客户端第一次连接
        if (StringUtils.isNotBlank(heartBeatContextBean.getStatus()) &&(
                DatanodeGlobalCons.CLIENT_STATUS_RUNNING
                        .equalsIgnoreCase(heartBeatContextBean.getStatus()))) {
            log.debug("Already connected to the server!");
            return true;

        }
        ((HeartBeatContextBean) cxtBean).setStatus(DatanodeGlobalCons.CLIENT_STATUS_RUNNING);
        //3.发送客户端启动流水。
        logClientRegister(reqDto, channelContext);

        return true;
    }

    //记录流水信息
    private void logClientRegister(StateHeartbeatReqDto reqDto, ChannelContext channelContext) {

        log.info("nano:{}#logRecord begin", reqDto.getNano());
        HeartBeatContextBean cxtBean = channelContext.cxtBean();
        if (cxtBean.getEndTime() == null) cxtBean.setEndTime(new Date());
        ClientRegisterRecord record = BeanConverter.strictConvertTo(cxtBean, ClientRegisterRecord.class);
        record.setNodeId(reqDto.getConfIp()+":"+String.valueOf(reqDto.getConfPort()));
        record.setUid(reqDto.getUid());
        record.setNodeName(reqDto.getUid()+"-"+reqDto.getConfIp()+":"+String.valueOf(reqDto.getConfPort()));
        record.setNodeIp(reqDto.getConfIp());
        record.setNodePort(String.valueOf(reqDto.getConfPort()));
        record.setConnectServerId(Cfg.getHostAddress());
        record.setStatus(cxtBean.getStatus());
        record.setNano(channelContext.getNano());
        record.setNodeType(NodeType.CLNODE.name());
        record.setSysName(FtpConfig.getInstance().getSystemName());
        record.setPasswd(reqDto.getPassword());
        record.setCmdPort(reqDto.getCmdPort());
        record.setUserDomainName(reqDto.getUserDomainName());
        record.setSuss(true);
        EsbFileService.getInstance().logClientRegister(record);
    }
}
