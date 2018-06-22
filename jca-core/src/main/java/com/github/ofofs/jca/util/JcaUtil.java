package com.github.ofofs.jca.util;

import com.github.ofofs.jca.model.JcaClass;
import com.github.ofofs.jca.model.JcaObject;
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
     * @param jcaClass  所在类
     * @param modifiers 修饰符
     * @param typeClass 字段类型
     * @param fieldName 字段名称
     * @param value     字段的值
     */
    public static void createField(JcaClass jcaClass, int modifiers, Class<?> typeClass, String fieldName, JCTree.JCExpression value) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(jcaClass.getClazz());
        ListBuffer<JCTree> statements = new ListBuffer<>();

        if (existsField(jcaClass, fieldName)) {
            return;
        }

        importPackage(jcaClass, typeClass);
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(modifiers), names.fromString(fieldName), getType(typeClass), value));
        for (JCTree jcTree : classDecl.defs) {
            statements.append(jcTree);
        }
        classDecl.defs = statements.toList();
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
     * 判断类中是否存在字段
     *
     * @param jcaClass  类
     * @param fieldName 字段名
     * @return 若存在返回true，否则返回false
     */
    public static boolean existsField(JcaClass jcaClass, String fieldName) {
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
     * 获取一个null
     *
     * @return 返回null
     */
    public static JCTree.JCLiteral getNull() {
        return treeMaker.Literal(TypeTag.BOT, null);
    }

    /**
     * 获取对象的值
     *
     * @param obj 对象
     * @return 返回对象的值
     */
    public static JCTree.JCExpression getValue(Object obj) {
        if (obj == null) {
            return getNull();
        }
        return treeMaker.Literal(obj);
    }
}
