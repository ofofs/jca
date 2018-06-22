package com.github.ofofs.jca.util;

import com.github.ofofs.jca.model.*;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * jca工具类
 *
 * @author kangyonggan
 * @since 6/22/18
 */
public final class JcaUtil {

    /**
     * 语法树
     */
    private static Trees trees;

    /**
     * 语法树创建者
     */
    private static TreeMaker treeMaker;

    /**
     * 变量命名器
     */
    private static Name.Table names;

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
     * 私有化构造
     */
    private JcaUtil() {
    }

    /**
     * 实例化一个类
     *
     * @param clazz 类
     * @return 返回类的一个实例
     */
    public static JcaObject instance(Class<?> clazz) {
        JCTree.JCExpression typeExpr = treeMaker.Ident(names.fromString(clazz.getSimpleName()));
        return new JcaObject(treeMaker.NewClass(null, List.nil(), typeExpr, List.nil(), null));
    }

    /**
     * 在类中创建一个字段（自动去重）
     *
     * @param jcaClass 所在类
     * @param jcaField 字段
     */
    public static void createField(JcaClass jcaClass, JcaField jcaField) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(jcaClass.getClazz());
        ListBuffer<JCTree> statements = new ListBuffer<>();

        if (existsField(jcaClass, jcaField.getFieldName())) {
            return;
        }

        importPackage(jcaClass, jcaField.getTypeClass());
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(jcaField.getModifiers()), names.fromString(jcaField.getFieldName()), getType(jcaField.getTypeClass()), jcaField.getValue().getObject()));
        for (JCTree jcTree : classDecl.defs) {
            statements.append(jcTree);
        }
        classDecl.defs = statements.toList();
    }

    /**
     * 在方法第一行插入一个变量
     *
     * @param jcaMethod   方法
     * @param jcaVariable 变量
     */
    public static void insertVariable(JcaMethod jcaMethod, JcaVariable jcaVariable) {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(jcaMethod.getMethod());
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

        importPackage(jcaMethod.getJcaClass(), jcaVariable.getTypeClass());
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(jcaVariable.getVarName()), getType(jcaVariable.getTypeClass()), jcaVariable.getValue().getObject()));
        for (JCTree.JCStatement statement : methodDecl.body.stats) {
            statements.append(statement);
        }
        methodDecl.body.stats = statements.toList();
    }

    /**
     * 在方法第一行插入一个表达式
     *
     * @param jcaMethod 方法
     * @param jcaObject 表达式
     */
    public static void insertExpress(JcaMethod jcaMethod, JcaObject jcaObject) {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(jcaMethod.getMethod());
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

        statements.append(treeMaker.Exec(jcaObject.getObject()));
        for (JCTree.JCStatement statement : methodDecl.body.stats) {
            statements.append(statement);
        }
        methodDecl.body.stats = statements.toList();
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
     * 调用变量的一个无参方法
     *
     * @param varName    变量名
     * @param methodName 方法名
     * @return 返回调用结果
     */
    public static JcaObject method(String varName, String methodName) {
        return method(varName, methodName, null);
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

    /**
     * 获取一个类型
     *
     * @param typeClass 类型
     * @return 返回一个类型
     */
    private static JCTree.JCIdent getType(Class<?> typeClass) {
        Symbol.ClassSymbol sym = new Symbol.ClassSymbol(Sequence.nextLong(), names.fromString(typeClass.getSimpleName()), null);
        return treeMaker.Ident(sym);
    }

    /**
     * 判断类中是否存在字段
     *
     * @param jcaClass  类
     * @param fieldName 字段名
     * @return 若存在返回true，否则返回false
     */
    private static boolean existsField(JcaClass jcaClass, String fieldName) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(jcaClass.getClazz());
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

    /**
     * 向指定的类中导入一个包
     *
     * @param jcaClass    目标类
     * @param importClass 要导入的包
     */
    private static void importPackage(JcaClass jcaClass, Class<?> importClass) {
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
}
