/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (c) 2012-2018. haiyi Inc.
 * jca All rights reserved.
 */

package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Util;
import com.github.ofofs.jca.annotation.Beta;
import com.github.ofofs.jca.model.JcaClass;
import com.sun.tools.javac.code.Flags;

import javax.annotation.processing.RoundEnvironment;

/**
 * <p> util 实现 </p>
 *
 * <pre> Created: 2018/6/23 上午6:03  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0.0
 * @since JDK 1.7
 * @see com.github.ofofs.jca.annotation.Util 工具类注解
 */
@Beta
public class UtilProcessor extends BaseProcessor {

    protected UtilProcessor(RoundEnvironment env) {
        super(env);
    }

    @Override
    protected void process() {
        for (JcaClass jcaClass : getJcaClasses(Util.class)) {
            process(jcaClass);
        }
    }

    /**
     * 处理每一个类
     *
     * @param jcaClass 类
     */
    private void process(JcaClass jcaClass) {
        // 设置访问符号为 final
        // ps: 这里添加一个修饰符应该也可以。后期可调整
        jcaClass.setModifier(Flags.PUBLIC | Flags.FINAL);

        // 设置无参数私有构造器
        jcaClass.setNoArgPrivateConstructor();
    }

}
