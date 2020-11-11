package com.dc.smarteam.aspectCfg.cfgOperate;

import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.modules.client.entity.FtOperationLog;
import com.dc.smarteam.modules.client.service.FtOperationLogService;

import org.apache.poi.ss.formula.functions.T;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by xuchuang on 2018/5/25.
 */
@Component
@Aspect
public class CfgOpreationCommon {

    private Logger log = LoggerFactory.getLogger(CfgOpreationCommon.class);

    @Resource
    private FtOperationLogService ftOperationLogService;

    //配置修改
    @Pointcut("execution(* com.dc.smarteam.service.*Service.update(*)) || @annotation(com.dc.smarteam.aspectCfg.base.UpdateEntity)")
    public void update_point(){}

    //配置添加
    @Pointcut("execution(* com.dc.smarteam.service.*Service.add(*)) && !@args(com.dc.smarteam.aspectCfg.base.Exclude)")
    public void add_point(){}

    //配置删除
    @Pointcut("execution(* com.dc.smarteam.service.*Service.del(*)) && !@args(com.dc.smarteam.aspectCfg.base.Exclude)")
    public void del_point(){}


    @Around("update_point()")
    public Object update(ProceedingJoinPoint jp) throws Throwable{
        log.info("update at {}",jp.getTarget().getClass().getName());
        CfgData cfgData;
        ICfgService service;
        boolean flag = false;
        Object result = null;
        try{
            cfgData = (CfgData)jp.getArgs()[0];
            service = (ICfgService) jp.getTarget();
            String pre = service.getEntityXml(cfgData,false);
            if(pre==null){
                log.error("{} 无前置对象,无法更新",cfgData.toString());
                return jp.proceed();
            }
            log.info("before update:{}",pre);
            result = jp.proceed();//通知
            flag = true;
            String curr = service.getEntityXml(cfgData,false);

           /*
            此处后期再改
            String newCfgFileName ="";
            if(service.getCfgFileName().equals("FilesProcesFlow")){
                 newCfgFileName =service.getCfgPsFileName();
            }else{
                newCfgFileName =service.getCfgFileName();

            }*/


            log.info("after update:{}",curr);
            if(!pre.equals(curr)){
                FtOperationLog ftOperationLog = new FtOperationLog();
                ftOperationLog.setSystem(service.getSystem());
                ftOperationLog.setBeforeModifyValue(pre);
                ftOperationLog.setAfterModifyValue(curr);
                ftOperationLog.setNodeType(service.getNodeType());
                ftOperationLog.setCfgFileName(service.getCfgFileName());
                ftOperationLog.setParamName(cfgData.getParamName());
                ftOperationLogService.updateOperation(ftOperationLog,cfgData.getParamName(),service.getCfgFileName());
            }else {
                log.warn("无修改项，不记录操作流水");
            }
        }catch (Exception e){
            log.error("配置更新,流水记录异常:{}",e.getMessage());
            if(!flag)
                return jp.proceed();
        }
        return result;

    }


    @AfterReturning(pointcut="add_point()",returning = "rvt")
    public void addOperation(JoinPoint jp, ResultDto<String> rvt){
        log.info("add at {}",jp.getTarget().getClass().getName());
        if("9999".equals(rvt.getCode())){//新增失败则不记录日志
            log.error("cfg新增失败");
            return;
        }
        CfgData cfgData;
        ICfgService service;
        try{
            cfgData = (CfgData)jp.getArgs()[0];
            service = (ICfgService) jp.getTarget();
            String curr = service.getEntityXml(cfgData,true);
            log.info("after add:{}",curr);
            FtOperationLog ftOperationLog = new FtOperationLog();
            ftOperationLog.setSystem(service.getSystem());
            ftOperationLog.setBeforeModifyValue("");
            ftOperationLog.setAfterModifyValue(curr);
            ftOperationLog.setNodeType(service.getNodeType());
            ftOperationLog.setCfgFileName(service.getCfgFileName());
            ftOperationLog.setParamName(cfgData.getParamName());
            ftOperationLogService.addOperation(ftOperationLog,cfgData.getParamName(),service.getCfgFileName());
        }catch (Exception e){
            log.error("配置新增,流水记录异常:{}",e.getMessage());
            return;
        }
    }

    @Around("del_point()")
    public Object delOperation(ProceedingJoinPoint jp) throws Throwable{
        log.info("del at {}",jp.getTarget().getClass().getName());
        CfgData cfgData;
        ICfgService service;
        Object result = null;
        boolean flag = false;
        try{
            cfgData = (CfgData)jp.getArgs()[0];
            service = (ICfgService) jp.getTarget();
            String pre = service.getEntityXml(cfgData,false);
            if(pre==null){
                log.error("{} 无前置对象,无法删除",cfgData.toString());
                return jp.proceed();
            }
            log.info("before del:{}",pre);
            result = jp.proceed();//通知删除
            flag = true;
            if(!((ResultDto<T>)result).getCode().equals(ResultDtoTool.SUCCESS_CODE)){
                log.error("cfg节点删除失败,不记录cfg变动流水日志");
                return result;
            }
            FtOperationLog ftOperationLog = new FtOperationLog();
            ftOperationLog.setSystem(service.getSystem());
            ftOperationLog.setBeforeModifyValue(pre);
            ftOperationLog.setAfterModifyValue("");
            ftOperationLog.setNodeType(service.getNodeType());
            ftOperationLog.setCfgFileName(service.getCfgFileName());
            ftOperationLog.setParamName(cfgData.getParamName());
            ftOperationLogService.delOperation(ftOperationLog,cfgData.getParamName(),service.getCfgFileName());

        }catch (Exception e){
            log.error("配置删除,流水记录异常:{}",e.getMessage());
            if(!flag)
                return jp.proceed();
        }
        return result;

    }

}
