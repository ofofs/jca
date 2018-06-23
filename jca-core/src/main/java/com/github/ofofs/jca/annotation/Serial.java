package com.github.ofofs.jca.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 序列化注解。
 * 作用：
 * 1. implement Serializable
 * 2. 生成 serialVersionUID
 * @author kangyonggan
 * @author houbinbin
 * @since 6/22/18
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Serial {

}
