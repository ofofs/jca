package com.github.ofofs.jca.annotation;

import com.github.ofofs.jca.constants.CoreConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限制方法连续两次调用的间隔时间。
 *
 * @author kangyonggan
 * @since 6/27/18
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Frequency {

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
     * 间隔时间，单位毫秒
     *
     * @return 返回间隔时间
     */
    long interval();

    /**
     * 连续两次调用的间隔时间小于interval就会调用此方法
     *
     * @return 返回方法名称
     */
    String violate();
}
