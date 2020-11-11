package com.dc.smarteam.modules.file.web;

import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.file.service.BizFileDownloadLogService;
import com.dc.smarteam.modules.file.service.BizFileTransLogWarnService;
import com.dc.smarteam.modules.file.service.BizFileUploadLogService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yangyga on 2017/8/3.
 */
@Controller
@RequestMapping(value = "${adminPath}/file/BizFileStatistics")
public class BizFileStatisticsController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(BizFileStatisticsController.class);

    @Autowired
    private BizFileTransLogWarnService bizFileTransLogWarnService;
    @Autowired
    private BizFileUploadLogService bizFileUploadLogService;
    @Autowired
    private BizFileDownloadLogService bizFileDownloadLogService;
    //add 20170904 单位换算1024*1024
    private static final int BASE_BYTE = 1048576;

    @RequiresPermissions("NodeMonitor:ftNodeMonitor:view")
    @RequestMapping(value = {"viewPage"})
    public String viewPage() {
        return "modules/monitor/transforMonitor/ftWorking";
    }

    @RequiresPermissions("NodeMonitor:ftNodeMonitor:view")
    @RequestMapping(value = {"01", "02"})
    public void getTimelyData(HttpServletRequest request, HttpServletResponse response, Model model) {

        Map<String, JSONObject> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        List<String> list1 = new ArrayList<>();
        List<Date> list2 = new ArrayList<>();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.MINUTE, -1);
            String time1 = sdf.format(calendar.getTime());
            String time2 = sdf2.format(calendar.getTime());
            list1.add(time1);
            try {
                list2.add(sdf2.parse(time2));
            } catch (ParseException e) {
                log.error("", e);
            }
        }
        Object[] strArr1 = list1.toArray();
        Object[] strArr2 = list2.toArray();
        Date beginDate;
        Date endDate;
        for (int i = strArr2.length - 1; i >= 0; i--) {
            beginDate = (Date) strArr2[i];
            endDate = (Date) strArr2[i - 1];
            String time = (String) strArr1[i];
            JSONObject jsonObject;
            if (map1.containsKey(time)) {
                jsonObject = map1.get(time);//NOSONAR
            } else {
                map2.put("beginDate", beginDate);
                map2.put("endDate", endDate);

                Long findUploadTotal = bizFileUploadLogService.findListByTime2(map2);
                Long findDownloadTotal = bizFileDownloadLogService.findListByTime2(map2);
                Long findTotal = findUploadTotal + findDownloadTotal;
                int findDownloadTotalFlow = Integer.parseInt(bizFileTransLogWarnService.findDownloadFlow(map2).toString());
                int findUploadTotalFlow = Integer.parseInt(bizFileTransLogWarnService.findUploadFlow(map2).toString());
                int findTotalFlow = findDownloadTotalFlow + findUploadTotalFlow;

                jsonObject = new JSONObject();
                jsonObject.put("time", time);
                jsonObject.put("Total", findTotal);
                jsonObject.put("DownloadTotal", findDownloadTotal);
                jsonObject.put("UploadTotal", findUploadTotal);
                jsonObject.put("TotalFlow", findTotalFlow / BASE_BYTE);
                jsonObject.put("DownloadTotalFlow", findDownloadTotalFlow / BASE_BYTE);
                jsonObject.put("UploadTotalFlow", findUploadTotalFlow / BASE_BYTE);
                map1.put(time, jsonObject);
            }
            if (i - 1 == 0) {
                break;
            }
        }
        Object[] objects = map1.values().toArray();
        Arrays.sort(objects, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                JSONObject jo1 = (JSONObject) o1;
                JSONObject jo2 = (JSONObject) o2;
                String time1 = (String) jo1.get("time");
                String time2 = (String) jo2.get("time");
                return time1.compareTo(time2);
            }
        });
        JSONArray jsonArray = new JSONArray();
        for (Object jsonObject : objects) {
            jsonArray.add(jsonObject);
        }
        renderString(response, jsonArray);
    }

}


