package com.nbondarchuk.oauth2.server.config;

import com.nbondarchuk.oauth2.server.endpoint.ApiEndpoint;
import com.nbondarchuk.oauth2.server.endpoint.AuthEndpoint;
import com.nbondarchuk.oauth2.server.endpoint.ErrorEndpoint;
import com.nbondarchuk.oauth2.server.endpoint.exception.AccessDeniedExceptionMapper;
import com.nbondarchuk.oauth2.server.endpoint.exception.UncaughtExceptionMapper;
import com.nbondarchuk.oauth2.server.endpoint.exception.WebApplicationExceptionMapper;
import com.nbondarchuk.oauth2.server.security.AuthenticationFacade;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.function.Supplier;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;
import static org.glassfish.jersey.logging.LoggingFeature.DEFAULT_LOGGER_NAME;
import static org.glassfish.jersey.logging.LoggingFeature.DEFAULT_MAX_ENTITY_SIZE;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
@Configuration
public class RestConfig extends ResourceConfig {

    @PostConstruct
    public void init() {
        registerFeatures();
        registerEndpoints();
        registerExceptionMappers();
    }

    @Bean
    public ApiEndpoint apiEndpoint() {
        return new ApiEndpoint();
    }

    @Bean
    public AuthEndpoint authEndpoint() {
        return new AuthEndpoint();
    }

    @Bean
    @Autowired
    public Supplier<RestTemplate> restTemplateFactory(AuthenticationFacade authenticationFacade) {
        return () -> {
            RestTemplate restTemplate = new RestTemplate();
            String accessToken = authenticationFacade.getAccessToken();
            if (accessToken == null) {
                restTemplate.getInterceptors().add(getNoTokenInterceptor());
            } else {
                restTemplate.getInterceptors().add(getBearerTokenInterceptor(accessToken));
            }
            return restTemplate;
        };
    }

    private void registerFeatures() {
        register(new LoggingFeature(getLogger(DEFAULT_LOGGER_NAME), Level.INFO, PAYLOAD_ANY, DEFAULT_MAX_ENTITY_SIZE));
    }

    private void registerEndpoints() {
        register(ApiEndpoint.class);
        register(AuthEndpoint.class);
        register(ErrorEndpoint.class);
    }

    private void registerExceptionMappers() {
        register(UncaughtExceptionMapper.class);
        register(AccessDeniedExceptionMapper.class);
        register(WebApplicationExceptionMapper.class);
    }

    private ClientHttpRequestInterceptor getNoTokenInterceptor() {
        return (request, bytes, execution) -> {
            throw new IllegalStateException(
                    "Can't access the API without an access token");
        };
    }

    private ClientHttpRequestInterceptor getBearerTokenInterceptor(String accessToken) {
        return (request, bytes, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + accessToken);
            return execution.execute(request, bytes);
        };
    }
}
