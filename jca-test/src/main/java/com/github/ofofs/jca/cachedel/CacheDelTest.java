package com.github.ofofs.jca.cachedel;

import com.github.ofofs.jca.annotation.CacheDel;

/**
 * @author kangyonggan
 * @since 6/27/18
 */
public class CacheDelTest {

    @CacheDel("name:${name}")
    public void getName(String name) {
        System.out.println(name);
    }

}
