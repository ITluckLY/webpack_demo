package com.dcfs.esb.ftp.servcomm;

import com.dcfs.esb.ftp.common.cons.KfkTopic;
import com.dcfs.esb.ftp.helper.KfkPropertiesHelper;
import com.dcfs.esb.ftp.utils.MClassLoaderUtil;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by mocg on 2016/8/24.
 */
public abstract class BaseKfkConsumer {
    private static final int MAX_COUNT = 1000;
    private List<String> recordFlagList1 = new ArrayList<>(MAX_COUNT);
    private List<String> recordFlagList2 = new ArrayList<>(MAX_COUNT);
    private int count = 0;
    private List<String> currList = recordFlagList1;
    private List<String> currListBak = recordFlagList2;

    /**
     * 消息排重,判断最近一段时间内是否已接收过相同的消息
     *
     * @param record
     * @return
     */
    protected boolean haveDoneRecord(ConsumerRecord<String, String> record) {
        String value = record.value();
        String str = "\"id\":";
        int idIndex1 = value.indexOf(str);
        if (idIndex1 == -1) return false;
        idIndex1 += str.length();
        int idIndex2 = value.indexOf(',', idIndex1);
        String id = value.substring(idIndex1, idIndex2);
        String key = id + "_" + record.topic();
        if (recordFlagList1.contains(key) || recordFlagList2.contains(key)) return true;

        if (count >= MAX_COUNT) trunCurrList();
        currList.add(key);
        count++;
        return false;
    }

    //当两个list都满时，才clear前一个list
    private void trunCurrList() {
        count = 0;
        currListBak.clear();
        currListBak = currList;
        if (currList.equals(recordFlagList1)) currList = recordFlagList2;
        else currList = recordFlagList1;
    }

    protected KfkTopic valueOfTopic(String topicName) {
        KfkTopic kfkTopic = null;
        try {
            kfkTopic = KfkTopic.valueOf(topicName);
        } catch (IllegalArgumentException ignored) {
            //nothing
        }
        return kfkTopic;
    }

    protected void loadCosumerProperties(Properties props) throws IOException {
        KfkPropertiesHelper.putBootstrapServers(props);
        PropertiesTool.load(props, MClassLoaderUtil.getResourceFile("kafka-cosumer.properties"));
    }
}
