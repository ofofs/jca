package com.github.ofofs.jca.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kangyonggan
 * @since 6/23/18
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Handler {

    /**
     * 注解是否可用
     *
     * @return 默认注解可用
     */
    boolean enable() default true;

    /**
     * 类型
     *
     * @return 类型枚举
     */
    Type type();

    enum Type {
        /**
         * 日志
         */
        LOG,
        /**
         * 缓存
         */
        CACHE
    }

}
