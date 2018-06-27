/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (c) 2012-2018. haiyi Inc.
 * jca All rights reserved.
 */

package com.github.ofofs.jca.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当工具类添加此注解。
 * 1. 将其 constructor 默认 private{};
 * 2. 将当前类设置为 final;
 *
 * @author bbhou
 * @version 1.0.0
 * @since 1.0.0, 2018-06-11 13:28:11
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
@API(status = API.Status.EXPERIMENTAL)
public @interface Util {
}
