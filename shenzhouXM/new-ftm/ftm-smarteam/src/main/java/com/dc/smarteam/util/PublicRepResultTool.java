package com.dc.smarteam.util;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PublicRepResultTool {
    /**
     * 处理返回结果，统一处理成json
     **/
    public static Object sendResult(String code, String msg, Object obj) {
        PublicResponseBean pBean = new PublicResponseBean();
        try {

            pBean.setCode(code);
            pBean.setMessage(msg);
            pBean.setData(obj);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("======return result error :=====" + null == obj ? "service result is null" :  obj);
        }
        return pBean;
    }
}
