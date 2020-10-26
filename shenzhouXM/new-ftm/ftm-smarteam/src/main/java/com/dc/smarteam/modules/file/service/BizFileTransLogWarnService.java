package com.dc.smarteam.modules.file.service;

import com.dc.smarteam.modules.file.dao.BizFileDownloadLogMapper;
import com.dc.smarteam.modules.file.dao.BizFileTransLogWarnDao;
import com.dc.smarteam.modules.file.dao.BizFileUploadLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by yangyga on 2017/8/3.
 */

@Service
public class BizFileTransLogWarnService {

    @Autowired
    private BizFileDownloadLogMapper bizFileDownloadLogDao;
    @Autowired
    private BizFileUploadLogMapper bizFileUploadLogDao;
    @Autowired
    private BizFileTransLogWarnDao bizFileTransLogWarnDao;

    public Long findTotal(Map map) {
        return bizFileTransLogWarnDao.findTotal(map);
    }

    public Long findDownloadTotal(Map map) {
        return bizFileTransLogWarnDao.findDownloadTotal(map);
    }

    public Long findUploadTotal(Map map) {
        return bizFileTransLogWarnDao.findUploadTotal(map);
    }

    public Long findTotalFlow(Map map) {
        return bizFileTransLogWarnDao.findTotalFlow(map);
    }

    public Long findDownloadTotalFlow(Map map) {
        return bizFileTransLogWarnDao.findDownloadTotalFlow(map);
    }

    public Long findUploadTotalFlow(Map map) {
        return bizFileTransLogWarnDao.findUploadTotalFlow(map);
    }


//    public  void timelyDate(){
//        //获取要同步的一分钟
//        Calendar calendar = Calendar.getInstance();
//        Map<String,Object> map = new HashMap<>();
//        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date startTime = null;
//        Date endTime = null;
//        try {
//        String str1 = sdf.format(calendar.getTime());
//        endTime = sdf.parse(str1);
//        calendar.add(Calendar.MINUTE, -1);
//        String str2 = sdf.format(calendar.getTime());
//        startTime = sdf.parse(str2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        map.put("startTime",startTime);
//        map.put("endTime", endTime);
//        //获取要同步的数据
//       List<BizFileDownloadLog> downloadLogs = bizFileDownloadLogDao.findFileListByTime(map);
//       List<BizFileUploadLog>  uploadLogs = bizFileUploadLogDao.findFileListByTime(map);
//        //遍历获取到数据并且插入到目标库
//        if(null != downloadLogs){
//
//            for(BizFileDownloadLog downloadLog : downloadLogs){
//                BizFileStatistics bizFileStatistics = new BizFileStatistics();
//                bizFileStatistics.setId(String.valueOf(UUIDService.nextId()));
//                bizFileStatistics.setEndTime(downloadLog.getEndTime());
//                bizFileStatistics.setErroCode(downloadLog.getErrCode());
//                bizFileStatistics.setFileName(downloadLog.getClientFileName());
//                bizFileStatistics.setFileSize((int) downloadLog.getFileSize());
//                bizFileStatistics.setLastPiece(downloadLog.isLastPiece()?1:0);
//                bizFileStatistics.setStartTime(downloadLog.getStartTime());
//                bizFileStatistics.setNodeName(downloadLog.getNodeNameTemp());
//                bizFileStatistics.setSuss(downloadLog.isSuss()?1:0);
//                bizFileStatistics.setOperator(downloadLog.getUname());
//                bizFileStatistics.setTransCode(downloadLog.getTranCode());
//                bizFileStatistics.setOperateType(0);
//                bizFileStatistics.setNoticeStat(0);
//            if(bizFileStatistics.getSuss() == 1 & bizFileStatistics.getLastPiece() == 1){
//                bizFileStatistics.setNoticeType(0);
//            }else{
//                bizFileStatistics.setNoticeType(1);
//            }
//                bizFileStatisticsDao.insert(bizFileStatistics);
//
//            }
//        }
//
//        if(null != downloadLogs){
//            for(BizFileUploadLog uploadLog : uploadLogs){
//                BizFileStatistics bizFileStatistics = new BizFileStatistics();
//                bizFileStatistics.setId(String.valueOf(UUIDService.nextId()));
//                bizFileStatistics.setEndTime(uploadLog.getEndTime());
//                bizFileStatistics.setErroCode(uploadLog.getErrCode());
//                bizFileStatistics.setFileName(uploadLog.getClientFileName());
//                bizFileStatistics.setFileSize((int) uploadLog.getFileSize());
//                bizFileStatistics.setLastPiece(uploadLog.isLastPiece()?1:0);
//                bizFileStatistics.setStartTime(uploadLog.getStartTime());
//                bizFileStatistics.setNodeName(uploadLog.getNodeNameTemp());
//                bizFileStatistics.setSuss(uploadLog.isSuss()?1:0);
//                bizFileStatistics.setOperator(uploadLog.getUname());
//                bizFileStatistics.setTransCode(uploadLog.getTranCode());
//                bizFileStatistics.setOperateType(1);
//                bizFileStatistics.setNoticeStat(0);
//                if(bizFileStatistics.getSuss() == 1 & bizFileStatistics.getLastPiece() == 1){
//                    bizFileStatistics.setNoticeType(0);
//                }else{
//                    bizFileStatistics.setNoticeType(1);
//                }
//                bizFileStatisticsDao.insert(bizFileStatistics);
//            }
//        }
//
//    }

    public Long findTotalSuss(Map map) {
        return bizFileTransLogWarnDao.findTotalSuss(map);
    }

    public Long findDownloadTotalSuss(Map map) {
        return bizFileTransLogWarnDao.findDownloadTotalSuss(map);
    }

    public Long findUploadTotalSuss(Map map) {
        return bizFileTransLogWarnDao.findUploadTotalSuss(map);
    }

    //add 20170904
    public Long findDownloadFlow(Map map) {
        return bizFileTransLogWarnDao.findDownloadFlow(map);
    }
    //add 20170904
    public Long findUploadFlow(Map map) {
        return bizFileTransLogWarnDao.findUploadFlow(map);
    }

}
