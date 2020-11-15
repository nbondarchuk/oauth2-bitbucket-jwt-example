package com.nbondarchuk.oauth2.server.model.entity;

import lombok.Data;

import java.time.Duration;
import java.util.Date;

import static java.time.Instant.now;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-13
 */
@Data
public class RefreshToken {

    private String id;

    private User user;

    private Date expiryTime;

    public long getValiditySeconds() {
        return expiryTime == null
                ? Long.MAX_VALUE
                : Duration.between(now(), expiryTime.toInstant()).getSeconds();
    }
}
