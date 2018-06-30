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
     * 缓存key的前缀，仅用于@Cache和@CacheDel
     *
     * @return 返回前缀
     */
    String prefix() default "";

    /**
     * 缓存失效时间, 单位毫秒
     *
     * @return 返回缓存失效时间，默认30分钟
     */
    long expire() default -1;

    /**
     * 类型
     *
     * @return 类型枚举
     */
    Type value();

    enum Type {
        /**
         * 日志
         */
        LOG,
        /**
         * 缓存
         */
        CACHE,
        /**
         * 序列化
         */
        SERIAL,
        /**
         * 工具类
         */
        UTIL,
        /**
         * 限制调用次数
         */
        COUNT,
        /**
         * 限制调用间隔时间
         */
        FREQUENCY,
        /**
         * 生成getter
         */
        GETTER
    }

}
