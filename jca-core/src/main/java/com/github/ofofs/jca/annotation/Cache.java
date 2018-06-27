package com.github.ofofs.jca.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解
 *
 * @author kangyonggan
 * @since 2018/6/26 0026
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Cache {
    /**
     * 缓存的键
     *
     * @return 返回缓存的键
     */
    String value();

    /**
     * 缓存失效时间, 单位毫秒
     *
     * @return 返回缓存失效时间，默认30分钟
     */
    long expire() default -1;
}
