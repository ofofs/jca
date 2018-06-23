package com.github.ofofs.jca.model;

import com.sun.tools.javac.tree.JCTree;

/**
 * @author kangyonggan
 * @since 6/23/18
 */
public class JcaAnnotation {

    /**
     * 注解
     */
    private JCTree.JCAnnotation annotation;

    public JcaAnnotation(JCTree.JCAnnotation annotation) {
        this.annotation = annotation;
    }

    /**
     * 获取注解的属性值
     *
     * @param name 属性名
     * @return 返回注解的属性值
     */
    public Object getAttribute(String name) {
        for (JCTree.JCExpression arg : annotation.args) {
            JCTree.JCAssign assign = (JCTree.JCAssign) arg;
            if (assign.lhs.toString().equals(name)) {
                // TODO
            }
        }
        return null;
    }
}
