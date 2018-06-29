package com.github.ofofs.jca.unit.provider;

import com.github.ofofs.jca.unit.exception.CUnitRuntimeException;
import com.github.ofofs.jca.unit.util.ArrayUtil;

import org.apiguardian.api.API;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * <p> CUtil 实现 </p>
 *
 * <pre> Created: 2018-06-29 09:52  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
@API(status = API.Status.EXPERIMENTAL)
public class UtilClassProvider implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        Class clazz = extensionContext.getRequiredTestClass();
        validClass(clazz);
        validConstructor(clazz);
    }

    /**
     * 测试 final class
     *
     * @param clazz 类信息
     */
    private void validClass(final Class clazz) {
        int modifiers = clazz.getModifiers();
        if (!Modifier.isFinal(modifiers)) {
            throw new CUnitRuntimeException("Util class must be final!");
        }
    }

    /**
     * 测试无参数构造器
     *
     * @param clazz 类信息
     */
    private void validConstructor(Class clazz) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();
            if (ArrayUtil.isEmpty(parameters)) {
                int modifiers = constructor.getModifiers();
                if (!Modifier.isPrivate(modifiers)) {
                    throw new CUnitRuntimeException("Util class no-arg consctor must be private!");
                }
            }
        }
    }

}
