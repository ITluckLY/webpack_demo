package com.dcfs.esb.ftp.namenode.handler;


import com.dcfs.esb.ftp.kafka.KfkProducer;
import com.dcfs.esb.ftp.namenode.kafka.KfkCommConsumer;
import com.dcfs.esb.ftp.namenode.kafka.KfkConsumer;
import com.dcfs.esb.ftp.zk.ZkClientTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cgmo on 2016/7/29.
 */
public class ShutdownHookHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ShutdownHookHandler.class);

    @Override
    public void run() {
        log.info("关闭应用时先关闭kafka");
        KfkProducer.getInstance().flush();
        KfkProducer.getInstance().close();

        KfkCommConsumer.getInstance().close(false);
        KfkConsumer.getInstance().close(false);

        log.info("关闭应用时先关闭zookeeper");
        ZkClientTool.getInstance().close();
    }
}
