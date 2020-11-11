/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.common.utils;

import com.dc.smarteam.modules.client.entity.ClientRule;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 *
 * @author ThinkGem
 * @version 2013-05-22
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {//NOSONAR
    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
    private static final char SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 转换为字节数组
     *
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;//NOSONAR
            }
        } else {
            return null;//NOSONAR
        }
    }

    /**
     * 转换为字节数组
     *
     * @return
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs) {
        if (str != null) {
            for (String s : strs) {
                if (str.equals(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (isBlank(html)) {
            return "";
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        return m.replaceAll("");
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     *
     * @param html
     * @return
     */
    public static String replaceMobileHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     *
     * @param txt
     * @return
     */
    public static String toHtml(String txt) {
        if (txt == null) {
            return "";
        }
        return replace(replace(Encodes.escapeHtml(txt), "\n", "<br/>"), "\t", "&nbsp; &nbsp; ");
    }

    /**
     * 缩略字符串（不区分中英文字符）
     *
     * @param str    目标字符串
     * @param length 截取长度
     * @return
     */
    public static String abbr(String str, int length) {
        if (str == null) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
                currentLength += String.valueOf(c).getBytes("GBK").length;
                if (currentLength <= length - 3) {
                    sb.append(c);
                } else {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        return "";
    }

    public static String abbr2(String param, int length) {
        if (param == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int n = 0;
        char temp;
        boolean isCode = false; // 是不是HTML代码
        boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;//NOSONAR
        for (int i = 0; i < param.length(); i++) {
            temp = param.charAt(i);
            if (temp == '<') {
                isCode = true;
            } else if (temp == '&') {
                isHTML = true;
            } else if (temp == '>' && isCode) {
                n = n - 1;
                isCode = false;
            } else if (temp == ';' && isHTML) {
                isHTML = false;
            }
            try {
                if (!isCode && !isHTML) {
                    n += String.valueOf(temp).getBytes("GBK").length;
                }
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }

            if (n <= length - 3) {
                result.append(temp);
            } else {
                result.append("...");
                break;
            }
        }
        // 取出截取字符串中的HTML标记
        String tempResult = result.toString().replaceAll("(>)[^<>]*(<?)",
                "$1$2");
        // 去掉不需要结素标记的HTML标记
        tempResult = tempResult
                .replaceAll(
                        "</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
                        "");
        // 去掉成对的HTML标记
        tempResult = tempResult.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>",
                "$2");
        // 用正则表达式取出标记
        Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
        Matcher m = p.matcher(tempResult);
        List<String> endHTML = Lists.newArrayList();
        while (m.find()) {
            endHTML.add(m.group(1));
        }
        // 补全不成对的HTML标记
        for (int i = endHTML.size() - 1; i >= 0; i--) {
            result.append("</");
            result.append(endHTML.get(i));
            result.append(">");
        }
        return result.toString();
    }

    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }

    /**
     * 获得i18n字符串
     */
    public static String getMessage(String code, Object[] args) {
        LocaleResolver localLocaleResolver = SpringContextHolder.getBean(LocaleResolver.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Locale localLocale = localLocaleResolver.resolveLocale(request);
        return SpringContextHolder.getApplicationContext().getMessage(code, args, localLocale);
    }

    /**
     * 获得用户远程地址
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        final String unknown = "unknown";
        String remoteAddr = request.getHeader("x-forwarded-for");
        if (isNotBlank(remoteAddr) && remoteAddr.contains(",")) {
            remoteAddr = remoteAddr.split(",")[0];
        }
        if (isBlank(remoteAddr) || unknown.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getHeader("Proxy-Client-IP");
        }
        if (isBlank(remoteAddr) || unknown.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlank(remoteAddr) || unknown.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isBlank(remoteAddr) || unknown.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isBlank(remoteAddr) || unknown.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getHeader("X-Real-IP");
        }
        if (isBlank(remoteAddr) || unknown.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase("hello_world") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase("hello_world") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase("hello_world") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     *
     * @param objectString 对象串
     *                     例如：row.user.id
     *                     返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
     */
    public static String jsGetVal(String objectString) {
        StringBuilder result = new StringBuilder();
        StringBuilder val = new StringBuilder();
        String[] vals = split(objectString, ".");
        for (int i = 0; i < vals.length; i++) {
            val.append("." + vals[i]);
            result.append("!" + (val.substring(1)) + "?'':");
        }
        result.append(val.substring(1));
        return result.toString();
    }

    public static Map toParamMap(String text){
        Map<String,String> paramMap = new LinkedHashMap<>();
        String[] lines = text.split("\n");
        for(String line:lines){
            int i = line.indexOf("=");
            if(i > 0 && !line.startsWith("#")){
                String paramName = line.substring(0,i);
                String paramValue = line.substring(i+1);
                paramMap.put(paramName,paramValue);
            }
        }
        return paramMap;
    }

    public static String changeContext(String old,Map<String,String> map){
        StringBuffer bf = new StringBuffer();
        String[] params = old.split("\n");
        List<String> lines = new ArrayList<>();
        Set<String> keys = map.keySet();
        for(int i = 0 ; i < params.length; i++){
            for (String key : keys){
                if(key.equals(params[i].split("=")[0])){
                    params[i] = key + "=" + map.get(key);
                    break;
                }
            }
            lines.add(params[i]);
        }
        for(String p : lines){
            bf.append(p).append("\n");
        }
        return bf.toString();
    }

    public static List<ClientRule> readStringXml(String xml) {
        List<ClientRule> clientRules = new ArrayList<>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            List<Element> rules = doc.selectNodes("/rules/rule");
            long id = 1;
            for(Element e : rules){
                ClientRule clientRule = new ClientRule();
                clientRule.setId(id++);
                clientRule.setUser(e.attributeValue("user")); // 拿到head节点下的子节点title值
                clientRule.setTranCode(e.attributeValue("tran_code"));
                clientRule.setType(e.attributeValue("type"));
                clientRule.setMode(e.attributeValue("mode"));
                clientRule.setDestination(e.attributeValue("destination"));
                clientRules.add(clientRule);
            }
        } catch (DocumentException e) {
            log.error("解析内容失败",e);
        } catch (Exception e) {
            log.error("解析内容失败",e);
        }
        return clientRules;
    }

    public static String upDateXMLStr(List<ClientRule> clientRules,String old) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(old); // 将字符串转为XML
            List<Element> rules = doc.selectNodes("/rules/rule");
            for(int i=0;i< rules.size();i++){
                rules.get(i).attribute("user").setValue(clientRules.get(i).getUser());
                rules.get(i).attribute("tran_code").setValue(clientRules.get(i).getTranCode());
                rules.get(i).attribute("type").setValue(clientRules.get(i).getType());
                rules.get(i).attribute("mode").setValue(clientRules.get(i).getMode());
                rules.get(i).attribute("destination").setValue(clientRules.get(i).getDestination());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return doc.asXML();
    }

}
