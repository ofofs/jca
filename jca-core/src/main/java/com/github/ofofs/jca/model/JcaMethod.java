package com.github.ofofs.jca.model;

import com.sun.tools.javac.code.Symbol;

/**
 * 方法
 *
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaMethod {

    /**
     * 方法
     */
    private Symbol.MethodSymbol method;

    /**
     * 方法所属的类
     */
    private JcaClass jcaClass;

    public JcaMethod(Symbol.MethodSymbol method) {
        this.method = method;
        jcaClass = new JcaClass((Symbol.ClassSymbol) method.owner);
    }

    public JcaClass getJcaClass() {
        return jcaClass;
    }

}
