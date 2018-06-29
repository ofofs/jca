package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Frequency;
import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.constants.CoreConstants;
import com.github.ofofs.jca.handler.impl.MemoryFrequencyHandler;
import com.github.ofofs.jca.model.*;
import com.github.ofofs.jca.util.JcaExpressionUtil;
import com.github.ofofs.jca.util.PropertiesUtil;
import com.github.ofofs.jca.util.Sequence;
import com.sun.tools.javac.code.Flags;

import javax.annotation.processing.RoundEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 调用间隔限制注解处理器
 *
 * @author kangyonggan
 * @since 6/23/18
 */
public class FrequencyProcessor extends AbstractJcaProcessor {

    protected FrequencyProcessor(RoundEnvironment env) {
        super(env);
    }

    @Override
    protected void process() {
        if (isEnable(Handler.Type.FREQUENCY)) {
            String fieldName = Sequence.nextString("field");
            for (JcaMethod jcaMethod : getJcaMethods(Frequency.class)) {
                process(jcaMethod, fieldName);
            }
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

        createIsViolate(jcaMethod, fieldName);
    }

    /**
     * if (memoryFrequencyHandler.isViolate(key, interval, count)) {return (ReturnType) this.violate(args);}
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     */
    private void createIsViolate(JcaMethod jcaMethod, String fieldName) {
        Frequency frequency = jcaMethod.getMethodSym().getAnnotation(Frequency.class);

        // memoryFrequencyHandler.isViolate(key, interval)
        List<JcaObject> args = new ArrayList<>();
        // key
        args.add(getKey(jcaMethod, frequency));
        // interval
        args.add(JcaCommon.getValue(frequency.interval()));
        JcaObject isViolate = JcaCommon.method(fieldName, "isViolate", args);

        if (jcaMethod.hasReturnValue()) {
            // 有返回值的情况
            // if (isViolate) {return (returnType) this.violate(args);}
            JcaObject ifBlock;
            if (jcaMethod.isStatic()) {
                ifBlock = JcaCommon.getReturn(JcaCommon.classCast(jcaMethod.getReturnType(), JcaCommon.method(jcaMethod.getJcaClass().getClassName(), frequency.violate(), jcaMethod.getArgs())));
            } else {
                ifBlock = JcaCommon.getReturn(JcaCommon.classCast(jcaMethod.getReturnType(), JcaCommon.method("this", frequency.violate(), jcaMethod.getArgs())));
            }
            JcaObject ifExpress = JcaCommon.getIf(isViolate, ifBlock);

            jcaMethod.insertBlock(ifExpress);
        } else {
            // 没返回值的情况
            // if (isViolate) {this.violate(args);return;}
            JcaObject ifBlock;
            if (jcaMethod.isStatic()) {
                ifBlock = JcaCommon.method(jcaMethod.getJcaClass().getClassName(), frequency.violate(), jcaMethod.getArgs());
            } else {
                ifBlock = JcaCommon.method("this", frequency.violate(), jcaMethod.getArgs());
            }
            ifBlock = JcaCommon.block(ifBlock, JcaCommon.getReturn());
            JcaObject ifExpress = JcaCommon.getIf(isViolate, ifBlock);
            jcaMethod.insertBlock(ifExpress);
        }
    }

    /**
     * 获取键
     *
     * @param jcaMethod 所在方法
     * @param frequency @Frequency注解
     * @return 返回键
     */
    private JcaObject getKey(JcaMethod jcaMethod, Frequency frequency) {
        String key = frequency.key();
        String value = frequency.value();
        if (CoreConstants.EMPTY.equals(value)) {
            if (CoreConstants.EMPTY.equals(key)) {
                throw new RuntimeException("@Frequency注解的key和value不能同时为空！");
            }

            if (jcaMethod.isStatic()) {
                return JcaCommon.method(jcaMethod.getJcaClass().getClassName(), key, jcaMethod.getArgs());
            }
            return JcaCommon.method("this", key, jcaMethod.getArgs());
        }

        String prefix = getPrefix();
        if (!CoreConstants.EMPTY.equals(prefix)) {
            value = prefix + ":" + value;
        }

        return new JcaObject(JcaExpressionUtil.parse(value));
    }

    /**
     * private static final MemoryFrequencyHandler fieldName = new MemoryFrequencyHandler();
     *
     * @param jcaClass  类
     * @param fieldName 字段名
     */
    private void createField(JcaClass jcaClass, String fieldName) {
        String handlerClass = MemoryFrequencyHandler.class.getName();
        JcaClass handler = getHandler(Handler.Type.FREQUENCY);
        if (handler != null) {
            handlerClass = handler.getFullName();
        }

        // new MemoryFrequencyHandler()
        JcaObject value = JcaCommon.instance(jcaClass, handlerClass);

        // private static final MemoryFrequencyHandler fieldName = value;
        JcaField jcaField = new JcaField(Flags.PRIVATE | Flags.STATIC | Flags.FINAL, handlerClass, fieldName, value);
        jcaClass.insert(jcaField);
    }

    /**
     * 获取注解句柄的前缀
     *
     * @return 返回注解句柄的前缀
     */
    private String getPrefix() {
        String prefix = PropertiesUtil.getProperty("frequency.prefix");
        Set<JcaClass> handlers = getJcaClasses(Handler.class);
        for (JcaClass handler : handlers) {
            Handler anno = handler.getClassSym().getAnnotation(Handler.class);
            if (anno.value() == Handler.Type.FREQUENCY) {
                // 优先使用@Handler注解, 默认值为""
                return anno.prefix();
            }
        }

        // 没有@Handler的情况，以配置文件jca.properties为准， 缺省为""
        return prefix;
    }

}
