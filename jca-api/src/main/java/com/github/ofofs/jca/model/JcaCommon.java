package com.github.ofofs.jca.model;

import com.github.ofofs.jca.constants.JcaConstants;
import com.github.ofofs.jca.util.Sequence;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author kangyonggan
 * @since 6/23/18
 */
public class JcaCommon {

    /**
     * 语法树
     */
    protected static Trees trees;

    /**
     * 语法树创建者
     */
    protected static TreeMaker treeMaker;

    /**
     * 变量命名器
     */
    protected static Name.Table names;

    /**
     * 初始化环境
     *
     * @param env 环境
     */
    public static void init(ProcessingEnvironment env) {
        trees = Trees.instance(env);
        Context context = ((JavacProcessingEnvironment) env).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;
    }

    /**
     * 实例化一个类
     *
     * @param jcaClass 所在的类
     * @param clazz    类
     * @return 返回类的一个实例
     */
    public static JcaObject instance(JcaClass jcaClass, Class<?> clazz) {
        importPackage(jcaClass, clazz);
        JCTree.JCExpression typeExpr = treeMaker.Ident(names.fromString(clazz.getSimpleName()));
        return new JcaObject(treeMaker.NewClass(null, List.nil(), typeExpr, List.nil(), null));
    }

    /**
     * 导入一个包
     *
     * @param jcaClass    所在的类
     * @param importClass 要导入的包
     */
    public static void importPackage(JcaClass jcaClass, Class<?> importClass) {
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getPath(jcaClass.getClazz()).getCompilationUnit();

        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(importClass.getPackage().getName())), names.fromString(importClass.getSimpleName()));
        JCTree.JCImport jcImport = treeMaker.Import(fieldAccess, false);

        ListBuffer<JCTree> imports = new ListBuffer<>();
        imports.append(jcImport);

        for (int i = 0; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
        }

        compilationUnit.defs = imports.toList();
    }

    /**
     * 调用变量的一个方法
     *
     * @param varName    变量名
     * @param methodName 方法名
     * @param args       参数
     * @return 返回调用结果
     */
    public static JcaObject method(String varName, String methodName, java.util.List<JcaObject> args) {
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString(methodName));
        List argsList = List.nil();
        if (args != null) {
            for (JcaObject jcaObject : args) {
                argsList = argsList.append(jcaObject.getObject());
            }
        }
        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, argsList);
        return new JcaObject(methodInvocation);
    }

    /**
     * 强制类型转换
     *
     * @param type  要转换的类型
     * @param value 值
     * @return 返回转换后的对象
     */
    public static JcaObject classCast(JcaObject type, JcaObject value) {
        if (JcaConstants.RETURN_VOID.equals(type.getObject().toString())) {
            return value;
        }
        return new JcaObject(treeMaker.TypeCast(type.getObject(), value.getObject()));
    }

    /**
     * 获取一个类型
     *
     * @param typeClass 类型
     * @return 返回一个类型
     */
    public static JCTree.JCIdent getType(Class<?> typeClass) {
        Symbol.ClassSymbol sym = new Symbol.ClassSymbol(Sequence.nextLong(), names.fromString(typeClass.getSimpleName()), null);
        return treeMaker.Ident(sym);
    }

    /**
     * 获取对象的值
     *
     * @param obj 对象
     * @return 返回对象的值
     */
    public static JcaObject getValue(Object obj) {
        if (obj == null) {
            return getNull();
        }
        return new JcaObject(treeMaker.Literal(obj));
    }

    /**
     * 获取变量
     *
     * @param var 变量
     * @return 返回变量
     */
    public static JcaObject getVar(Symbol var) {
        if (var == null) {
            return getNull();
        }
        return new JcaObject(treeMaker.Ident(var));
    }

    /**
     * 获取变量
     *
     * @param name 变量名
     * @return 返回变量
     */
    public static JcaObject getVar(String name) {
        if (name == null || name.length() == 0) {
            return getNull();
        }
        return new JcaObject(treeMaker.Ident(names.fromString(name)));
    }

    /**
     * 获取一个null
     *
     * @return 返回null
     */
    public static JcaObject getNull() {
        return new JcaObject(treeMaker.Literal(TypeTag.BOT, null));
    }

}
