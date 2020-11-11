package com.dc.smarteam.aspectCfg.base;

import java.lang.annotation.*;

/**
 * Created by xuchuang on 2018/5/28.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
//@Component
public @interface UpdateEntity {
    String value() default "";
}
