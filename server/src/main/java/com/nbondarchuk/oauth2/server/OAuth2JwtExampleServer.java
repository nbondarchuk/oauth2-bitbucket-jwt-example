package com.nbondarchuk.oauth2.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-05
 */
@SpringBootApplication
public class OAuth2JwtExampleServer {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2JwtExampleServer.class, args);
    }
}
