package com.github.ofofs.jca.frequency;

import com.github.ofofs.jca.annotation.Frequency;

/**
 * @author kangyonggan
 * @since 6/29/18
 */
public class FrequencyTest {


    @Frequency(value = "user:${name}", interval = 1000, violate = "getUsernameViolate")
    public String getUsername(String name) {
        return name;
    }


    @Frequency(key = "getKey", interval = 1000, violate = "getUsernameViolate")
    public String getUsername2(String name) {
        return name;
    }

    public String getKey(String name) {
        return "ip";
    }

    public static String getUsernameViolate(String name) {
        return "error";
    }


}
