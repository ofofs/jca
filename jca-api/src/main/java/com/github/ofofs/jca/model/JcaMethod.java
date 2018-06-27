package com.github.ofofs.jca.model;

import com.github.ofofs.jca.constants.JcaConstants;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法
 *
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaMethod extends JcaCommon {

    /**
     * 方法
     */
    private Symbol.MethodSymbol method;

    /**
     * 方法所属的类
     */
    private JcaClass jcaClass;

    public JcaMethod(Symbol.MethodSymbol method) {
        this.method = method;
        jcaClass = new JcaClass((Symbol.ClassSymbol) method.owner);
    }

    public JcaClass getJcaClass() {
        return jcaClass;
    }

    public Symbol.MethodSymbol getMethod() {
        return method;
    }

    /**
     * 在方法第一行插入一个变量
     *
     * @param jcaVariable 变量
     * @return 返回当前方法
     */
    public JcaMethod insert(JcaVariable jcaVariable) {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(this.getMethod());
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

        importPackage(getJcaClass(), jcaVariable.getTypeClass());
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(jcaVariable.getVarName()), getType(jcaVariable.getTypeClass()), jcaVariable.getValue().getObject()));
        for (JCTree.JCStatement statement : methodDecl.body.stats) {
            statements.append(statement);
        }
        methodDecl.body.stats = statements.toList();
        return this;
    }

    /**
     * 在方法第一行插入一个表达式
     *
     * @param express 表达式
     * @return 返回当前方法
     */
    public JcaMethod insert(JcaObject express) {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(getMethod());
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

        statements.append(treeMaker.Exec(express.getObject()));
        for (JCTree.JCStatement statement : methodDecl.body.stats) {
            statements.append(statement);
        }
        methodDecl.body.stats = statements.toList();
        return this;
    }

    /**
     * 在方法第一行插入一个代码块
     *
     * @param express 表达式
     * @return 返回当前方法
     */
    public JcaMethod insertBlock(JcaObject express) {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(getMethod());
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

        statements.append(express.getStatement());
        for (JCTree.JCStatement statement : methodDecl.body.stats) {
            statements.append(statement);
        }
        methodDecl.body.stats = statements.toList();
        return this;
    }

    /**
     * 获取方法名
     *
     * @return 返回方法名
     */
    public String getMethodName() {
        return method.name.toString();
    }

    /**
     * 设置方法的修饰符
     *
     * @param modifier 修饰符
     */
    public void setModifier(int modifier) {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(method);
        methodDecl.mods = treeMaker.Modifiers(modifier);
    }

    /**
     * 获取方法的参数
     *
     * @return 返回方法的参数
     */
    public List<JcaObject> getArgs() {
        com.sun.tools.javac.util.List<Symbol.VarSymbol> params = method.params;
        List<JcaObject> result = new ArrayList();

        if (params != null) {
            for (Symbol.VarSymbol var : params) {
                result.add(getVar(var));
            }
        }

        return result;
    }

    /**
     * 获取方法的返回类型
     *
     * @return 返回方法的返回类型
     */
    public JcaObject getReturnType() {
        return new JcaObject(((JCTree.JCMethodDecl) trees.getTree(method)).restype);
    }

    /**
     * 方法返回的回调
     *
     * @param returnValue 返回值
     * @return 返回方法的返回值
     */
    public JcaObject onReturn(JcaObject returnValue) {
        return returnValue;
    }

    /**
     * 处理方法的返回值，遇到方法返回处，会回调onReturn。
     *
     * @return 返回放弃方法
     */
    public JcaMethod visitReturn() {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(method);
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        com.sun.tools.javac.util.List<JCTree.JCStatement> stats = methodDecl.body.stats;
        if (stats.isEmpty()) {
            JcaObject jcaObject = onReturn(getNull());
            statements.append(treeMaker.Exec(jcaObject.getObject()));
        }
        JcaObject returnType = getReturnType();
        for (int i = 0; i < stats.size(); i++) {
            JCTree.JCStatement stat = stats.get(i);
            ListBuffer<JCTree.JCStatement> transStats = visitReturn(stat);
            for (JCTree.JCStatement st : transStats) {
                statements.append(st);
            }

            if (i == stats.size() - 1) {
                if (returnType.getObject() == null || JcaConstants.RETURN_VOID.equals(returnType.getObject().toString())) {
                    if (!(stat instanceof JCTree.JCReturn)) {
                        JcaObject jcaObject = onReturn(getNull());
                        statements.append(treeMaker.Exec(jcaObject.getObject()));
                    }
                }
            }
        }

        methodDecl.body.stats = statements.toList();
        return this;
    }

    /**
     * 处理返回值
     *
     * @param stat 当前代码块
     * @return 返回方法的代码块
     */
    private ListBuffer<JCTree.JCStatement> visitReturn(JCTree.JCStatement stat) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        if (stat instanceof JCTree.JCReturn) {
            JCTree.JCReturn jcReturn = (JCTree.JCReturn) stat;
            if (jcReturn.expr == null) {
                // return;
                JcaObject jcaObject = onReturn(getNull());
                statements.append(treeMaker.Exec(jcaObject.getObject()));
                statements.append(stat);
            } else {
                // return xxx;
                JcaObject jcaObject = onReturn(new JcaObject(jcReturn.expr));
                jcReturn.expr = jcaObject.getObject();
                statements.append(jcReturn);
            }
        } else if (stat instanceof JCTree.JCIf) {
            JCTree.JCIf jcIf = (JCTree.JCIf) stat;
            JCTree.JCBlock block;
            if (jcIf.thenpart != null) {
                if (jcIf.thenpart instanceof JCTree.JCBlock) {
                    block = (JCTree.JCBlock) jcIf.thenpart;
                    doBlock(block);
                    jcIf.thenpart = block;
                } else {
                    ListBuffer<JCTree.JCStatement> stats = visitReturn(jcIf.thenpart);
                    jcIf.thenpart = treeMaker.Block(stats.size(), stats.toList());
                }
            }
            if (jcIf.elsepart != null) {
                if (jcIf.elsepart instanceof JCTree.JCBlock) {
                    block = (JCTree.JCBlock) jcIf.elsepart;
                    doBlock(block);
                    jcIf.elsepart = block;
                } else {
                    ListBuffer<JCTree.JCStatement> stats = visitReturn(jcIf.elsepart);
                    jcIf.elsepart = treeMaker.Block(stats.size(), stats.toList());
                }
            }
            statements.append(jcIf);
        } else if (stat instanceof JCTree.JCForLoop) {
            JCTree.JCForLoop forLoop = (JCTree.JCForLoop) stat;
            forLoop.body = doLoop(forLoop.body);

            statements.append(forLoop);
        } else if (stat instanceof JCTree.JCDoWhileLoop) {
            JCTree.JCDoWhileLoop doWhileLoop = (JCTree.JCDoWhileLoop) stat;
            doWhileLoop.body = doLoop(doWhileLoop.body);

            statements.append(doWhileLoop);
        } else if (stat instanceof JCTree.JCWhileLoop) {
            JCTree.JCWhileLoop whileLoop = (JCTree.JCWhileLoop) stat;
            whileLoop.body = doLoop(whileLoop.body);

            statements.append(whileLoop);
        } else {
            statements.append(stat);
        }

        return statements;
    }

    /**
     * 处理循环
     *
     * @param stat 循环
     * @return 返回处理后的循环
     */
    private JCTree.JCStatement doLoop(JCTree.JCStatement stat) {
        if (stat instanceof JCTree.JCBlock) {
            JCTree.JCBlock block = (JCTree.JCBlock) stat;
            doBlock(block);
            stat = block;
        } else {
            ListBuffer<JCTree.JCStatement> stats = visitReturn(stat);
            stat = treeMaker.Block(stats.size(), stats.toList());
        }

        return stat;
    }

    /**
     * 处理代码块
     *
     * @param block 代码块
     */
    private void doBlock(JCTree.JCBlock block) {
        ListBuffer<JCTree.JCStatement> stats = new ListBuffer();
        for (JCTree.JCStatement st : block.getStatements()) {
            ListBuffer<JCTree.JCStatement> ss = visitReturn(st);

            for (JCTree.JCStatement stat : ss) {
                stats.append(stat);
            }
        }

        block.stats = stats.toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JcaMethod jcaMethod = (JcaMethod) o;

        if (method != null ? !method.equals(jcaMethod.method) : jcaMethod.method != null) {
            return false;
        }
        return jcaClass != null ? jcaClass.equals(jcaMethod.jcaClass) : jcaMethod.jcaClass == null;
    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + (jcaClass != null ? jcaClass.hashCode() : 0);
        return result;
    }
}
