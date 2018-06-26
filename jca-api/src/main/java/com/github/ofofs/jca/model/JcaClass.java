package com.github.ofofs.jca.model;

import com.github.ofofs.jca.annotation.Alpha;
import com.github.ofofs.jca.constants.JcaConstants;
import com.github.ofofs.jca.util.Sequence;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaClass extends JcaCommon {

    /**
     * 类
     */
    private Symbol.ClassSymbol clazz;

    /**
     * 类的定义
     */
    private JCTree.JCClassDecl classDecl;

    public JcaClass(Symbol.ClassSymbol clazz) {
        this.clazz = clazz;
        classDecl = (JCTree.JCClassDecl) trees.getTree(clazz);
    }

    /**
     * 插入一个字段
     *
     * @param jcaField 字段
     * @return 返回当前类
     */
    public JcaClass insert(JcaField jcaField) {
        ListBuffer<JCTree> statements = new ListBuffer<>();

        if (existsField(jcaField.getFieldName())) {
            return this;
        }

        importPackage(this, jcaField.getTypeClass());
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(jcaField.getModifiers()), names.fromString(jcaField.getFieldName()), getType(jcaField.getTypeClass()), jcaField.getValue().getObject()));
        for (JCTree jcTree : classDecl.defs) {
            statements.append(jcTree);
        }
        classDecl.defs = statements.toList();
        return this;
    }

    /**
     * 获取当前类
     *
     * @return 返回当前类
     */
    public Symbol.ClassSymbol getClazz() {
        return clazz;
    }

    /**
     * 获取类的全路径
     *
     * @return 返回类的全路径
     */
    public String getFullName() {
        return clazz.fullname.toString();
    }

    /**
     * 获取类的包名
     *
     * @return 返回类的包名
     */
    public String getPackageName() {
        String fullName = clazz.fullname.toString();
        return fullName.substring(0, fullName.lastIndexOf("."));
    }

    /**
     * 获取类名
     *
     * @return 返回类名
     */
    public String getClassName() {
        String fullName = clazz.fullname.toString();
        return fullName.substring(fullName.lastIndexOf(".") + 1);
    }

    /**
     * 添加接口
     *
     * @param interfaceClass 接口类
     * @return 返回当前类
     */
    public JcaClass addInterface(Class<?> interfaceClass) {
        // 判断类有没有实现此接口
        if (!hasInterface(interfaceClass)) {
            // 导包（会自动去重）
            importPackage(this, interfaceClass);

            java.util.List<JCTree.JCExpression> implementing = classDecl.implementing;
            ListBuffer<JCTree.JCExpression> statements = new ListBuffer<>();
            for (JCTree.JCExpression impl : implementing) {
                statements.append(impl);
            }

            Symbol.ClassSymbol sym = new Symbol.ClassSymbol(Sequence.nextLong(), names.fromString(interfaceClass.getSimpleName()), null);
            statements.append(treeMaker.Ident(sym));
            classDecl.implementing = statements.toList();
        }
        return this;
    }

    /**
     * 调用一个静态无参方法
     *
     * @param jcaClass   所在的类
     * @param clazz      目标类
     * @param methodName 方法名
     * @return 方法调用结果
     */
    public static JcaObject staticMethod(JcaClass jcaClass, Class<?> clazz, String methodName) {
        importPackage(jcaClass, clazz);
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(clazz.getSimpleName())), names.fromString(methodName));
        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
        return new JcaObject(methodInvocation);
    }

    /**
     * 设置类的修饰符
     *
     * @param modifier 修饰符
     */
    public void setModifier(int modifier) {
        classDecl.mods = treeMaker.Modifiers(modifier);
    }

    /**
     * 设置无参数私有构造器
     */
    public void setNoArgPrivateConstructor() {
        JCTree tree = (JCTree) trees.getTree(clazz);
        tree.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                List<JCTree> oldList = this.translate(jcClassDecl.defs);
                ListBuffer<JCTree> statements = new ListBuffer<>();

                boolean hasPrivateDefaultConstructor = false;
                for (JCTree jcTree : oldList) {
                    if (isDefaultConstructor(jcTree, Modifier.PUBLIC)) {
                        //1. 设置访问符号为私有
                        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) jcTree;
                        JcaMethod jcaMethod = new JcaMethod(methodDecl.sym);
                        jcaMethod.setModifier(Flags.PRIVATE);

                        hasPrivateDefaultConstructor = true;
                    }
                    if (isDefaultConstructor(jcTree, Modifier.PRIVATE)) {
                        hasPrivateDefaultConstructor = true;
                    }
                    statements.append(jcTree);
                }

                if (!hasPrivateDefaultConstructor) {
                    // 添加私有构造器
                    JCTree.JCBlock block = treeMaker.Block(0L, List.nil());
                    JCTree.JCMethodDecl constructor = treeMaker.MethodDef(
                            treeMaker.Modifiers(Flags.PRIVATE, List.nil()),
                            names.fromString(JcaConstants.CONSTRUCTOR_NAME),
                            null,
                            List.nil(),
                            List.nil(),
                            List.nil(),
                            block,
                            null);

                    statements.append(constructor);
                    //更新
                    jcClassDecl.defs = statements.toList();
                }
                this.result = jcClassDecl;
            }
        });
    }


    /**
     * 是否为共有默认构造器
     *
     * @param jcTree   tree 信息
     * @param modifier 访问修饰符
     * @return {@code true} 是
     */
    @Alpha
    private static boolean isDefaultConstructor(JCTree jcTree, Modifier modifier) {

        if (jcTree.getKind() == Tree.Kind.METHOD) {
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) jcTree;
            return isConstructor(jcMethodDecl)
                    && isNoArgsMethod(jcMethodDecl)
                    && isMatchModifierMethod(jcMethodDecl, modifier);
        }

        return false;
    }

    /**
     * 是否为构造器
     *
     * @param jcMethodDecl 方法声明
     * @return {@code true} 是
     */
    @Alpha
    private static boolean isConstructor(JCTree.JCMethodDecl jcMethodDecl) {
        String name = jcMethodDecl.name.toString();
        return JcaConstants.CONSTRUCTOR_NAME.equals(name);
    }

    /**
     * 是否为无参方法
     *
     * @param jcMethodDecl 方法声明
     * @return {@code true} 是
     */
    @Alpha
    private static boolean isNoArgsMethod(JCTree.JCMethodDecl jcMethodDecl) {
        List<JCTree.JCVariableDecl> jcVariableDeclList = jcMethodDecl.getParameters();
        return jcVariableDeclList == null
                || jcVariableDeclList.size() == 0;
    }

    /**
     * 是否为匹配修饰符的方法
     *
     * @param jcMethodDecl 方法声明
     * @param modifier     修饰符
     * @return {@code true} 是
     */
    @Alpha
    private static boolean isMatchModifierMethod(JCTree.JCMethodDecl jcMethodDecl,
                                                 Modifier modifier) {
        JCTree.JCModifiers jcModifiers = jcMethodDecl.getModifiers();
        Set<Modifier> modifiers = jcModifiers.getFlags();
        return modifiers.contains(modifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JcaClass jcaClass = (JcaClass) o;

        return clazz != null ? clazz.equals(jcaClass.clazz) : jcaClass.clazz == null;
    }

    @Override
    public int hashCode() {
        return clazz != null ? clazz.hashCode() : 0;
    }

    /**
     * 判断有没有实现指定的接口
     *
     * @param interfaceClass 接口
     * @return 如果类已经实现了指定接口则返回true，否则返回false
     */
    private boolean hasInterface(Class<?> interfaceClass) {
        for (JCTree.JCExpression impl : classDecl.implementing) {
            if (impl.type.toString().equals(interfaceClass.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在字段
     *
     * @param fieldName 字段名
     * @return 若存在返回true，否则返回false
     */
    private boolean existsField(String fieldName) {
        for (JCTree jcTree : classDecl.defs) {
            if (jcTree.getKind() == Tree.Kind.VARIABLE) {
                JCTree.JCVariableDecl var = (JCTree.JCVariableDecl) jcTree;
                if (fieldName.equals(var.name.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
