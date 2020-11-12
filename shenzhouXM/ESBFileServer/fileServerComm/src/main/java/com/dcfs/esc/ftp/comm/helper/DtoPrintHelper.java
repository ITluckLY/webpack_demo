package com.dcfs.esc.ftp.comm.helper;

import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.comm.dto.BaseBusiDto;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ExceptionDto;

/**
 * Created by mocg on 2017/6/4.
 */
public class DtoPrintHelper {
    public static boolean printDto = false;//NOSONAR
    public static boolean printDtoJson = false;//NOSONAR

    public static void print(BaseDto dto) {
        if (!printDto) return;
        if (printDtoJson) {
            println("dtoJson::" + GsonUtil.toJson(dto));
        }
        if (dto instanceof BaseBusiDto) print((BaseBusiDto) dto);
        else if (dto instanceof ExceptionDto) print((ExceptionDto) dto);
        else {
            println(String.valueOf(dto.getNano()));
        }
    }

    public static void print(BaseBusiDto dto) {
        println(dto.getErrCode());
        println(dto.getErrMsg());
        println(String.valueOf(dto.getPieceNum()));
    }

    public static void print(ExceptionDto dto) {
        println(dto.getErrCode());
        println(dto.getErrMsg());
    }

    private static void println(String x) {
        System.out.println(x);//NOSONAR
    }

    private DtoPrintHelper() {
    }
}
