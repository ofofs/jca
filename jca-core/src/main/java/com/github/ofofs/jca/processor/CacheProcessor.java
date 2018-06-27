package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Cache;
import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.handler.impl.MemoryCacheHandler;
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
 * 缓存注解处理器
 *
 * @author kangyonggan
 * @since 6/22/18
 */
public class CacheProcessor extends AbstractJcaProcessor {

    CacheProcessor(RoundEnvironment env) {
        super(env);
    }

    /**
     * 处理缓存注解
     */
    @Override
    protected void process() {
        if (isEnable(Handler.Type.CACHE)) {
            String fieldName = Sequence.nextString("field");
            for (JcaMethod jcaMethod : getJcaMethods(Cache.class)) {
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

        // 在方法第一行创建代码块
        createBefore(jcaMethod, fieldName);
    }

    /**
     * Object cacheValue = memoryCacheHandler.get(key);
     * if (cacheValue != null) {return (returnType) cacheValue;}
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     */
    private void createBefore(JcaMethod jcaMethod, String fieldName) {
        List<JcaObject> args = new ArrayList<>();
        // key
        String key = jcaMethod.getMethod().getAnnotation(Cache.class).value();
        String prefix = getPrefix();
        if (!"".equals(prefix)) {
            key = prefix + ":" + key;
        }
        args.add(new JcaObject(JcaExpressionUtil.parse(key)));

        // Object cacheValue = memoryCacheHandler.get(key)
        String varName = Sequence.nextString("var");
        JcaVariable jcaVariable = new JcaVariable(Object.class, varName, JcaCommon.method(fieldName, "get", args));

        // if (cacheValue != null) {return (returnType) cacheValue;}
        JcaObject ifBlock = JcaCommon.getReturn(JcaCommon.classCast(jcaMethod.getReturnType(), JcaCommon.getVar(varName)));
        JcaObject ifExpress = JcaCommon.getIf(JcaCommon.notNull(varName), ifBlock);

        jcaMethod.insertBlock(ifExpress);
        jcaMethod.insert(jcaVariable);
    }

    /**
     * private static final MemoryCacheHandler fieldName = new MemoryCacheHandler();
     *
     * @param jcaClass  类
     * @param fieldName 字段名
     */
    private void createField(JcaClass jcaClass, String fieldName) {
        String handlerClass = MemoryCacheHandler.class.getName();
        JcaClass handler = getHandler(Handler.Type.CACHE);
        if (handler != null) {
            handlerClass = handler.getFullName();
        }

        // new MemoryCacheHandler()
        JcaObject value = JcaCommon.instance(jcaClass, handlerClass);

        // private static final MemoryCacheHandler fieldName = value;
        JcaField jcaField = new JcaField(Flags.PRIVATE | Flags.STATIC | Flags.FINAL, handlerClass, fieldName, value);
        jcaClass.insert(jcaField);
    }

    /**
     * 获取注解句柄的前缀
     *
     * @return 返回注解句柄的前缀
     */
    private String getPrefix() {
        String prefix = PropertiesUtil.getProperty("cache.prefix");
        Set<JcaClass> handlers = getJcaClasses(Handler.class);
        for (JcaClass handler : handlers) {
            Handler anno = handler.getClazz().getAnnotation(Handler.class);
            if (anno.value() == Handler.Type.CACHE) {
                // 优先使用@Handler注解, 默认值为""
                return anno.prefix();
            }
        }

        // 没有@Handler的情况，以配置文件jca.properties为准， 缺省为""
        return prefix;
    }
}
