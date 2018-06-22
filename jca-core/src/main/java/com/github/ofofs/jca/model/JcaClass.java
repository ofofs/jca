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
     * 创建一个字段（自动去重）
     *
     * @param modifiers 修饰符
     * @param typeClass 字段类型
     * @param fieldName 字段名称
     * @param value     字段的值
     * @return 返回当前类
     */
    public JcaClass createField(int modifiers, Class<?> typeClass, String fieldName, JcaObject value) {
        JcaUtil.createField(this, modifiers, typeClass, fieldName, value.getObject());
        return this;
    }

    public Symbol.ClassSymbol getClazz() {
        return clazz;
    }
}
