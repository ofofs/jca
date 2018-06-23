/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (c) 2012-2018. ofofs Inc.
 * jca All rights reserved.
 */

package com.github.ofofs.jca.inner;

import com.github.ofofs.jca.annotation.dev.Alpha;
import com.sun.tools.javac.tree.JCTree;

/**
 * <p> Jca 共有属性接口 </p>
 *
 * 设计意图：对于很多功能，应该可以共同获取。比如：modifiers/jctree 属性的获取
 *
 * 1. 比较合理的方式 JcaXXX 继承共同的父类，缺点：会对使用者暴露 Jctree 等不该暴露的信息
 * 2. 全部使用工具类，会导致整个项目功能变得单薄.
 * 3. 如果后期想将本项目拆分成2部分，可以分为共有工具类(供所有开发者使用)+inner内部实现
 * <pre> Created: 2018/6/23 上午6:21  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since JDK 1.7
 * @see com.sun.source.tree.ClassTree class 树信息
 */
@Alpha
public interface IJcaCommon {

    /**
     * 字段修饰符
     *
     * @return 返回字段修饰符
     */
    long getModifiers();

    /**
     * 获取当前属性对应的
     * @return 获取对应的语法树
     */
    JCTree getJcTree();

}
