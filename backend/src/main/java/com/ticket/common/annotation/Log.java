package com.ticket.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Ticket System
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块
     */
    String module() default "";

    /**
     * 操作内容
     */
    String content() default "";
}
