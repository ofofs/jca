package com.github.ofofs.jca.model;

import com.sun.tools.javac.tree.JCTree;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaObject {

    /**
     * 对象
     */
    private JCTree.JCExpression object;

    /**
     * 代码块
     */
    private JCTree.JCStatement statement;

    public JcaObject(JCTree.JCExpression object) {
        this.object = object;
    }

    public JcaObject(JCTree.JCStatement statement) {
        this.statement = statement;
    }

    public JCTree.JCExpression getObject() {
        return object;
    }

    public void setObject(JCTree.JCExpression object) {
        this.object = object;
    }

    public JCTree.JCStatement getStatement() {
        return statement;
    }

    public void setStatement(JCTree.JCStatement statement) {
        this.statement = statement;
    }
}
