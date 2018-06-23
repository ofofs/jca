package com.github.ofofs.jca.model;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaVariable {

    /**
     * 变量类型
     */
    private Class<?> typeClass;

    /**
     * 变量名称
     */
    private String varName;

    /**
     * 变量的值
     */
    private JcaObject value;

    public JcaVariable(Class<?> typeClass, String varName, JcaObject value) {
        this.typeClass = typeClass;
        this.varName = varName;
        this.value = value;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public String getVarName() {
        return varName;
    }

    public JcaObject getValue() {
        return value;
    }
}
