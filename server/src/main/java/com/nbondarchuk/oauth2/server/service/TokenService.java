package com.nbondarchuk.oauth2.server.service;

import com.nbondarchuk.oauth2.server.model.entity.RefreshToken;
import com.nbondarchuk.oauth2.server.model.entity.User;
import lombok.Builder;
import lombok.Data;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
public interface TokenService {

    @Data
    @Builder
    class UserContext {

        private String name;

        private String login;

        public static UserContext of(User user) {
            return UserContext.builder()
                    .name(user.getName())
                    .login(user.getLogin())
                    .build();
        }
    }

    String getUsername(String token);

    String newAccessToken(UserContext ctx);

    boolean isValidAccessToken(String token);

    RefreshToken newRefreshToken(UserContext ctx);

    RefreshToken findRefreshToken(String tokenId);

    boolean isValidRefreshToken(RefreshToken token);
}
