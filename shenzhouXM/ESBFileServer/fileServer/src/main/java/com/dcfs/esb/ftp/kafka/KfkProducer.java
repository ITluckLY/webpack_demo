package com.dcfs.esb.ftp.kafka;

import com.dcfs.esb.ftp.common.cons.KfkTopic;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.helper.KfkPropertiesHelper;
import com.dcfs.esb.ftp.utils.MClassLoaderUtil;
import com.dcfs.esb.ftp.utils.NullDefTool;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import com.dcfs.esb.ftp.utils.StringTool;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by mocg on 2016/7/26.
 */
public class KfkProducer {
    private static KfkProducer ourInstance = new KfkProducer();
    private KfkOneProducer[] kfkProducerArr;
    private int poolSize;
    private int index;

    private static final int DEF_POOL_SIZE = 10;
    private static final int MAX_INDEX = 1000;

    private KfkProducer() {
    }

    public static KfkProducer getInstance() {
        return ourInstance;
    }

    private synchronized void init0() throws IOException {
        if (kfkProducerArr != null) return;
        Properties props = new Properties();
        KfkPropertiesHelper.putBootstrapServers(props);
        PropertiesTool.load(props, MClassLoaderUtil.getResourceFile("kafka-producer.properties"));
        poolSize = StringTool.toInt(props.getProperty("self.poolsize"), DEF_POOL_SIZE);
        if (poolSize < 1) poolSize = DEF_POOL_SIZE;
        KfkPropertiesHelper.removeSelf(props);
        kfkProducerArr = new KfkOneProducer[poolSize];
        for (int i = 0; i < poolSize; i++) {
            Properties oneProps = (Properties) props.clone();
            kfkProducerArr[i] = new KfkOneProducer(oneProps);
        }
    }

    public void init() throws IOException {
        if (kfkProducerArr == null) init0();
    }

    private int currIndex() {
        int ind = index++;
        if (ind > MAX_INDEX) index = 0;
        return ind < 0 ? 0 : ind;
    }

    public void send(KfkTopic topic, final String message) throws IOException {
        send(topic.name(), message, NanoHelper.nanos(), null);
    }

    public void send(final String topic, final String message) throws IOException {
        send(topic, message, NanoHelper.nanos(), null);
    }

    public void send(KfkTopic topic, final String message, final long nano) throws IOException {
        send(topic.name(), message, nano, null);
    }

    public void send(KfkTopic topic, final String message, final Long nano) throws IOException {
        send(topic.name(), message, NullDefTool.defNull(nano, 0), null);
    }

    public void send(KfkTopic topic, final String message, final long nano, final String flowNo) throws IOException {
        send(topic.name(), message, nano, flowNo);
    }

    public void send(KfkTopic topic, final String message, final Long nano, final String flowNo) throws IOException {
        send(topic.name(), message, NullDefTool.defNull(nano, 0), flowNo);
    }

    public void send(final String topic, final String message, final long nano, final String flowNo) throws IOException {
        CapabilityDebugHelper.markCurrTime("kfkSendBegin");
        init();
        KfkOneProducer oneProducer = kfkProducerArr[currIndex() % poolSize];
        oneProducer.send(topic, message, nano, flowNo);
        CapabilityDebugHelper.markCurrTime("kfkSendEnd");
    }

    public void send(KfkTopic topic, List<String> messages) throws IOException {
        init();
        KfkOneProducer oneProducer = kfkProducerArr[currIndex() % poolSize];
        oneProducer.send(topic, messages);
    }

    public void flush() {
        if (kfkProducerArr != null) {
            for (KfkOneProducer oneProducer : kfkProducerArr) {
                oneProducer.flush();
            }
        }
    }

    public void close() {
        if (kfkProducerArr != null) {
            for (KfkOneProducer oneProducer : kfkProducerArr) {
                oneProducer.close();
            }
        }
        kfkProducerArr = null;
    }
}
