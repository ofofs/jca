/*
 * Copyright (c)  2018. houbinbin Inc.
 * jca All rights reserved.
 */

package com.github.ofofs.jca.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> 容易改变的 </p>
 *
 * 1. 表示一个想法，刚开始萌生，后期很容易被修正/完善/移除
 *
 * <pre> Created: 2018/6/23 上午6:49  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since JDK 1.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Alpha {
}
