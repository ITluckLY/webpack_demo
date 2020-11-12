package com.dcfs.esb.ftp.utils;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by cgmo on 2015/10/10.
 */
public class BeanConverter {
    private static final Logger log = LoggerFactory.getLogger(BeanConverter.class);

    private BeanConverter() {
    }

    private static ModelMapper modelMapper = null;
    private static ModelMapper modelMapperStrict = null;

    public static ModelMapper getIncetance() {
        if (modelMapper == null) {
            modelMapper = new ModelMapper();
        }
        return modelMapper;
    }

    public static ModelMapper getStrictIncetance() {
        if (modelMapperStrict == null) {
            modelMapperStrict = new ModelMapper();
            //使用严格匹配模式
            modelMapperStrict.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        }
        return modelMapperStrict;
    }

    public static <D> D convertTo(Object source, Class<D> destinationType) {
        if (source == null) return null;
        return getIncetance().map(source, destinationType);
    }

    /**
     * 严格模式匹配
     *
     * @param source
     * @param destinationType
     * @param <D>
     * @return
     */
    public static <D> D strictConvertTo(Object source, Class<D> destinationType) {
        if (source == null) return null;
        return getStrictIncetance().map(source, destinationType);
    }

    public static <D> D strictConvertTo(Object source, Class<D> destinationType, List<DiffMethod> diffMethodList) {
        if (source == null) return null;
        D dest = getStrictIncetance().map(source, destinationType);
        if (diffMethodList != null && dest != null && dest.getClass() == destinationType) {
            if (!(dest instanceof Iterable) && !(dest instanceof Map) && !(destinationType.isArray())) {//要求单个对象//NOSONAR
                for (DiffMethod diffMethod : diffMethodList) {
                    try {
                        Object obj = MethodUtils.invokeExactMethod(source, diffMethod.getSourceMethodName());
                        MethodUtils.invokeExactMethod(dest, diffMethod.getDestMethodName(), obj);
                    } catch (Exception e) {
                        log.error("对象转换出错", e);
                    }
                }
            }
        }
        return dest;
    }

    public static <D> List<D> strictConvertTo(List sourceList, Class<D> destinationType, List<D> targetList) {
        for (Object o : sourceList) {
            targetList.add(strictConvertTo(o, destinationType));
        }
        return targetList;
    }

    public static <D> List<D> strictConvertTo(List sourceList, Class<D> destinationType, List<D> targetList, List<DiffMethod> diffMethodList) {
        for (Object o : sourceList) {
            targetList.add(strictConvertTo(o, destinationType, diffMethodList));
        }
        return targetList;
    }

    public static <T> T copyProperties(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <T> T copyProperties(Object source, T target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    public static <T> List<T> copyProperties(List sourceList, Class<T> destinationType, List<T> targetList) throws IllegalAccessException, InstantiationException {
        for (Object o : sourceList) {
            targetList.add(copyProperties(o, destinationType.newInstance()));
        }
        return targetList;
    }

    public static class DiffMethod {
        private String sourceMethodName;
        private String destMethodName;

        public DiffMethod(String sourceMethodName, String destMethodName) {
            this.sourceMethodName = sourceMethodName;
            this.destMethodName = destMethodName;
        }

        public String getSourceMethodName() {
            return sourceMethodName;
        }

        public void setSourceMethodName(String sourceMethodName) {
            this.sourceMethodName = sourceMethodName;
        }

        public String getDestMethodName() {
            return destMethodName;
        }

        public void setDestMethodName(String destMethodName) {
            this.destMethodName = destMethodName;
        }
    }
}
