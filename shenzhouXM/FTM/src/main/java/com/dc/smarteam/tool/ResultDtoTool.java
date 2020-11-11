package com.dc.smarteam.tool;

import com.dc.smarteam.common.json.ResultDto;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2019/12/3.
 */
public class ResultDtoTool {
    private ResultDtoTool() {
    }

    public static final String SUCCESS_CODE = "0000";

    public static <T> ResultDto<T> fromJson(String json, Class<T> tClass) {
        ResultDto<T> resultDto = new ResultDto<>();
        JsonObject jsonObj = GsonUtil.toJsonObj(json);
        resultDto.setCode(jsonObj.has("code") ? jsonObj.get("code").getAsString() : null);
        resultDto.setMessage(jsonObj.has("message") ? jsonObj.get("message").getAsString() : null);
        if (jsonObj.has("data")) {
            String dataStr = jsonObj.get("data").toString();
            if (StringUtils.isNotEmpty(dataStr)) {
                T t = GsonUtil.fromJson(dataStr, tClass);
                resultDto.setData(t);
            }
        }
        return resultDto;
    }


    public static <T> String toJson(ResultDto<T> resultDto) {
        return GsonUtil.toJson(resultDto);
    }

    public static <T> ResultDto<T> buildSucceed(T data) {
        ResultDto<T> result = new ResultDto<>();
        result.setCode(SUCCESS_CODE);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> ResultDto<T> buildSucceed(String message, T data) {
        ResultDto<T> result = new ResultDto<>();
        result.setCode(SUCCESS_CODE);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> ResultDto<T> buildError(String code, String message) {
        ResultDto<T> resultDto = new ResultDto<>();
        resultDto.setCode(code);
        resultDto.setMessage(message);
        return resultDto;
    }

    public static <T> ResultDto<T> buildError(String message) {
        return buildError("9999", message);
    }

    public static boolean isSuccess(ResultDto dto) {
        return SUCCESS_CODE.equals(dto.getCode());
    }
}
