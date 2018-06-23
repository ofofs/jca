/*
 * Copyright (c)  2018. houbinbin Inc.
 * jca All rights reserved.
 */

package com.github.ofofs.jca.annotation.dev;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> 稳定的 </p>
 *
 * 1. 此实现验证已经非常成熟，可以放心使用
 * <pre> Created: 2018/6/23 上午6:49  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since JDK 1.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Stable {
}
