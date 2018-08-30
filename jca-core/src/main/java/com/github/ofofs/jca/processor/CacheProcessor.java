package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Cache;
import com.github.ofofs.jca.annotation.CacheDel;
import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.constants.JcaConstants;
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
                if (JcaConstants.RETURN_VOID.equals(jcaMethod.getReturnType().getExpression().toString())) {
                    continue;
                }
                process(jcaMethod, fieldName);
            }
            // 生成的代码是插入到第一行的，因此是先删除缓存，如果有@Cache再保存缓存
            for (JcaMethod jcaMethod : getJcaMethods(CacheDel.class)) {
                processDel(jcaMethod, fieldName);
            }
        }
    }

    /**
     * 处理每一个方法（删除缓存）
     * memoryCacheHandler.delete(key);
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     */
    private void processDel(JcaMethod jcaMethod, String fieldName) {
        // 给方法所在的类创建一个字段
        createField(jcaMethod.getJcaClass(), fieldName);

        // key
        String key = jcaMethod.getMethodSym().getAnnotation(CacheDel.class).value();
        String prefix = getPrefix();
        if (!"".equals(prefix)) {
            key = prefix + ":" + key;
        }

        List<JcaObject> args = new ArrayList<>();
        args.add(new JcaObject(JcaExpressionUtil.parse(key)));

        // memoryCacheHandler.delete(key)
        jcaMethod.insert(JcaCommon.method(fieldName, "delete", args));
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

        // key
        String key = jcaMethod.getMethodSym().getAnnotation(Cache.class).value();
        String prefix = getPrefix();
        if (!"".equals(prefix)) {
            key = prefix + ":" + key;
        }

        // 在return前创建代码块
        createAfter(jcaMethod, fieldName, key);

        // 在方法第一行创建代码块
        createBefore(jcaMethod, fieldName, key);
    }

    /**
     * return (ReturnType) memoryCacheHandler.set(key, returnValue, expire)
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     * @param key       缓存的键
     */
    private void createAfter(JcaMethod jcaMethod, String fieldName, String key) {
        new JcaMethod(jcaMethod.getMethodSym()) {
            @Override
            public JcaObject onReturn(JcaObject returnValue) {
                List<JcaObject> args = new ArrayList<>();
                // key
                args.add(new JcaObject(JcaExpressionUtil.parse(key)));
                // returnValue
                args.add(new JcaObject(returnValue.getExpression()));
                // expire
                args.add(JcaCommon.getValue(getExpire(jcaMethod)));

                JcaObject express = JcaCommon.method(fieldName, "set", args);
                // 替换原来的返回值
                returnValue.setExpression(JcaCommon.classCast(jcaMethod.getReturnType(), express).getExpression());
                return returnValue;
            }
        }.visitReturn();
    }

    /**
     * Object cacheValue = memoryCacheHandler.get(key);
     * if (cacheValue != null) {return (ReturnType) cacheValue;}
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     * @param key       缓存的键
     */
    private void createBefore(JcaMethod jcaMethod, String fieldName, String key) {
        List<JcaObject> args = new ArrayList<>();
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
            Handler anno = handler.getClassSym().getAnnotation(Handler.class);
            if (anno.value() == Handler.Type.CACHE) {
                // 优先使用@Handler注解, 默认值为""
                return anno.prefix();
            }
        }

        // 没有@Handler的情况，以配置文件jca.properties为准， 缺省为""
        return prefix;
    }

    /**
     * 获取缓存的失效时间
     *
     * @param jcaMethod 所在的方法
     * @return 返回缓存的失效时间
     */
    public long getExpire(JcaMethod jcaMethod) {
        // 优先使用@Cache的失效时间
        long expire = jcaMethod.getMethodSym().getAnnotation(Cache.class).expire();
        if (expire != -1) {
            return expire;
        }

        // 次优先使用@Handler的失效时间
        Set<JcaClass> handlers = getJcaClasses(Handler.class);
        for (JcaClass handler : handlers) {
            Handler anno = handler.getClassSym().getAnnotation(Handler.class);
            if (anno.value() == Handler.Type.CACHE) {
                expire = anno.expire();
                if (expire != -1) {
                    return expire;
                }
            }
        }

        // 最后使用jca.properties的失效时间(默认30分钟)
        expire = Long.parseLong(PropertiesUtil.getProperty("cache.expire", "1800000"));
        return expire;
    }
}
