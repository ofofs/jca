/*
 * Copyright (c)  2018. houbinbin Inc.
 * jca All rights reserved.
 */

package com.github.ofofs.jca.util;


import com.github.ofofs.jca.annotation.Util;

/**
 * <p> util 已经指定有参数构造器 </p>
 *
 * <pre> Created: 2018/6/11 下午8:34  </pre>
 * <pre> Project: lombok-ex  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since JDK 1.7
 */
@Util
public class UtilPubWithArgConsTest {

    private String name;

    public UtilPubWithArgConsTest(String name) {
        this.name = name;
    }
}
