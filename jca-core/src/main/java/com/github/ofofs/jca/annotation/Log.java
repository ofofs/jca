package com.github.ofofs.jca.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法日志注解。
 * <p>在任意方法上加上此注解可以打印方法的入参、出参和耗时。</p>
 * <p>默认打印在控制台，也可以自定义打印方式，具体可以参考：<a href="https://github.com/ofofs/jca/wiki/@Log">https://github.com/ofofs/jca/wiki/@Log</a>。</p>
 *
 * @author kangyonggan
 * @since 6/22/18
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Log {

}
