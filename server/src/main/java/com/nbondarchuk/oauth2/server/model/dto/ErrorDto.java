package com.nbondarchuk.oauth2.server.model.dto;

import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
@Data
public class ErrorDto {

    private String message;

    private String stacktrace;

    public static ErrorDto of(Throwable t) {
        ErrorDto error = new ErrorDto();
        error.setMessage(t.getMessage());
        error.setStacktrace(ExceptionUtils.getStackTrace(t));
        return error;
    }
}
