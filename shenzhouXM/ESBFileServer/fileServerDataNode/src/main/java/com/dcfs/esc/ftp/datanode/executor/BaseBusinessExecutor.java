package com.dcfs.esc.ftp.datanode.executor;

import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esc.ftp.comm.dto.BaseBusiDto;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.ContextBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实现需要保证线程安全
 * Created by mocg on 2017/6/3.
 */
public abstract class BaseBusinessExecutor {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public final <T extends BaseDto & RspDto, D extends BaseDto & ReqDto> T invoke(ChannelContext channelContext, D dto, Class<T> tClass) throws Exception {
        initContextBeanField(channelContext, channelContext.cxtBean(), dto);

        if (CapabilityDebugHelper.isDebug()) CapabilityDebugHelper.markCurrTime("BaseBusinessExecutor-invoke0-before#" + getClass().getSimpleName());

        T t = invoke0(channelContext, dto, tClass);

        if (CapabilityDebugHelper.isDebug()) CapabilityDebugHelper.markCurrTime("BaseBusinessExecutor-invoke0-after#" + getClass().getSimpleName());

        if (t == null && log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            for (StackTraceElement traceElement : Thread.currentThread().getStackTrace()) {
                builder.append(traceElement.toString()).append("\n");
            }
            log.debug("syserr#dto is null#{}#{}", channelContext.getNano(), builder.toString());
        }
        if (t == null) throw new NestedRuntimeException("syserr#dto is null#" + tClass.getName());
        return t;
    }

    protected void initContextBeanField(ChannelContext channelContext, ContextBean contextBean, BaseDto reqDto) {
        if (contextBean == null) return;
        contextBean.setUserIp(channelContext.getUserIp());
        if (reqDto instanceof BaseBusiDto) {
            BaseBusiDto busiDto = (BaseBusiDto) reqDto;
            contextBean.setByClient(busiDto.isByClient());
            contextBean.setApiVersion(busiDto.getApiVersion());
        }
    }

    protected abstract <T extends BaseDto & RspDto, D extends BaseDto & ReqDto> T invoke0(ChannelContext channelContext, D dto, Class<T> tClass) throws Exception;//NOSONAR

}
