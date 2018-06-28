package com.github.ofofs.jca.model;

import com.sun.tools.javac.tree.JCTree;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaObject {

    /**
     * 表达式
     */
    private JCTree.JCExpression expression;

    /**
     * 代码块
     */
    private JCTree.JCStatement statement;

    public JcaObject(JCTree.JCExpression expression) {
        this.expression = expression;
    }

    public JcaObject(JCTree.JCStatement statement) {
        this.statement = statement;
    }

    public JCTree.JCExpression getExpression() {
        return expression;
    }

    public void setExpression(JCTree.JCExpression expression) {
        this.expression = expression;
    }

    public JCTree.JCStatement getStatement() {
        return statement;
    }

    public void setStatement(JCTree.JCStatement statement) {
        this.statement = statement;
    }
}
