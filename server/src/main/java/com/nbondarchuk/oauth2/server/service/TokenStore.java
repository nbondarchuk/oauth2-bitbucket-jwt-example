package com.nbondarchuk.oauth2.server.service;

import com.nbondarchuk.oauth2.server.model.entity.RefreshToken;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-13
 */
public interface TokenStore {

    RefreshToken find(String tokenId);

    RefreshToken store(RefreshToken token);
}
