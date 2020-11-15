package com.nbondarchuk.oauth2.server.config;

import com.nbondarchuk.oauth2.server.config.props.model.TokenProperties;
import com.nbondarchuk.oauth2.server.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
@Configuration
public class ServiceLayerConfig {

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    public TokenService tokenService(
            TokenStore tokenStore,
            UserService userService,
            TokenProperties tokenProperties) {
        return new TokenServiceImpl(tokenStore, userService, tokenProperties);
    }

    @Bean
    public BitbucketService bitbucketService(Supplier<RestTemplate> restTemplate) {
        return new BitbucketServiceImpl(restTemplate);
    }
}
