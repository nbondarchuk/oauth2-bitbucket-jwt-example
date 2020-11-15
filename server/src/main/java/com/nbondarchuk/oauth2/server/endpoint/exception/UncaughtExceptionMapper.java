package com.nbondarchuk.oauth2.server.endpoint.exception;

import com.nbondarchuk.oauth2.server.model.dto.ErrorDto;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable t) {
        return Response.serverError().entity(ErrorDto.of(t)).type(APPLICATION_JSON_TYPE).build();
    }
}