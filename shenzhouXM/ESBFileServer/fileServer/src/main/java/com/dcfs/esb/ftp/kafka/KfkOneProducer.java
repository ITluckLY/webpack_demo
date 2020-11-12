package com.dcfs.esb.ftp.kafka;

import com.dcfs.esb.ftp.common.cons.KfkTopic;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.NullDefTool;
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
public class KfkOneProducer {
    private static final Logger log = LoggerFactory.getLogger(KfkOneProducer.class);
    private Producer<String, String> producer;
    private boolean useFlush = true;

    public KfkOneProducer(Properties kfkProps) {
        useFlush = BooleanTool.toBoolean(kfkProps.getProperty("self.use.flush", "true"));
        producer = new KafkaProducer<>(kfkProps);
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

    public void send(final String topic, final String message, final long nano, final String flowNo) throws IOException {//NOSONAR
        CapabilityDebugHelper.markCurrTime("onekfkSendBegin");
        if (log.isDebugEnabled()) log.debug("kafka发送内容#nano:{}#flowNo:{}#{}-{}", nano, flowNo, topic, message);
        producer.send(new ProducerRecord<String, String>(topic, null, message), new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (log.isDebugEnabled()) {
                    log.debug("kafka发送结果#nano:{}#flowNo:{}#{}", nano, flowNo, GsonUtil.toJson(metadata));
                }
                if (exception != null) {
                    log.error("kafka发送出错#nano:{}#flowNo:{}#topic:{},message:{}", nano, flowNo, topic, message, exception);
                }
            }
        });
        if (useFlush) producer.flush();
        CapabilityDebugHelper.markCurrTime("onekfkSendEnd");
    }

    public void send(KfkTopic topic, List<String> messages) throws IOException {//NOSONAR
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
