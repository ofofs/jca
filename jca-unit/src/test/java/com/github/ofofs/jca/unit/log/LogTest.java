package com.github.ofofs.jca.unit.log;

import com.github.ofofs.jca.handler.LogHandler;
import com.github.ofofs.jca.unit.annotation.CLog;

/**
 * <p> 日志测试 </p>
 *
 * <pre> Created: 2018-06-29 14:23  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
public class LogTest {

    private static final LogHandler field100003491619053570 = new DefaultLogHandler();

    @CLog
    public void logOneTest() {
    }

    @CLog
    public void logTwoTest() {
    }

}
