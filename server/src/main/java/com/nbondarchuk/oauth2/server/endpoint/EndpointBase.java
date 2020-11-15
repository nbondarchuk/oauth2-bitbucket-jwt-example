package com.nbondarchuk.oauth2.server.endpoint;

import org.apache.logging.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import java.util.concurrent.Callable;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
public abstract class EndpointBase {

    protected abstract Logger getLogger();

    protected <T> T handle(Callable<T> callable) {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            getLogger().error("Error processing request.", e);
            throw e;
        } catch (Exception e) {
            getLogger().error("Error processing request.", e);
            throw new WebApplicationException(e);
        }
    }
}
