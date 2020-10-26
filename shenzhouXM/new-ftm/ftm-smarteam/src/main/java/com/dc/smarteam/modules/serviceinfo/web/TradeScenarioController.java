package com.dc.smarteam.modules.serviceinfo.web;


import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.cfgmodel.ServiceModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.TradeScenario;
import com.dc.smarteam.modules.serviceinfo.service.TradeScenarioService;
import com.dc.smarteam.service.impl.RouteServiceImpl;
import com.dc.smarteam.service.impl.ServiceInfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liwjx on 2017/9/6.
 */
@Slf4j
@RestController
@RequestMapping(value = "${adminPath}/monitor/TradeScenario")
public class TradeScenarioController{

    @Resource
    private ServiceInfoServiceImpl serviceInfoService;
    @Autowired
    @Qualifier("TradeScenarioServiceImpl")
    private TradeScenarioService tradeScenarioService;
    @Autowired
    private RouteServiceImpl routeService;


    /**
     * 一个"发送方,接受方,交易码"定义一个交易场景
     *
     * @return 交易场景集合
     */
    private List<TradeScenario> getScenarioList() {//NOSONAR
        ResultDto<List<ServiceModel.Service>> resultDto = serviceInfoService.listAll();
        List<ServiceModel.Service> nodelist = resultDto.getData();
        List<TradeScenario> tradeScenarioList = new ArrayList<>();
        for (ServiceModel.Service service : nodelist) {

            ServiceModel.GetAuth getAuth = service.getGetAuth();
            ServiceModel.PutAuth putAuth = service.getPutAuth();
            if (null != getAuth && null != getAuth.getUsers()) {
                for (ServiceModel.AuthUser getuser : getAuth.getUsers()) {
                    if (getuser == null) continue;
                    if (null != putAuth && null != putAuth.getUsers()) {
                        for (ServiceModel.AuthUser putuser : putAuth.getUsers()) {
                            if (putuser == null) continue;
                            TradeScenario trade = new TradeScenario();
                            trade.setTranCode(service.getTrancode());
                            trade.setToUid(getuser.getUser());
                            trade.setFromUid(putuser.getUser());
                            trade.setGroups(trade.getFromUid() + "," + trade.getToUid());
                            tradeScenarioList.add(trade);
                        }
                    }
                }
            }
        }
        return tradeScenarioList;
    }

    /**
     * 统计“发送发+接受方”各个交易码的失败笔数
     * 包含 biz_file_upload_log、biz_file_msg2client_log、biz_file_download_log 中的失败记录
     *
     * @return 各“发送发+接受方”失败交易笔数集合
     */
    private List<TradeScenario> getScenariosByFailList(TradeScenario tradeScenario) {

        // 统计推送失败和下载失败的总笔数
        List<TradeScenario> scenariosByPushAndDownFail = tradeScenarioService.findListFail(tradeScenario);

        // 统计上传失败的笔数。FtpErrCode.AUTH_TRAN_CODE_FAILED 无法判断属于哪个场景，需筛掉；其他的需根据路由规则找到相应的接收方
        List<TradeScenario> scenariosByUploadFail = tradeScenarioService.findUploadFail(tradeScenario);
        RouteModel routeModel = routeService.loadModel();
        List<TradeScenario> scenariosByUploadFailAll = new ArrayList<>();
        for (TradeScenario tradeScenarioUpload : scenariosByUploadFail) {
            RouteModel.Route route = routeService.selByTranscodeAndUser(tradeScenarioUpload.getTranCode(), tradeScenarioUpload.getFromUid(), routeModel);
            List<String> routeNameList = new ArrayList<>();
            if (route != null) {
                FtRoute ftRoute = new FtRoute();
                CfgModelConverter.convertTo(route, ftRoute);
                String destination = ftRoute.getDestination();
                String[] split = destination.split(",");
                routeNameList.addAll(Arrays.asList(split));
            }
            for (String routeName : routeNameList) {
                TradeScenario tradeScenarioUploadAll = new TradeScenario();
                tradeScenarioUploadAll.setFromUid(tradeScenarioUpload.getFromUid());
                tradeScenarioUploadAll.setToUid(routeName);
                tradeScenarioUploadAll.setTranCode(tradeScenarioUpload.getTranCode());
                tradeScenarioUploadAll.setTradeScenarioFailed(tradeScenarioUpload.getTradeScenarioFailed());
                scenariosByUploadFailAll.add(tradeScenarioUploadAll);
            }
        }

        // 合并 scenariosByUploadFailAll scenariosByPushAndDownFail
        List<TradeScenario> scenariosByUploadFailDel = new ArrayList<>();
        for (TradeScenario tradeScenarioPushAndDown : scenariosByPushAndDownFail) {
            for (TradeScenario tradeScenarioUpload : scenariosByUploadFailAll) {
                if (StringUtils.equals(tradeScenarioPushAndDown.getTranCode(), tradeScenarioUpload.getTranCode())
                        && StringUtils.equals(tradeScenarioPushAndDown.getFromUid(), tradeScenarioUpload.getFromUid())
                        && StringUtils.equals(tradeScenarioPushAndDown.getToUid(), tradeScenarioUpload.getToUid())) {
                    // 当scenariosByUploadFailAll中的数据在scenariosByPushAndDownFail中已存在，则累计
                    tradeScenarioPushAndDown.setTradeScenarioFailed(tradeScenarioPushAndDown.getTradeScenarioFailed() + tradeScenarioUpload.getTradeScenarioFailed());
                    scenariosByUploadFailDel.add(tradeScenarioUpload);
                    break;
                }
            }
        }
        scenariosByUploadFailAll.removeAll(scenariosByUploadFailDel);
        // 当scenariosByUploadFailAll中的数据在scenariosByPushAndDownFail中不存在，则追加
        scenariosByPushAndDownFail.addAll(scenariosByUploadFailAll);

        return scenariosByPushAndDownFail;
    }

