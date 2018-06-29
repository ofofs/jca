package com.github.ofofs.jca.unit.exception;

import org.apiguardian.api.API;

/**
 * <p> CUnit 运行时异常 </p>
 *
 * <pre> Created: 2018-06-29 10:04  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
@API(status = API.Status.INTERNAL)
public class CUnitRuntimeException extends RuntimeException{

    private static final long serialVersionUID = 8912350629993895207L;

    public CUnitRuntimeException() {
    }

    public CUnitRuntimeException(String message) {
        super(message);
    }

    public CUnitRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CUnitRuntimeException(Throwable cause) {
        super(cause);
    }

    public CUnitRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
