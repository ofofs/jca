package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Count;
import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.constants.CoreConstants;
import com.github.ofofs.jca.handler.impl.MemoryCountHandler;
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
 * 调用次数限制注解处理器
 *
 * @author kangyonggan
 * @since 6/23/18
 */
public class CountProcessor extends AbstractJcaProcessor {

    protected CountProcessor(RoundEnvironment env) {
        super(env);
    }

    @Override
    protected void process() {
        if (isEnable(Handler.Type.COUNT)) {
            String fieldName = Sequence.nextString("field");
            for (JcaMethod jcaMethod : getJcaMethods(Count.class)) {
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
     * if (memoryCountHandler.isViolate(key)) {return (ReturnType) cacheValue;}
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     */
    private void createIsViolate(JcaMethod jcaMethod, String fieldName) {
        Count count = jcaMethod.getMethod().getAnnotation(Count.class);

        // memoryCountHandler.isViolate(key, during, count)
        List<JcaObject> args = new ArrayList<>();
        // key
        args.add(getKey(jcaMethod, count));
        // during
        args.add(JcaCommon.getValue(count.during()));
        // count
        args.add(JcaCommon.getValue(count.count()));
        JcaObject isViolate = JcaCommon.method(fieldName, "isViolate", args);

        if (jcaMethod.hasReturnValue()) {
            // if (isViolate) {return (returnType) this.violate(args);}
            JcaObject ifBlock;
            if (jcaMethod.isStatic()) {
                ifBlock = JcaCommon.getReturn(JcaCommon.classCast(jcaMethod.getReturnType(), JcaCommon.method(jcaMethod.getJcaClass().getClassName(), count.violate(), jcaMethod.getArgs())));
            } else {
                ifBlock = JcaCommon.getReturn(JcaCommon.classCast(jcaMethod.getReturnType(), JcaCommon.method("this", count.violate(), jcaMethod.getArgs())));
            }
            JcaObject ifExpress = JcaCommon.getIf(isViolate, ifBlock);

            jcaMethod.insertBlock(ifExpress);
        } else {
            // 没返回值的情况
            // if (isViolate) {this.violate(args);return;}
            JcaObject ifBlock;
            if (jcaMethod.isStatic()) {
                ifBlock = JcaCommon.method(jcaMethod.getJcaClass().getClassName(), count.violate(), jcaMethod.getArgs());
            } else {
                ifBlock = JcaCommon.method("this", count.violate(), jcaMethod.getArgs());
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
     * @param count     @Count注解
     * @return 返回键
     */
    private JcaObject getKey(JcaMethod jcaMethod, Count count) {
        String key = count.key();
        String value = count.value();
        if (CoreConstants.EMPTY.equals(value)) {
            if (CoreConstants.EMPTY.equals(key)) {
                throw new RuntimeException("@Count注解的key和value不能同时为空！");
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
     * private static final MemoryCountHandler fieldName = new MemoryCountHandler();
     *
     * @param jcaClass  类
     * @param fieldName 字段名
     */
    private void createField(JcaClass jcaClass, String fieldName) {
        String handlerClass = MemoryCountHandler.class.getName();
        JcaClass handler = getHandler(Handler.Type.COUNT);
        if (handler != null) {
            handlerClass = handler.getFullName();
        }

        // new MemoryCountHandler()
        JcaObject value = JcaCommon.instance(jcaClass, handlerClass);

        // private static final MemoryCountHandler fieldName = value;
        JcaField jcaField = new JcaField(Flags.PRIVATE | Flags.STATIC | Flags.FINAL, handlerClass, fieldName, value);
        jcaClass.insert(jcaField);
    }

    /**
     * 获取注解句柄的前缀
     *
     * @return 返回注解句柄的前缀
     */
    private String getPrefix() {
        String prefix = PropertiesUtil.getProperty("count.prefix");
        Set<JcaClass> handlers = getJcaClasses(Handler.class);
        for (JcaClass handler : handlers) {
            Handler anno = handler.getClazz().getAnnotation(Handler.class);
            if (anno.value() == Handler.Type.COUNT) {
                // 优先使用@Handler注解, 默认值为""
                return anno.prefix();
            }
        }

        // 没有@Handler的情况，以配置文件jca.properties为准， 缺省为""
        return prefix;
    }

}
