package com.nbondarchuk.oauth2.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-14
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .maxAge(MAX_AGE_SECS)
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }
}