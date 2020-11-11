package com.dc.smarteam.util;

import org.apache.poi.ss.usermodel.Row;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

/**
 * Created by hudja on 2017/7/27.
 */
public class ImportsHelpUtil
{

    /**
     * 获取有效excle数据
     * @param row
     * @param cellnum
     * @return
     */
    public static String getexcleString(Row row, int cellnum){
        if(row.getCell(cellnum)==null){
            return "";
        }
        row.getCell(cellnum).setCellType(CELL_TYPE_STRING);
        return row.getCell(cellnum).getStringCellValue();
    }

    /**
     * 正则匹配有效字段
     * @param data
     * @param rex
     * @return
     */
    public static boolean rexMatch(String data, String rex) {
        Pattern pattern = Pattern.compile(rex);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();

    }
    /**
     * 截取有效字段
     * @param str
     * @param flagbegin
     * @param flagend
     * @return
     */
    public static String getstrchat(String str, String flagbegin, String flagend) {
        if (!str.contains(flagbegin) || !str.contains(flagend)) {
            return str;
        }
        int begindex = str.indexOf(flagbegin);
        int endindex = str.indexOf(flagend);

        return str.substring(begindex + 1, endindex);
    }
    public static void main(String[]args){
        System.out.print(rexMatch("123456","[^\\u4e00-\\u9fa5]{6,20}"));
    }
}
