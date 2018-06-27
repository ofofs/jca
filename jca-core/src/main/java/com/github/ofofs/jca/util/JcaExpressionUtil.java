package com.github.ofofs.jca.util;

import com.kangyonggan.jcel.JCExpressionParser;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author kangyonggan
 * @since 6/27/18
 */
public final class JcaExpressionUtil {

    /**
     * 解析器
     */
    private static JCExpressionParser parser;

    private JcaExpressionUtil() {
    }

    public static void init(ProcessingEnvironment env) {
        Context context = ((JavacProcessingEnvironment) env).getContext();
        parser = new JCExpressionParser(TreeMaker.instance(context), Names.instance(context).table);
    }

    /**
     * 解析
     *
     * @param express 表达式
     * @return 返回解析后的表达式
     */
    public static JCTree.JCExpression parse(String express) {
        return parser.parse(express);
    }
}
