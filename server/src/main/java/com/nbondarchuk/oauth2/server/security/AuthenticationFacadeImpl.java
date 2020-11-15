package com.nbondarchuk.oauth2.server.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import static java.util.Objects.requireNonNull;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-10
 */
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    private final OAuth2AuthorizedClientService clientService;

    public AuthenticationFacadeImpl(
            OAuth2AuthorizedClientService clientService) {
        this.clientService = requireNonNull(clientService);
    }

    @Override
    public String getAccessToken() {
        return getAuthorizedClient().getAccessToken().getTokenValue();
    }

    private OAuth2AuthorizedClient getAuthorizedClient() {
        Authentication authentication = getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        return clientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
