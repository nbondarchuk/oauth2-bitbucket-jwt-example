package com.nbondarchuk.oauth2.server.service;

import com.nbondarchuk.oauth2.server.model.entity.User;

import java.util.Optional;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-05
 */
public interface UserService {

    User create(User user);

    Optional<User> findByLogin(String login);
}
