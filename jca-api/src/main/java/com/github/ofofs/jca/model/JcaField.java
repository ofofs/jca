package com.github.ofofs.jca.model;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import java.util.Objects;

import static com.github.ofofs.jca.model.JcaCommon.trees;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaField {

    /**
     * 字段的标识
     */
    private Symbol.VarSymbol varSym;

    /**
     * 字段的声明
     */
    private JCTree.JCVariableDecl variableDecl;

    /**
     * 字段修饰符
     */
    private long modifiers;

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

    public JcaField(long modifiers, Class<?> typeClass, String fieldName, JcaObject value) {
        this.modifiers = modifiers;
        this.typeClass = typeClass.getName();
        this.fieldName = fieldName;
        this.value = value;
    }

    public JcaField(long modifiers, String typeClass, String fieldName, JcaObject value) {
        this.modifiers = modifiers;
        this.typeClass = typeClass;
        this.fieldName = fieldName;
        this.value = value;
    }

    public JcaField(Symbol.VarSymbol varSym) {
        this.varSym = varSym;
        variableDecl = (JCTree.JCVariableDecl) trees.getTree(varSym);

        // 初始化一些信息
        this.modifiers = variableDecl.mods.flags;
        this.fieldName = variableDecl.name.toString();
    }

    /**
     * 获取字段的getter方法名
     *
     * @return 返回字段的getter方法名
     */
    public String getGetterMethodName() {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public long getModifiers() {
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

    /**
     * 获取字段类型
     *
     * @return 返回字段类型
     */
    public JcaObject getType() {
        return new JcaObject(variableDecl.vartype);
    }

    /**
     * 获取字段所在的类
     *
     * @return 返回字段所在的类
     */
    public JcaClass getJcaClass() {
        return new JcaClass((Symbol.ClassSymbol) varSym.owner);
    }

    /**
     * 判断字段是不是静态的
     *
     * @return 如果字段是静态的返回true，否则返回false
     */
    public boolean isStatic() {
        return hasModifier(Flags.STATIC);
    }

    /**
     * 判断字段是不是有某个修饰符
     *
     * @return 如果字段有某个修饰符的返回true，否则返回false
     */
    public boolean hasModifier(int modifier) {
        return variableDecl.mods.flags % (modifier * 2) >= modifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JcaField jcaField = (JcaField) o;
        return modifiers == jcaField.modifiers &&
                Objects.equals(varSym, jcaField.varSym) &&
                Objects.equals(variableDecl, jcaField.variableDecl) &&
                Objects.equals(typeClass, jcaField.typeClass) &&
                Objects.equals(fieldName, jcaField.fieldName) &&
                Objects.equals(value, jcaField.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varSym, variableDecl, modifiers, typeClass, fieldName, value);
    }
}
