package com.dc.smarteam.aspectCfg.base;

import java.lang.annotation.*;

/**
 * Created by xuchuang on 2018/5/30.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
/**
 * 切面例外类注解
 */
public @interface Exclude {
}
