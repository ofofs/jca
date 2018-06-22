package com.github.ofofs.jca.model;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaField {

    /**
     * 字段修饰符
     */
    private int modifiers;

    /**
     * 字段类型
     */
    private Class<?> typeClass;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段的值
     */
    private JcaObject value;

    public JcaField(int modifiers, Class<?> typeClass, String fieldName, JcaObject value) {
        this.modifiers = modifiers;
        this.typeClass = typeClass;
        this.fieldName = fieldName;
        this.value = value;
    }

    public int getModifiers() {
        return modifiers;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public JcaObject getValue() {
        return value;
    }
}
