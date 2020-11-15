package com.nbondarchuk.oauth2.server.config.props.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-07-07
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "token")
public class TokenProperties {

    private String secret;

    private long accessTokenValidityMillis;

    private long refreshTokenValidityMillis;
}
