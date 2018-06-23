package com.github.ofofs.jca.util;

import com.github.ofofs.jca.annotation.dev.Alpha;
import com.github.ofofs.jca.annotation.dev.Beta;
import com.github.ofofs.jca.constants.JcaConstants;
import com.github.ofofs.jca.model.*;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * jca工具类
 *
 * @author kangyonggan
 * @since 6/22/18
 */
public final class JcaUtil {

    /**
     * 构造器名称
     */
    @Beta
    private static final String CONSTRUCTOR_NAME = "<init>";

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
     * 给类添加一个接口
     *
     * @param jcaClass       类
     * @param interfaceClass 接口
     */
    public static void addInterface(JcaClass jcaClass, Class<?> interfaceClass) {
        // 判断类有没有实现此接口
        if (!hasInterface(jcaClass, interfaceClass)) {
            // 导包（会自动去重）
            importPackage(jcaClass, interfaceClass);

            JCTree.JCClassDecl clazz = (JCTree.JCClassDecl) trees.getTree(jcaClass.getClazz());
            java.util.List<JCTree.JCExpression> implementing = clazz.implementing;
            ListBuffer<JCTree.JCExpression> statements = new ListBuffer<>();
            for (JCTree.JCExpression impl : implementing) {
                statements.append(impl);
            }

            Symbol.ClassSymbol sym = new Symbol.ClassSymbol(Sequence.nextLong(), names.fromString(interfaceClass.getSimpleName()), null);
            statements.append(treeMaker.Ident(sym));
            clazz.implementing = statements.toList();
        }
    }

