package com.github.ofofs.jca.unit.util;

import org.apiguardian.api.API;

/**
 * <p> 数组工具类 </p>
 *
 * <pre> Created: 2018-06-29 13:22  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
@API(status = API.Status.INTERNAL)
public final class ArrayUtil {

    private ArrayUtil(){}

    /**
     * 数组是否为空
     * @param objects 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final Object[] objects) {
        return null == objects
                || objects.length == 0;
    }

}
