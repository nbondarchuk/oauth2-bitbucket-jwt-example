package com.nbondarchuk.oauth2.server.endpoint.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException e) {
        return e.getResponse();
    }
}