    /*@RequiresPermissions("NodeMonitor:tradeScenario:view")*/
    @GetMapping(value = "/trade")
    public ResultDto<Map<String, String>> tradeScenariolist(TradeScenario tradeScenario, HttpServletRequest request, HttpServletResponse response) {//NOSONAR

        // 根据时间过滤，默认查当天数据
        if (null == tradeScenario.getStartTime() || null == tradeScenario.getEndTime()) {
            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            tradeScenario.setStartTime(currentDate.getTime());

            Calendar nextDate = Calendar.getInstance();
            nextDate.setTime(new Date());
            nextDate.add(Calendar.DAY_OF_MONTH, +1);
            tradeScenario.setEndTime(nextDate.getTime());
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("endTime", fmt.format(tradeScenario.getEndTime()));
        resultMap.put("startTime", fmt.format(tradeScenario.getStartTime()));

        // 根据service_info.xml,计算各个“发送发+接受方”分别用了多少交易码
        List<TradeScenario> tradeScenarioList = this.getScenarioList();
        Map<String, String> codeMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();
        for (TradeScenario ts : tradeScenarioList) {
            countMap.put(ts.getGroups(), 0);//初始化
        }
        for (TradeScenario ts : tradeScenarioList) {
            String codeList = codeMap.get(ts.getGroups());
            if (!StringUtils.contains(codeList, ts.getTranCode())) {
                int integer = countMap.get(ts.getGroups());
                integer++;
                countMap.put(ts.getGroups(), integer);
            }
            if (codeList == null) {
                codeMap.put(ts.getGroups(), ts.getTranCode());
            } else {
                codeMap.put(ts.getGroups(), codeList + "<br>" + ts.getTranCode());
            }
        }

        // 根据流水表，获取“发送发+接受方”各个交易码的成功笔数
        List<TradeScenario> scenarioBySucc = tradeScenarioService.findListSuss(tradeScenario);

        // 根据流水表，获取“发送发+接受方”各个交易码的失败笔数
        List<TradeScenario> scenarioByFail = this.getScenariosByFailList(tradeScenario);

        List<TradeScenario> resultList = new ArrayList<>();
        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            if (entry.getKey() != null) {

                TradeScenario traderes = new TradeScenario();
                String[] fromuidAndtouid = entry.getKey().split(",");
                traderes.setFromUid(fromuidAndtouid[0]);
                traderes.setToUid(fromuidAndtouid[1]);
                traderes.setTradeScenarioTotal(countMap.get(entry.getKey()));
                traderes.setTranCode(entry.getValue());

                //成功交易笔数信息
                StringBuilder succMsg = new StringBuilder();
                for (TradeScenario tradeScenarioBySucc : scenarioBySucc) {
                    if (StringUtils.equals(tradeScenarioBySucc.getFromUid(), fromuidAndtouid[0])
                            && StringUtils.equals(tradeScenarioBySucc.getToUid(), fromuidAndtouid[1])
                            && StringUtils.contains(entry.getValue(), tradeScenarioBySucc.getTranCode())) {
                        succMsg.append(tradeScenarioBySucc.getTranCode()).append(" 笔数：").append(tradeScenarioBySucc.getTradeScenarioPass()).append("<br>");
                    }
                }
                traderes.setTradeScenarioPassMess(succMsg.toString());

                //失败交易笔数信息
                StringBuilder failMsg = new StringBuilder();
                for (TradeScenario tradeScenarioByFail : scenarioByFail) {
                    if (StringUtils.equals(tradeScenarioByFail.getFromUid(), fromuidAndtouid[0])
                            && StringUtils.equals(tradeScenarioByFail.getToUid(), fromuidAndtouid[1])
                            && StringUtils.contains(entry.getValue(), tradeScenarioByFail.getTranCode())) {
                        failMsg.append(tradeScenarioByFail.getTranCode()).append(" 笔数：").append(tradeScenarioByFail.getTradeScenarioFailed()).append("<br>");
                    }
                }
                traderes.setTradeScenarioFailedMess(failMsg.toString());

                // 根据发送发、接受方过滤
                if ((null == tradeScenario.getFromUid() || StringUtils.containsIgnoreCase(traderes.getFromUid(), tradeScenario.getFromUid())) &&
                        (null == tradeScenario.getToUid() || StringUtils.containsIgnoreCase(traderes.getToUid(), tradeScenario.getToUid()))) {
                    resultList.add(traderes);
                }

            }
        }

        PageHelper.getInstance().getPage(tradeScenario.getClass(), request, response, resultMap, resultList);
        return ResultDtoTool.buildSucceed("success", resultMap);
    }

}
