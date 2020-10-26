package com.dc.smarteam.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xuchuang on 2018/5/28.
 */
public class CollectionUtil {

    /**
     * 连接列表中的元素
     * @param list
     * @param c
     * @return
     */
    public static String join(List<String> list, String c){
        StringBuffer sb = new StringBuffer();
        if(list==null) return null;
        int size = list.size();
        if(size<1) return "";
        if(size==1) return list.get(0);
        for(int i=0;i<size-1;i++){
            sb.append(list.get(i));
            sb.append(c);
        }
        sb.append(list.get(size-1));
        return sb.toString();
    }

    /**
     *
     * @param list
     * @param methodName
     * @param <T>
     * @param <U>
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T,U> List<U> freeTurnList(List<T> list, String... methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(methodName==null||methodName.length<1){
            return null;
        }
        List<U> resultList = new ArrayList<>();
        for(T e:list){
            Method m ;
            Object obj = e;
            for(String method:methodName){
                m = obj.getClass().getDeclaredMethod(method);
                obj = m.invoke(obj);
            }
            if(obj!=null){
                resultList.add((U)obj);
            }
        }
        return resultList;

    }

    public static String removeOne(String src,String dest,String c){
        if(dest==null||"".equals(dest)) return src;
        String[] sp = src.split(c);
        if(sp==null||sp.length<1) return src;
        int size = sp.length;
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<size-1;i++){
            if(!sp[i].equals(dest)){
                sb.append(sp[i]);
                sb.append(c);
            }
        }
        if(sp[size-1].equals(dest)){
            if(sb.length()<1){
                return "";
            }
            return sb.substring(0,sb.length()-c.length());
        }
        return sb.append(sp[size-1]).toString();

    }

    public static String addOne(String src, String dest, String c){
        if(dest==null||"".equals(dest)){
            return src;
        }
        String[] sp = (src==null || "".equals(src))?null:src.split(c);
        if(sp==null){
            return dest;
        }
        for(String s:sp){
            if(s.equals(dest)){
                return src;
            }
        }
        return src+(c+dest);

    }

    public static String toString(List<String> list, String splitChar){
        String[] str = list.toArray(new String[0]);
        StringBuffer sb = new StringBuffer();
        for(String s:str){
            sb.append(s);
            sb.append(splitChar);
        }
        if(sb.length()>=splitChar.length()){
            return sb.substring(0,sb.length()-splitChar.length());
        }
        return null;
    }


}
