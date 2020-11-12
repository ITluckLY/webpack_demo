package com.dcfs.esb.ftp.server.invoke.crontab;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.schedule.Schedule;
import com.dcfs.esb.ftp.server.schedule.ScheduleManager;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.server.tool.XMLtoJSON;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CrontabManager {
    private static final Logger log = LoggerFactory.getLogger(CrontabManager.class);

    private static final String CRONTABPATH = Cfg.getCrontabCfg();
    private ResultDto<String> resultDto = new ResultDto<>();
    private Document doc;
    private Element root;
    private String id;
    private String description;
    private String nodeName;
    private String status;
    private String count;
    private String timeExp;
    private String mission;
    private String params;
    private String xpath;
    private String taskName;
    private String str = null;
    private Element task = null;

    private static final String F_STATUS = "status";
    private static final String F_DESCRIPTION = "description";
    private static final String NODE_NAME = "nodeName";
    private static final String F_COUNT = "count";
    private static final String TIME_EXP = "timeExp";
    private static final String F_MISSION = "mission";
    private static final String F_PARAMS = "params";
    private static final String TASK_NAME = "taskName";

    private CrontabManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadCrontab();
            XMLDealTool.withoutTimestamp(doc);
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static CrontabManager getInstance() {
        return new CrontabManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        id = MessDealTool.getString(data, "ID");
        description = MessDealTool.getString(data, F_DESCRIPTION);
        nodeName = MessDealTool.getString(data, NODE_NAME);
        status = MessDealTool.getString(data, F_STATUS);
        count = MessDealTool.getString(data, F_COUNT);
        timeExp = MessDealTool.getString(data, TIME_EXP);
        mission = MessDealTool.getString(data, F_MISSION);
        params = MessDealTool.getString(data, F_PARAMS);
        taskName = MessDealTool.getString(data, TASK_NAME);

        xpath = "/schedules/task[@ID='" + id + "']";
    }

    public ResultDto<String> add(JSONObject jsonObject) {//NOSONAR
        load(jsonObject);
        if (null == id || id.isEmpty()) {
            resultDto = ResultDtoTool.buildError("编号不能为空");
            return resultDto;
        }
        if (null == nodeName || nodeName.isEmpty()) {
            resultDto = ResultDtoTool.buildError("节点名不能为空");
            return resultDto;
        }
        if (null == count || count.isEmpty()) {
            resultDto = ResultDtoTool.buildError("执行次数不能为空");
            return resultDto;
        }
        if (null == status || status.isEmpty()) {
            resultDto = ResultDtoTool.buildError("执行状态不能为空");
            return resultDto;
        }
        if (null == mission || mission.isEmpty()) {
            resultDto = ResultDtoTool.buildError("调用流程不能为空");
            return resultDto;
        }
        if (null == params || params.isEmpty()) {
            resultDto = ResultDtoTool.buildError("参数不能为空");
            return resultDto;
        }
        if (null == taskName || taskName.isEmpty()) {
            resultDto = ResultDtoTool.buildError("任务名称不能为空");
            return resultDto;
        }
        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            resultDto = ResultDtoTool.buildError("添加失败，编号重复");
            return resultDto;
        }
        description = (StringUtils.isEmpty(description) ? "" : description);
        Map<String, String> p = new HashMap<>();
        String[] ps = params.split(",");
        for (String param : ps) {
            int idx = param.indexOf('=');
            String key = param.substring(0, idx);
            String value = param.substring(idx + 1);
            p.put(key, value);
        }
        Schedule sc = new Schedule(id, mission, nodeName, 0, description, timeExp, p);
        if (!(ScheduleManager.getInstance().addOne(id, sc))) {
            resultDto = ResultDtoTool.buildError("添加任务失败，存在重复ID的任务");
            return resultDto;
        }
        task = XMLDealTool.addChild("task", root);
        XMLDealTool.addProperty("ID", id, task);
        XMLDealTool.addProperty(TASK_NAME, taskName, task);
        XMLDealTool.addProperty(F_DESCRIPTION, description, task);
        XMLDealTool.addProperty(NODE_NAME, nodeName, task);
        if (null == timeExp || "".equals(timeExp)) {
            XMLDealTool.addProperty(F_STATUS, "0", task);
        } else {
            XMLDealTool.addProperty(F_STATUS, status, task);
        }
        XMLDealTool.addProperty(F_COUNT, count, task);
        Element timeExpNode = XMLDealTool.addChild(TIME_EXP, task);
        XMLDealTool.updateNode(timeExp, timeExpNode);
        Element missionNode = XMLDealTool.addChild(F_MISSION, task);
        XMLDealTool.updateNode(mission, missionNode);
        Element paramsNode = XMLDealTool.addChild(F_PARAMS, task);
        XMLDealTool.updateNode(params, paramsNode);
        str = XMLDealTool.xmlWriter(doc, CRONTABPATH);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);
        task = (Element) root.selectSingleNode(xpath);
        if (null == task) {
            log.error("没有找到指定的任务");//NOSONAR
            resultDto = ResultDtoTool.buildError("没有找到指定的任务");
            return resultDto;
        }
        root.remove(task);
        str = XMLDealTool.xmlWriter(doc, CRONTABPATH);
        ScheduleManager.getInstance().deleteOne(id);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject jsonObject) {
        load(jsonObject);
        task = (Element) root.selectSingleNode(xpath);
        if (null == task) {
            log.error("没有找到指定的任务");
            resultDto = ResultDtoTool.buildError("没有找到指定的任务");
            return resultDto;
        }
//		修改前先删除定时任务
        ScheduleManager.getInstance().deleteOne(id);
//		添加修改后的任务
        Map<String, String> p = new HashMap<>();
        String[] ps = params.split(",");
        for (String param : ps) {
            int idx = param.indexOf('=');
            String key = param.substring(0, idx);
            String value = param.substring(idx + 1);
            p.put(key, value);
        }
        Schedule sc = new Schedule(id, mission, nodeName, 0, description, timeExp, p);
        if (!(ScheduleManager.getInstance().addOne(id, sc))) {
            resultDto = ResultDtoTool.buildError("修改任务失败，存在重复ID的任务");
            return resultDto;
        }
//		修改配置文件信息
        if (null != description) {
            Attribute attr = task.attribute(F_DESCRIPTION);
            attr.setValue(description);
        }
        if (null != status && !"".equals(status)) {
            Attribute attr = task.attribute(F_STATUS);
            attr.setValue(status);
        }
        if (null != nodeName && !"".equals(nodeName)) {
            Attribute attr = task.attribute(NODE_NAME);
            attr.setValue(nodeName);
        }
        if (null != count && !"".equals(count)) {
            Attribute attr = task.attribute(F_COUNT);
            attr.setValue(count);
        }
        if (null != taskName && !"".equals(taskName)) {
            Attribute attr = task.attribute(TASK_NAME);
            attr.setValue(taskName);
        }
        if (null != timeExp) {
            Element timeExpNode = task.element(TIME_EXP);
            timeExpNode.setText(timeExp);
        }
        if (null != mission) {
            Element missionNode = task.element(F_MISSION);
            missionNode.setText(mission);
        }
        if (null != params) {
            Element paramsNode = task.element(F_PARAMS);
            paramsNode.setText(params);
        }
        str = XMLDealTool.xmlWriter(doc, CRONTABPATH);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByID(JSONObject jsonObject) {
        load(jsonObject);
        task = (Element) root.selectSingleNode(xpath);
        if (null == task) {
            log.error("没有找到指定的任务");
            resultDto = ResultDtoTool.buildError("没有找到指定的任务");
            return resultDto;
        }
        str = XMLtoJSON.getJSONFromXMLEle(task);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> start(JSONObject jsonObject) {
        load(jsonObject);
        task = (Element) root.selectSingleNode(xpath);
        if (null == task) {
            log.error("没有找到指定的任务");
            resultDto = ResultDtoTool.buildError("没有找到指定的任务");
            return resultDto;
        }
        Attribute attr = task.attribute(F_STATUS);
        attr.setValue("1");
        str = XMLDealTool.xmlWriter(doc, CRONTABPATH);
        start(id);
        return ResultDtoTool.buildSucceed(str);
    }


    public ResultDto<String> stop(JSONObject jsonObject) {
        load(jsonObject);
        task = (Element) root.selectSingleNode(xpath);
        if (null == task) {
            log.error("没有找到指定的任务");
            resultDto = ResultDtoTool.buildError("没有找到指定的任务");
            return resultDto;
        }
        Attribute attr = task.attribute(F_STATUS);
        attr.setValue("0");
        str = XMLDealTool.xmlWriter(doc, CRONTABPATH);
        stop(id);
        return ResultDtoTool.buildSucceed(str);
    }

    private void stop(String id) {
        ScheduleManager.getInstance().stopOne(id);
    }

    private void start(String id) {
        ScheduleManager.getInstance().startOne(id);
    }

    public String getVersion() {
        return root.attributeValue("timestamp", null);
    }
}
