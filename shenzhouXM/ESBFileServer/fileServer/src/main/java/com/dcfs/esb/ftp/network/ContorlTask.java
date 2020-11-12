package com.dcfs.esb.ftp.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ContorlTask extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(ContorlTask.class);
    private int maxspeed;
    private int currspeed = 0;
    private int sleeptime = 0;//NOSONAR
    private Random random;
    private List<Channel> list;

    public ContorlTask() {
        this.maxspeed = ControlUtil.getInstance().getNetWorkSpeed();
        this.sleeptime = ControlUtil.getInstance().getSleepTime();
        this.currspeed = 0;
        this.sleeptime = 0;
        this.list = new LinkedList<>();
        this.random = new Random();
    }

    @Override
    public void run() {
        currspeed = 0;
        Map<String, Channel> map = ControlUtil.getInstance().getChannelCollMap();
        int max = map.size() - 1;
        //log.info("开始扫描当前网络带宽 map.szie: " + (max + 1));
        for (Channel channel : map.values()) {
            if (log.isDebugEnabled()) {
                log.debug(channel.getChannelName() + " : " + channel.getSpeed() + "KB/S");
            }
            list.add(channel);
            if (channel.getIsUse()) {
                currspeed += channel.getSpeed();
            }
        }
        //log.info("当前的占用带宽： " + this.currspeed + "KB/S"
//				+ " 允许的最大速度: " + maxspeed + "KB/S");
        // 如果当前所有渠道的速度大于设置的最大带宽，则随机休眠某个渠道
        if ((currspeed > maxspeed) && (map.size() >= 2)) {
            int min = 1;
            int index = random.nextInt(max - min + 1) + min;
            Channel channel = list.get(index);
            if (channel.getIsUse()) {
                //log.info("设置渠道" + channel.getChannelName() + "开始休眠");
                channel.setIsUse(false);
            }
        }

        list.clear();
        //log.info("================== LINE ===================");
        /*
         * if (currspeed > maxspeed) { for (Channel channel : map.values()) {
		 * int num = random.nextInt(2); if (num == 1) { if (channel.getIsUse())
		 * { System.out.println("设置渠道" + channel.getChannelName() + "开始休眠");
		 * channel.setIsUse(false); } } } }
		 */

    }

}
