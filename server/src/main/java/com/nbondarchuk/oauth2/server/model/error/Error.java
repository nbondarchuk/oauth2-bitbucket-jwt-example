package com.nbondarchuk.oauth2.server.model.error;

import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
@Data
public class Error {

    private String message;

    private String stacktrace;

    public static Error of(Throwable t) {
        Error error = new Error();
        error.setMessage(t.getMessage());
        error.setStacktrace(ExceptionUtils.getStackTrace(t));
        return error;
    }
}
