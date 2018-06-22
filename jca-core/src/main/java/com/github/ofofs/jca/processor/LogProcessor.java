package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Log;
import com.github.ofofs.jca.handler.impl.ConsoleLogHandler;
import com.github.ofofs.jca.model.*;
import com.github.ofofs.jca.util.JcaUtil;
import com.github.ofofs.jca.util.Sequence;
import com.sun.tools.javac.code.Flags;

import javax.annotation.processing.RoundEnvironment;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志注解处理器
 *
 * @author kangyonggan
 * @since 6/22/18
 */
public class LogProcessor extends BaseProcessor {

    LogProcessor(RoundEnvironment env) {
        super(env);
    }

    /**
     * 处理日志注解
     */
    @Override
    protected void process() {
        String fieldName = Sequence.nextString("field");
        for (JcaMethod jcaMethod : getJcaMethods(Log.class)) {
            process(jcaMethod, fieldName);
        }
    }

    /**
     * 处理每一个方法
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     */
    private void process(JcaMethod jcaMethod, String fieldName) {
        // 给方法所在的类创建一个字段
        createField(jcaMethod.getJcaClass(), fieldName);

        // 创建代码块logBefore
        createLogBefore(jcaMethod, fieldName);

        // 创建代码块startTime
        String startTime = createStartTime(jcaMethod);

        // 创建代码块logAfter
        createLogAfter(jcaMethod, fieldName, startTime);
    }

    /**
     * fieldName.logAfter(packageName, methodName, startTime, returnValue);
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     * @param startTime 开始时间
     */
    private void createLogAfter(JcaMethod jcaMethod, String fieldName, String startTime) {
        List<JcaObject> args = new ArrayList<>();
        // packageName
        args.add(JcaUtil.getValue(jcaMethod.getJcaClass().getPackageName()));
        // className
        args.add(JcaUtil.getValue(jcaMethod.getJcaClass().getClassName()));
        // methodName
        args.add(JcaUtil.getValue(jcaMethod.getMethodName()));
        // startTime
        args.add(JcaUtil.getVar(startTime));
        // TODO method's return express

        JcaObject express = JcaUtil.method(fieldName, "logAfter", args);
//        jcaMethod.append(express);
    }

    /**
     * Long startTime = System.currentTimeMillis();
     *
     * @param jcaMethod 方法
     * @return 返回变量名
     */
    private String createStartTime(JcaMethod jcaMethod) {
        String varName = Sequence.nextString("var");

        // System.currentTimeMillis()
        JcaObject value = JcaUtil.staticMethod(jcaMethod.getJcaClass(), System.class, "currentTimeMillis");

        // Long startTime = value;
        JcaVariable jcaVariable = new JcaVariable(Long.class, varName, value);
        jcaMethod.insert(jcaVariable);
        return varName;
    }

    /**
     * fieldName.logBefore(packageName, methodName, args);
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名称
     */
    private void createLogBefore(JcaMethod jcaMethod, String fieldName) {
        List<JcaObject> args = new ArrayList<>();
        // packageName
        args.add(JcaUtil.getValue(jcaMethod.getJcaClass().getPackageName()));
        // className
        args.add(JcaUtil.getValue(jcaMethod.getJcaClass().getClassName()));
        // methodName
        args.add(JcaUtil.getValue(jcaMethod.getMethodName()));
        // method's args
        args.addAll(jcaMethod.getArgs());

        JcaObject express = JcaUtil.method(fieldName, "logBefore", args);
        jcaMethod.insert(express);
    }

    /**
     * private static final ConsoleLogHandler fieldName = new ConsoleLogHandler();
     *
     * @param jcaClass  类
     * @param fieldName 字段名
     */
    private void createField(JcaClass jcaClass, String fieldName) {
        // TODO 暂时写死ConsoleLogHandler，后面可配置
        // new ConsoleLogHandler()
        JcaObject value = JcaUtil.instance(ConsoleLogHandler.class);

        // private static final ConsoleLogHandler fieldName = value;
        JcaField jcaField = new JcaField(Flags.PRIVATE | Flags.STATIC | Flags.FINAL, ConsoleLogHandler.class, fieldName, value);
        jcaClass.insert(jcaField);
    }
}
