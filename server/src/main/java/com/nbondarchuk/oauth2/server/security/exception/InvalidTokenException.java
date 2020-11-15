package com.nbondarchuk.oauth2.server.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-13
 */
public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(String msg) {
        super(msg);
    }
}