    /**
     * 设置访问修饰符
     * 1. 这个方法应该设计成，所有的 jca 对象公用。暂时先不处理(因为 jca 对象暂无共有父类)
     * 2. 注意：此方法为设置，会覆盖原来的访问修饰符
     *
     * @param jcaClass class 信息
     * @param modifier 访问修饰符
     */
    @Alpha
    public static void setModifier(JcaClass jcaClass, final long modifier) {
        JCTree tree = (JCTree) trees.getTree(jcaClass.getClazz());
        tree.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                jcClassDecl.mods = treeMaker.Modifiers(modifier, List.nil());
            }
        });
    }

    /**
     * 设置访问修饰符
     *
     * @param jcMethodDecl class 信息
     * @param modifier     访问修饰符
     */
    @Alpha
    public static void setModifier(JCTree.JCMethodDecl jcMethodDecl, final long modifier) {
        jcMethodDecl.accept(new TreeTranslator() {
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                jcMethodDecl.mods = treeMaker.Modifiers(modifier, List.nil());
            }
        });
    }

    /**
     * 设置无参数构造器
     * 有两种方式：
     * 0. 如果存在，dn
     * 1. 删除原来的 pub 无参数构造器，添加私有构造器
     * 2. 将 pub 无参数构造器设置为 pri; (√)
     *
     * @param jcaClass jca class
     */
    @Alpha
    public static void setNoArgPrivateConstructor(JcaClass jcaClass) {
        JCTree tree = (JCTree) trees.getTree(jcaClass.getClazz());
        tree.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                List<JCTree> oldList = this.translate(jcClassDecl.defs);
                ListBuffer<JCTree> statements = new ListBuffer<>();

                boolean hasPrivateDefaultConstructor = false;
                for (JCTree jcTree : oldList) {
                    if (isDefaultConstructor(jcTree, Modifier.PUBLIC)) {
                        //1. 设置访问符号为私有
                        setModifier((JCTree.JCMethodDecl) jcTree, Flags.PRIVATE);
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
                            names.fromString(CONSTRUCTOR_NAME),
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
     * 获取方法的声明
     *
     * @param jcaMethod 方法
     * @return 返回方法的声明
     */
    public static JCTree.JCMethodDecl getMethodDecl(JcaMethod jcaMethod) {
        return (JCTree.JCMethodDecl) trees.getTree(jcaMethod.getMethod());
    }

    /**
     * 处理方法的返回值
     *
     * @param jcaMethod 方法
     */
    public static void visitReturn(JcaMethod jcaMethod) {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(jcaMethod.getMethod());
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        List<JCTree.JCStatement> stats = methodDecl.body.stats;
        if (stats.isEmpty()) {
            JcaObject jcaObject = jcaMethod.onReturn(getNull());
            statements.append(treeMaker.Exec(jcaObject.getObject()));
        }
        JcaObject returnType = jcaMethod.getReturnType();
        for (int i = 0; i < stats.size(); i++) {
            JCTree.JCStatement stat = stats.get(i);
            ListBuffer<JCTree.JCStatement> transStats = visitReturn(jcaMethod, stat);
            for (JCTree.JCStatement st : transStats) {
                statements.append(st);
            }

            if (i == stats.size() - 1) {
                if (returnType.getObject() == null || JcaConstants.RETURN_VOID.equals(returnType.getObject().toString())) {
                    if (!(stat instanceof JCTree.JCReturn)) {
                        JcaObject jcaObject = jcaMethod.onReturn(getNull());
                        statements.append(treeMaker.Exec(jcaObject.getObject()));
                    }
                }
            }
        }

        methodDecl.body.stats = statements.toList();
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
        return CONSTRUCTOR_NAME.equals(name);
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

    /**
     * 判断类有没有实现指定的接口
     *
     * @param jcaClass       类
     * @param interfaceClass 接口
     * @return 如果类已经实现了指定接口则返回true，否则返回false
     */
    private static boolean hasInterface(JcaClass jcaClass, Class<?> interfaceClass) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(jcaClass.getClazz());
        for (JCTree.JCExpression impl : classDecl.implementing) {
            if (impl.type.toString().equals(interfaceClass.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理返回值
     *
     * @param jcaMethod 方法
     * @param stat      当前代码块
     * @return 返回方法的代码块
     */
    private static ListBuffer<JCTree.JCStatement> visitReturn(JcaMethod jcaMethod, JCTree.JCStatement stat) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        if (stat instanceof JCTree.JCReturn) {
            JCTree.JCReturn jcReturn = (JCTree.JCReturn) stat;
            if (jcReturn.expr == null) {
                // return;
                JcaObject jcaObject = jcaMethod.onReturn(getNull());
                statements.append(treeMaker.Exec(jcaObject.getObject()));
                statements.append(stat);
            } else {
                // return xxx;
                JcaObject jcaObject = jcaMethod.onReturn(new JcaObject(jcReturn.expr));
                jcReturn.expr = jcaObject.getObject();
                statements.append(jcReturn);
            }
        } else if (stat instanceof JCTree.JCIf) {
            JCTree.JCIf jcIf = (JCTree.JCIf) stat;
            JCTree.JCBlock block;
            if (jcIf.thenpart != null) {
                if (jcIf.thenpart instanceof JCTree.JCBlock) {
                    block = (JCTree.JCBlock) jcIf.thenpart;
                    doBlock(jcaMethod, block);
                    jcIf.thenpart = block;
                } else {
                    ListBuffer<JCTree.JCStatement> stats = visitReturn(jcaMethod, jcIf.thenpart);
                    jcIf.thenpart = treeMaker.Block(stats.size(), stats.toList());
                }
            }
            if (jcIf.elsepart != null) {
                if (jcIf.elsepart instanceof JCTree.JCBlock) {
                    block = (JCTree.JCBlock) jcIf.elsepart;
                    doBlock(jcaMethod, block);
                    jcIf.elsepart = block;
                } else {
                    ListBuffer<JCTree.JCStatement> stats = visitReturn(jcaMethod, jcIf.elsepart);
                    jcIf.elsepart = treeMaker.Block(stats.size(), stats.toList());
                }
            }
            statements.append(jcIf);
        } else if (stat instanceof JCTree.JCForLoop) {
            JCTree.JCForLoop forLoop = (JCTree.JCForLoop) stat;
            forLoop.body = doLoop(jcaMethod, forLoop.body);

            statements.append(forLoop);
        }  else if (stat instanceof JCTree.JCDoWhileLoop) {
            JCTree.JCDoWhileLoop doWhileLoop = (JCTree.JCDoWhileLoop) stat;
            doWhileLoop.body = doLoop(jcaMethod, doWhileLoop.body);

            statements.append(doWhileLoop);
        } else if (stat instanceof JCTree.JCWhileLoop) {
            JCTree.JCWhileLoop whileLoop = (JCTree.JCWhileLoop) stat;
            whileLoop.body = doLoop(jcaMethod, whileLoop.body);

            statements.append(whileLoop);
        } else {
            statements.append(stat);
        }

        return statements;
    }

    private static JCTree.JCStatement doLoop(JcaMethod jcaMethod, JCTree.JCStatement stat) {
        if (stat instanceof JCTree.JCBlock) {
            JCTree.JCBlock block = (JCTree.JCBlock) stat;
            doBlock(jcaMethod, block);
            stat = block;
        } else {
            ListBuffer<JCTree.JCStatement> stats = visitReturn(jcaMethod, stat);
            stat = treeMaker.Block(stats.size(), stats.toList());
        }

        return stat;
    }

    private static void doBlock(JcaMethod jcaMethod, JCTree.JCBlock block) {
        ListBuffer<JCTree.JCStatement> stats = new ListBuffer();
        for (JCTree.JCStatement st : block.getStatements()) {
            ListBuffer<JCTree.JCStatement> ss = visitReturn(jcaMethod, st);

            for (JCTree.JCStatement stat : ss) {
                stats.append(stat);
            }
        }

        block.stats = stats.toList();
    }
}
