package com.nbondarchuk.oauth2.server.config.props;

import com.nbondarchuk.oauth2.server.config.props.model.AuthProperties;
import com.nbondarchuk.oauth2.server.config.props.model.TokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
@Configuration
@EnableConfigurationProperties({
        AuthProperties.class,
        TokenProperties.class
})
public class PropertiesConfig {
}
