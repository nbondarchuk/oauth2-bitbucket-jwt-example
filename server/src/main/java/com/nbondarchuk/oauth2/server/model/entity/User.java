package com.nbondarchuk.oauth2.server.model.entity;

import lombok.Data;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-05
 */
@Data
public class User {

    private Long id;

    private String login;

    private String name;

    private String provider;
}
