package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.model.Node;
import com.dcfs.esb.ftp.common.model.PushDataNodeInfo;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.cons.NodeState;
import com.dcfs.esb.ftp.helper.CacheUpdateHelper;
import com.dcfs.esb.ftp.helper.NodeListHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.invoke.node.NodesWorker;
import com.dcfs.esb.ftp.server.invoke.node.VsysmapWorker;
import com.dcfs.esb.ftp.utils.EmptyUtils;
import com.dcfs.esb.ftp.utils.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mocg on 2016/8/22.
 */
public class PushDataNodeListService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(PushDataNodeListService.class);
    private String dataNodeListInfoCache = null;

    @Override
    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        String clientNodelistVersion = bean.getClientNodelistVersion();
        String nodelistVersion = SysContent.getInstance().getNodelistVersion();
        bean.setServerNodelistVersion(nodelistVersion);
        long nano = context.getCxtBean().getNano();
        String flowNo = context.getCxtBean().getFlowNo();
        CapabilityDebugHelper.markCurrTime("PushDataNodeListService middle");
        if (StringUtils.isEmpty(clientNodelistVersion) || !clientNodelistVersion.equals(nodelistVersion)) {
            bean.setNodeList(getDataNodeListInfo());
            bean.setVsysmap(getVsysmap());
            log.debug("nano:{}#flowNo:{}#datanode列表下发到API客户端#cliVers:{}-svcVers:{}#list:{}", nano, flowNo, clientNodelistVersion, nodelistVersion, bean.getNodeList());
        } else {
            log.debug("nano:{}#flowNo:{}#版本号相同,不下发datanode列表到API客户端#cliVers:{}", nano, flowNo, clientNodelistVersion);
            bean.setNodeList(null);
            bean.setVsysmap(null);
        }
    }

    private String getDataNodeListInfo() {
        if (dataNodeListInfoCache != null && CacheUpdateHelper.isLatestDataNodeList()) return dataNodeListInfoCache;
        //获取正常非隔离的节点列表
        List<Node> nodes = NodesWorker.getInstance().listDataNodesByStateAndIsolState(NodeState.RUNNING.num(), 0);
        //[{nodeName:"",addr:"ip:port",sysName:"",ms:""},...]
        List<PushDataNodeInfo> infoList = new ArrayList<>();
        for (Node node : nodes) {
            String sysName = null;
            if (EmptyUtils.isNotEmpty(node.getSystems())) {
                sysName = node.getSystems().get(0);
            }
            String ms = null;
            String nodeModel = node.getModel();
            if ("ms-m".equals(nodeModel)) ms = "m";
            else if ("ms-s".equals(nodeModel)) ms = "s";
            //手动模式的备节点不下发到API
            if ("s".equals(ms) && "handle".equals(node.getSwitchModel())) continue;

            //apiCmdPort与cmdPort不同
            //int apiCmdPort = node.getCmdPort()
            infoList.add(new PushDataNodeInfo(node.getName(), node.getIp() + ":" + node.getFtpServPort(), sysName, ms, null));
        }
        String str = null;
        if (!infoList.isEmpty()) str = GsonUtil.toJson(infoList);
        dataNodeListInfoCache = str;
        CacheUpdateHelper.setLatestDataNodeList(true);
        //默认的 便于测试
        if (str == null) str = NodeListHelper.getDefNodeListStrForTest();
        return str;
    }

    private String getVsysmap() {
        Map<String, String> vsysmap = VsysmapWorker.getInstance().getVsysmap();
        return vsysmap.size() == 0 ? null : GsonUtil.toJson(vsysmap);
    }

}
