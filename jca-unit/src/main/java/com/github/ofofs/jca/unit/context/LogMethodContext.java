package com.github.ofofs.jca.unit.context;

import com.github.ofofs.jca.handler.LogHandler;
import com.github.ofofs.jca.unit.exception.CUnitRuntimeException;
import com.github.ofofs.jca.unit.util.ArrayUtil;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * <p> Log 方法上下文 </p>
 *
 * <pre> Created: 2018-06-29 13:50  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
public class LogMethodContext implements TestTemplateInvocationContext {

    private final Method method;
    private final Class clazz;

    public LogMethodContext(ExtensionContext context) {
        this.method = context.getRequiredTestMethod();
        this.clazz = method.getDeclaringClass();
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return method.getName();
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(
                (BeforeTestExecutionCallback) context -> {
                    validClass();
                    validMethod();
                }
        );
    }

    /**
     * 校验方法体
     */
    private void validMethod() {
        //TODO: 直接获取 maven/target 下的 类文件，然后解析？
    }

    /**
     * 测试包含属性
     */
    private void validClass() {
        //1. 校验字段信息
        boolean containsField = containsLogHandlerField();
        if(!containsField) {
            throw new CUnitRuntimeException("Log must have fields!");
        }
    }

    /**
     * 是否包含目标志处理器字段
     * @return 是否
     */
    @SuppressWarnings("unchecked")
    private boolean containsLogHandlerField() {
        Field[] fields = clazz.getDeclaredFields();
        if(ArrayUtil.isEmpty(fields)) {
            return false;
        }

        final String regex = "^field[0-9]{16,}$";
        for(Field field : fields) {
            final String name = field.getName();
            final Class clazz = field.getType();
            if(clazz.isAssignableFrom(LogHandler.class)
                    && name.matches(regex)) {
                return true;
            }
        }
        return false;
    }

}
