package com.github.ofofs.jca.unit.annotation;

import com.github.ofofs.jca.unit.provider.LogMethodProvider;

import org.apiguardian.api.API;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p> Log 方法测试注解 </p>
 *
 * <pre> Created: 2018-06-29 09:42  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(LogMethodProvider.class)
@CMethodTest
@API(status = API.Status.EXPERIMENTAL)
public @interface CLog {
}
