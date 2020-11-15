package com.nbondarchuk.oauth2.client.model;

import lombok.Data;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-14
 */
@Data
public class Tokens {

    private String accessToken;

    private String refreshToken;

    public static Tokens of(String accessToken, String refreshToken) {
        Tokens tokens = new Tokens();
        tokens.accessToken = accessToken;
        tokens.refreshToken = refreshToken;
        return tokens;
    }
}
