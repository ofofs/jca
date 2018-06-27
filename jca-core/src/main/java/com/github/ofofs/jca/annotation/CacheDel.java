package com.github.ofofs.jca.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除缓存的注解
 *
 * @author kangyonggan
 * @since 6/27/18
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface CacheDel {

    /**
     * 缓存的键
     *
     * @return 返回缓存的键
     */
    String value();

}
