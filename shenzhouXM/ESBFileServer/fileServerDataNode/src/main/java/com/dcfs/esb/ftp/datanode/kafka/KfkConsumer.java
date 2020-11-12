package com.dcfs.esb.ftp.datanode.kafka;

import com.dcfs.esb.ftp.common.cons.KfkTopic;
import com.dcfs.esb.ftp.helper.KfkPropertiesHelper;
import com.dcfs.esb.ftp.servcomm.BaseKfkConsumer;
import com.dcfs.esb.ftp.servcomm.cons.KfkCons;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.file.FileManager;
import com.dcfs.esb.ftp.server.invoke.node.NodeWorker;
import com.dcfs.esb.ftp.server.model.SameFileDeleteRecord;
import com.dcfs.esb.ftp.utils.DateUtil;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import com.dcfs.esc.ftp.comm.constant.UnitCons;
import org.apache.commons.lang3.StringUtils;
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
    private static final Logger logKfk = LoggerFactory.getLogger("kfk.com.dcfs.esb.ftp.datanode.kafka.KfkConsumer");
    private static KfkConsumer ourInstance = new KfkConsumer();
    private KafkaConsumer<String, String> consumer;
    private String selfTopics;
    private String selfSysnameTopics;
    private String nodeName;
    private String sysName;
    private boolean isRunning = true;
    private String sysnameEfsFileDelSame;
    private String upperSysname;
    private static final int MAX_NO_RECORD_COUNT = 2;
    private int noRecordSum = 0;
    private boolean toConsumeDelFileRecord = true;

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
        selfSysnameTopics = props.getProperty("self.sysname.topics");
        //每个dataNode一个group.id
        nodeName = FtpConfig.getInstance().getNodeName();
        sysName = NodeWorker.getInstance().getSysName();
        String groupId = props.getProperty("group.id", "efs-data-grp");
        props.put("group.id", groupId + "-" + nodeName);
        KfkPropertiesHelper.appendHostToGroupId(props);
        KfkPropertiesHelper.removeSelf(props);
        if (log.isDebugEnabled()) {
            log.debug("bootstrap.servers = {}", props.getProperty("bootstrap.servers"));
            log.debug("kafka props:\r\n{}", PropertiesTool.print(props));
            log.debug("kafka props is {}", props.toString());
        }
        consumer = new KafkaConsumer<>(props);
        if (sysName != null) upperSysname = sysName.toUpperCase();
        sysnameEfsFileDelSame = upperSysname + "_" + KfkTopic.EFS_FILE_DEL_SAME;
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
        //topic前面添加当前节点的所属系统名称作为新的topic
        if (StringUtils.isNoneEmpty(selfSysnameTopics, sysName)) {
            String[] topicArr = selfSysnameTopics.split(",");
            for (String topic : topicArr) {
                topic = upperSysname + "_" + topic.trim();
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
            if (toConsumeDelFileRecord) {
                boolean hasNext = records.records(sysnameEfsFileDelSame).iterator().hasNext();
                if (hasNext) noRecordSum = 0;
                else noRecordSum++;
                if (noRecordSum > MAX_NO_RECORD_COUNT) toConsumeDelFileRecord = false;
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
            //24小时内有效
            if (DateUtil.compareToNowLess(record.timestamp(), 1000L * 60 * 60 * 24)) {//NOSONAR
                logKfk.debug(String.format("topic = %s ,offset = %d, key = %s, value = %s ", record.topic(), record.offset(), record.key(), record.value()));
            }
        }
        String topic = record.topic();
        String value = record.value();
        KfkTopic kfkTopic = valueOfTopic(topic);

        if (kfkTopic == null) {//topic是动态时
            if (StringUtils.equals(sysnameEfsFileDelSame, topic)) {//NOSONAR
                SameFileDeleteRecord sdr = GsonUtil.fromJson(value, SameFileDeleteRecord.class);
                //同一个系统的其他节点
                if (StringUtils.equals(sysName, sdr.getSysname()) && !StringUtils.equals(nodeName, sdr.getFromNodeName())) {
                    String fileName = sdr.getFilePath();
                    String result = FileManager.getInstance().delFile(fileName, sdr.getNewVersion());
                    log.info("delFile:{},version:{}#{}", fileName, sdr.getNewVersion(), result);
                } else {
                    log.info("不是同一个系统的其他节点#delFile:{},version:{}", sdr.getFilePath(), sdr.getNewVersion());
                }
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

    public boolean isToConsumeDelFileRecord() {
        return toConsumeDelFileRecord;
    }

    public int getNoRecordSum() {
        return noRecordSum;
    }
}
