package com.github.ofofs.jca.annotation;

import com.github.ofofs.jca.constants.CoreConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限制一定时间内方法的调用次数。
 *
 * @author kangyonggan
 * @since 6/27/18
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Count {

    /**
     * 方法的键
     *
     * @return 返回方法的键
     */
    String value() default CoreConstants.EMPTY;

    /**
     * 方法的键（和value二者至少提供一个，如果都提供，value更优先）
     *
     * @return 返回键对应的方法名称
     */
    String key() default CoreConstants.EMPTY;

    /**
     * 持续时间，单位毫秒
     *
     * @return 返回持续时间
     */
    long during();

    /**
     * 方法在持续时间内人最大调用次数
     *
     * @return 返回最大调用次数
     */
    int count();

    /**
     * 在持续时间内超过最大调用次数后调用此方法
     *
     * @return 返回方法名称
     */
    String violate();

}
