package com.nbondarchuk.oauth2.server.config.props.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private List<String> authorizedRedirectUris;
}
