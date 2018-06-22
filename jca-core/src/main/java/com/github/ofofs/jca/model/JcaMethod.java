package com.github.ofofs.jca.model;

import com.github.ofofs.jca.util.JcaUtil;
import com.sun.tools.javac.code.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法
 *
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaMethod {

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
        JcaUtil.insertVariable(this, jcaVariable);
        return this;
    }

    /**
     * 在方法第一行插入一个表达式
     *
     * @param express 表达式
     * @return 返回当前方法
     */
    public JcaMethod insert(JcaObject express) {
        JcaUtil.insertExpress(this, express);
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
     * 获取方法的参数
     *
     * @return 返回方法的参数
     */
    public List<JcaObject> getArgs() {
        com.sun.tools.javac.util.List<Symbol.VarSymbol> params = method.params;
        List<JcaObject> result = new ArrayList();

        if (params != null) {
            for (Symbol.VarSymbol var : params) {
                result.add(JcaUtil.getVar(var));
            }
        }

        return result;
    }
}
