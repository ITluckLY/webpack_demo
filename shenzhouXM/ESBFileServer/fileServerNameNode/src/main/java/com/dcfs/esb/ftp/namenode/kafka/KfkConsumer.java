package com.dcfs.esb.ftp.namenode.kafka;

import com.dcfs.esb.ftp.helper.KfkPropertiesHelper;
import com.dcfs.esb.ftp.servcomm.BaseKfkConsumer;
import com.dcfs.esb.ftp.servcomm.cons.KfkCons;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.DateUtil;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import com.dcfs.esc.ftp.comm.constant.UnitCons;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by mocg on 2016/7/26.
 */
public class KfkConsumer extends BaseKfkConsumer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(KfkConsumer.class);
    private static final Logger logKfk = LoggerFactory.getLogger("kfk.com.dcfs.esb.ftp.namenode.kafka.KfkConsumer");
    private static KfkConsumer ourInstance = new KfkConsumer();
    private KafkaConsumer<String, String> consumer;
    private String selfTopics;
    private String nodeName;
    private boolean isRunning = true;

    private KfkConsumer() {
    }

    public static KfkConsumer getInstance() {
        return ourInstance;
    }

    private synchronized void init0() throws IOException {
        if (consumer != null) return;
        Properties props = new Properties();
        loadCosumerProperties(props);
        selfTopics = props.getProperty("self.topics");
        //每个dataNode一个group.id
        nodeName = FtpConfig.getInstance().getNodeName();
        String groupId = props.getProperty("group.id", "efs-name-grp");
        props.put("group.id", groupId + "-" + nodeName);
        KfkPropertiesHelper.appendHostToGroupId(props);
        KfkPropertiesHelper.removeSelf(props);
        if (log.isDebugEnabled()) {
            log.debug("bootstrap.servers = {}", props.getProperty("bootstrap.servers"));
            log.debug("kafka props:\r\n{}", PropertiesTool.print(props));
            log.debug("kafka props is {}", props.toString());
        }
        consumer = new KafkaConsumer<>(props);
    }

    public void init() throws IOException {
        if (consumer == null) init0();
    }

    public void subscribe() throws IOException {
        init();
        List<String> topics = new ArrayList<>();
        if (StringUtils.isNotEmpty(selfTopics)) {
            String[] topicArr = selfTopics.split(",");
            for (String topic : topicArr) {
                topic = topic.trim();
                if (!topics.contains(topic)) {
                    topics.add(topic);
                }
            }
        }
        if (log.isDebugEnabled()) log.debug("topics:{}", Arrays.toString(topics.toArray()));
        isRunning = !topics.isEmpty();
        if (isRunning) consumer.subscribe(topics);
    }

    public void poll() throws IOException {
        init();
        while (isRunning) {
            if (consumer == null) break;
            ConsumerRecords<String, String> records = consumer.poll(KfkCons.POLL_TIMEOUT);
            for (ConsumerRecord<String, String> record : records) {
                try {
                    if (!haveDoneRecord(record)) dealRecord(record);
                } catch (Exception e) {
                    log.error("kafka-consumer.dealRecord err", e);
                }
            }
        }
        close0();
    }

    public void start() {
        log.info("开始启动私有的kafka消息者...#{}", nodeName);
        new Thread(this).start();
        log.info("启动私有的kafka消息者完成#{}", nodeName);
    }

    private void dealRecord(ConsumerRecord<String, String> record) {
        if (logKfk.isTraceEnabled()) {
            logKfk.trace(String.format("topic = %s ,offset = %d, key = %s, value = %s ", record.topic(), record.offset(), record.key(), record.value()));
        } else if (logKfk.isDebugEnabled()) {
            //2分钟内有效
            if (DateUtil.compareToNowLess(record.timestamp(), 1000L * 60 * 2)) {//NOSONAR
                logKfk.debug(String.format("topic = %s ,offset = %d, key = %s, value = %s ", record.topic(), record.offset(), record.key(), record.value()));
            }
        }
    }

    public void close() {
        close(true);
    }

    public void close(boolean isSleep) {
        log.info("close kfkConsumer...");
        isRunning = false;
        if (isSleep) {
            try {
                Thread.sleep(KfkCons.POLL_TIMEOUT + UnitCons.ONE_SECONDS_MILLIS);
            } catch (InterruptedException e) {
                log.error("", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized void close0() {
        log.info("close0 kfkConsumer...");
        if (consumer != null) consumer.close();
        consumer = null;
    }

    @Override
    public void run() {
        try {
            subscribe();
            poll();
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
