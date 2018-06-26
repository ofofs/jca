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
    private String typeClass;

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
        this.typeClass = typeClass.getName();
        this.fieldName = fieldName;
        this.value = value;
    }

    public JcaField(int modifiers, String typeClass, String fieldName, JcaObject value) {
        this.modifiers = modifiers;
        this.typeClass = typeClass;
        this.fieldName = fieldName;
        this.value = value;
    }

    public int getModifiers() {
        return modifiers;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public JcaObject getValue() {
        return value;
    }
}
