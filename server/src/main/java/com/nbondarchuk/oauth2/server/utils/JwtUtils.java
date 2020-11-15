package com.nbondarchuk.oauth2.server.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
public class JwtUtils {

    @Data
    @Builder
    public static class TokenData {

        private String secret;

        private String subject;

        private String userName;

        private long validityMillis;
    }

    private JwtUtils() {
    }

    public static String buildToken(TokenData data) {
        LocalDateTime iat = LocalDateTime.now();
        LocalDateTime exp = iat.plusNanos(MILLISECONDS.toNanos(data.getValidityMillis()));
        return Jwts.builder()
                .setSubject(data.getSubject())
                .claim("username", data.getUserName())
                .signWith(SignatureAlgorithm.HS512, data.getSecret())
                .setIssuedAt(Date.from(iat.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(exp.atZone(ZoneId.systemDefault()).toInstant()))
                .compact();
    }

    public static Claims parseToken(String token, String secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
