package com.nbondarchuk.oauth2.server.service;

import com.nbondarchuk.oauth2.server.model.entity.RefreshToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-13
 */
public class InMemoryTokenStore implements TokenStore {

    private final Map<String, RefreshToken> store = new ConcurrentHashMap<>();

    @Override
    public RefreshToken store(RefreshToken token) {
        store.put(token.getId(), token);
        return token;
    }

    @Override
    public RefreshToken find(String tokenId) {
        return store.get(tokenId);
    }
}
