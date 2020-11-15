package com.nbondarchuk.oauth2.server.service;

import com.nbondarchuk.oauth2.server.config.props.model.TokenProperties;
import com.nbondarchuk.oauth2.server.model.entity.RefreshToken;
import com.nbondarchuk.oauth2.server.model.entity.User;
import com.nbondarchuk.oauth2.server.utils.JwtUtils.TokenData;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;

import static com.nbondarchuk.oauth2.server.utils.JwtUtils.buildToken;
import static com.nbondarchuk.oauth2.server.utils.JwtUtils.parseToken;
import static java.lang.String.format;
import static java.time.Instant.now;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
public class TokenServiceImpl implements TokenService {

    private final TokenStore tokenStore;

    private final UserService userService;

    private final TokenProperties tokenProperties;

    public TokenServiceImpl(
            TokenStore tokenStore,
            UserService userService,
            TokenProperties tokenProperties) {
        this.tokenStore = tokenStore;
        this.userService = userService;
        this.tokenProperties = tokenProperties;
    }

    @Override
    public String getUsername(String token) {
        return parseToken(token, tokenProperties.getSecret()).getSubject();
    }

    @Override
    public String newAccessToken(UserContext ctx) {
        return buildToken(TokenData.builder()
                .userName(ctx.getName())
                .subject(ctx.getLogin())
                .secret(tokenProperties.getSecret())
                .validityMillis(tokenProperties.getAccessTokenValidityMillis())
                .build());
    }

    @Override
    public boolean isValidAccessToken(String token) {
        try {
            parseToken(token, tokenProperties.getSecret());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public RefreshToken newRefreshToken(UserContext ctx) {
        User user = userService
                .findByLogin(ctx.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException(format("User %s not found.", ctx.getLogin())));

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setId(randomAlphanumeric(64));
        token.setExpiryTime(new Date(System.currentTimeMillis() + tokenProperties.getRefreshTokenValidityMillis()));

        return tokenStore.store(token);
    }

    public RefreshToken findRefreshToken(String tokenId) {
        return tokenStore.find(tokenId);
    }

    public boolean isValidRefreshToken(RefreshToken token) {
        return !now().isAfter(token.getExpiryTime().toInstant());
    }
}
