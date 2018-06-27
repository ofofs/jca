/*
 * Copyright (c)  2018. ofofs Inc.
 * jca All rights reserved.
 */

package com.github.ofofs.jca.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p> 用于标识 API 的信息 </p>
 *
 * 本类的所有权属于：apiguardian-api
 *
 * <pre> Created: 2018/6/27 上午10:26  </pre>
 * <pre> Project: paradise  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0, 2018-06-27 10:27:09
 */
@Target({ TYPE, METHOD, CONSTRUCTOR, FIELD })
@Retention(RUNTIME)
@Documented
public @interface API {

    /**
     * The current {@linkplain Status status} of the API.
     */
    Status status();

    /**
     * The version of the API when the {@link #status} was last changed.
     *
     * <p>Defaults to an empty string, signifying that the <em>since</em>
     * version is unknown.
     */
    String since() default "";

    /**
     * List of packages belonging to intended consumers.
     *
     * <p>The supplied packages can be fully qualified package names or
     * patterns containing asterisks that will be used as wildcards.
     *
     * <p>Defaults to {@code "*"}, signifying that the API is intended to be
     * consumed by any package.
     */
    String[] consumers() default "*";

    /**
     * Indicates the status of an API element and therefore its level of
     * stability as well.
     */
    enum Status {

        /**
         * Must not be used by any external code. Might be removed without prior
         * notice.
         */
        INTERNAL,

        /**
         * Should no longer be used. Might disappear in the next minor release.
         */
        DEPRECATED,

        /**
         * Intended for new, experimental features where the publisher of the
         * API is looking for feedback.
         *
         * <p>Use with caution. Might be promoted to {@link #MAINTAINED} or
         * {@link #STABLE} in the future, but might also be removed without
         * prior notice.
         */
        EXPERIMENTAL,

        /**
         * Intended for features that will not be changed in a backwards-
         * incompatible way for at least the next minor release of the current
         * major version. If scheduled for removal, such a feature will be
         * demoted to {@link #DEPRECATED} first.
         */
        MAINTAINED,

        /**
         * Intended for features that will not be changed in a backwards-
         * incompatible way in the current major version.
         */
        STABLE
        ;
    }

}
