package com.nbondarchuk.oauth2.server.endpoint;

import com.nbondarchuk.oauth2.server.model.entity.Repository;
import com.nbondarchuk.oauth2.server.service.BitbucketService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
@Log4j2
@Path("/api")
public class ApiEndpoint extends EndpointBase {

    @Autowired
    private BitbucketService bitbucketService;

    @GET
    @Path("/repositories")
    @Produces(APPLICATION_JSON)
    public List<Repository> getRepositories() {
        return handle(bitbucketService::getRepositories);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
