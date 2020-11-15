package com.nbondarchuk.oauth2.server.endpoint;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
@Path("/error")
public class ErrorEndpoint {

    private static final String PATH_ATTR = "path";

    private static final String ERROR_ATTR = "error";

    private static final String STATUS_ATTR = "status";

    private static final String MESSAGE_ATTR = "message";

    private static final String TIMESTAMP_ATTR = "timestamp";

    @Context
    private HttpServletRequest request;

    @GET
    @Produces(APPLICATION_JSON)
    public Response handleGetError() {
        return handleError();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response handlePostError() {
        return handleError();
    }

    private Response handleError() {
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put(TIMESTAMP_ATTR, new Date());
        addPath(errorAttributes, webRequest);
        addStatus(errorAttributes, webRequest);
        addErrorDetails(errorAttributes, webRequest);

        return Response.status(getStatus(request)).entity(errorAttributes).build();
    }

    private Status getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return Status.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return Status.fromStatusCode(statusCode);
            } catch (Exception ex) {
                return Status.INTERNAL_SERVER_ERROR;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    private void addErrorDetails(Map<String, Object> errorAttributes, WebRequest webRequest) {
        Object message = getAttribute(webRequest, "javax.servlet.error.message");
        if ((!StringUtils.isEmpty(message) || errorAttributes.get(MESSAGE_ATTR) == null)) {
            errorAttributes.put(MESSAGE_ATTR, StringUtils.isEmpty(message) ? "No message available" : message);
        }
    }

    private void addPath(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
        String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
        if (path != null) {
            errorAttributes.put(PATH_ATTR, path);
        }
    }

    private void addStatus(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
        Integer status = getAttribute(requestAttributes, "javax.servlet.error.status_code");
        if (status == null) {
            errorAttributes.put(STATUS_ATTR, 999);
            errorAttributes.put(ERROR_ATTR, "None");
            return;
        }
        errorAttributes.put(STATUS_ATTR, status);
        try {
            errorAttributes.put(ERROR_ATTR, HttpStatus.valueOf(status).getReasonPhrase());
        } catch (Exception ex) {
            // Unable to obtain a reason.
            errorAttributes.put(ERROR_ATTR, "Http Status " + status);
        }
    }
}
