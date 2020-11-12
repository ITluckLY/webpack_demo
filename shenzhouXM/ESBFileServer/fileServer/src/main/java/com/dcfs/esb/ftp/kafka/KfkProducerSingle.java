package com.dcfs.esb.ftp.kafka;

import com.dcfs.esb.ftp.common.cons.KfkTopic;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.helper.KfkPropertiesHelper;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.MClassLoaderUtil;
import com.dcfs.esb.ftp.utils.NullDefTool;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by mocg on 2016/7/26.
 */

public class KfkProducerSingle {
    private static final Logger log = LoggerFactory.getLogger(KfkProducerSingle.class);
    private static KfkProducerSingle ourInstance = new KfkProducerSingle();
    private Producer<String, String> producer;

    private KfkProducerSingle() {
    }

    public static KfkProducerSingle getInstance() {
        return ourInstance;
    }

    private synchronized void init0() throws IOException {
        if (producer != null) return;
        Properties props = new Properties();
        KfkPropertiesHelper.putBootstrapServers(props);
        PropertiesTool.load(props, MClassLoaderUtil.getResourceFile("kafka-producer.properties"));
        KfkPropertiesHelper.removeSelf(props);
        producer = new KafkaProducer<>(props);
    }

    public void init() throws IOException {
        if (producer == null) init0();
    }

    public void send(KfkTopic topic, final String message) throws IOException {
        send(topic.name(), message, NanoHelper.nanos());
    }

    public void send(final String topic, final String message) throws IOException {
        send(topic, message, NanoHelper.nanos());
    }

    public void send(KfkTopic topic, final String message, final long nano) throws IOException {
        send(topic.name(), message, nano);
    }

    public void send(KfkTopic topic, final String message, final Long nano) throws IOException {
        send(topic.name(), message, NullDefTool.defNull(nano, 0));
    }

    public void send(final String topic, final String message, final long nano) throws IOException {
        CapabilityDebugHelper.markCurrTime("singlekfkSendBegin");
        init();
        if (log.isDebugEnabled()) log.debug("singlekafka发送内容#nano:{}-{}#{}-{}", nano, System.currentTimeMillis(), topic, message);
        producer.send(new ProducerRecord<String, String>(topic, null, message), new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (log.isDebugEnabled()) {
                    log.debug("singlekafka发送结果#nano:{}-{}#{}", nano, System.currentTimeMillis(), GsonUtil.toJson(metadata));
                }
                if (exception != null) {
                    log.error("singlekafka发送出错#nano:{}#topic:{}", nano, topic, exception);
                }
            }
        });
        producer.flush();
        CapabilityDebugHelper.markCurrTime("singlekfkSendEnd");
    }

    public void send(KfkTopic topic, List<String> messages) throws IOException {
        init();
        for (String message : messages) {
            producer.send(new ProducerRecord<String, String>(topic.name(), null, message));
        }
        producer.flush();
    }

    public void flush() {
        if (producer != null) producer.flush();
    }

    public void close() {
        if (producer != null) producer.close();
        producer = null;
    }
}
