package com.dcfs.esb.ftp.helper;

import com.dcfs.esb.ftp.server.config.CommCfgCxtKey;
import com.dcfs.esb.ftp.spring.EfsProperties;
import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esb.ftp.utils.InetAddressUtil;

import java.util.Properties;

/**
 * Created by mocg on 2016/7/29.
 */
public class KfkPropertiesHelper {
    protected KfkPropertiesHelper() {
    }

    public static void removeSelf(Properties props) {
        props.remove("self.topics");
        props.remove("self.commTopics");
        props.remove("self.poolsize");
    }

    public static void appendHostToGroupId(Properties props) {
        String groupId = props.getProperty("group.id");
        boolean ifKfkGroupIdAppendHost = ContextHelper.getDefaultByCommCfgCxt(CommCfgCxtKey.IF_KFK_GROUP_ID_APPEND_HOST, false);
        if (ifKfkGroupIdAppendHost) {
            String hostName = InetAddressUtil.getHostName();
            if (hostName != null) props.put("group.id", groupId + "-" + hostName);
        }
    }

    public static void putBootstrapServers(Properties props) {
        EfsProperties efsProperties = SpringContext.getInstance().getEfsProperties();
        props.put("bootstrap.servers", efsProperties.getKfkBootstrapServers());
    }
}
