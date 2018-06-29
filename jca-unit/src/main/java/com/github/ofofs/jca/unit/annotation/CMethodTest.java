package com.github.ofofs.jca.unit.annotation;

import org.apiguardian.api.API;
import org.junit.jupiter.api.TestTemplate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p> Util 方法注解父类 </p>
 *
 * <pre> Created: 2018-06-29 09:42  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TestTemplate
@API(status = API.Status.INTERNAL)
public @interface CMethodTest {
}
