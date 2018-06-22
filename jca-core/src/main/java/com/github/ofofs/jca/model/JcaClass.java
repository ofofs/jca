package com.github.ofofs.jca.model;

import com.github.ofofs.jca.util.JcaUtil;
import com.sun.tools.javac.code.Symbol;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaClass {

    /**
     * 类
     */
    private Symbol.ClassSymbol clazz;

    public JcaClass(Symbol.ClassSymbol clazz) {
        this.clazz = clazz;
    }

    /**
     * 插入一个字段
     *
     * @param jcaField 字段
     * @return 返回当前类
     */
    public JcaClass insertField(JcaField jcaField) {
        JcaUtil.createField(this, jcaField);
        return this;
    }

    public Symbol.ClassSymbol getClazz() {
        return clazz;
    }
}
