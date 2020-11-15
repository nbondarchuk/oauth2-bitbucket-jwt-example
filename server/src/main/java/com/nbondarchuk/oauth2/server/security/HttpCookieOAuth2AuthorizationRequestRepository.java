package com.nbondarchuk.oauth2.server.security;

import com.nbondarchuk.oauth2.server.shared.QueryParams;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

import static com.nbondarchuk.oauth2.server.utils.Cookies.*;
import static com.nimbusds.oauth2.sdk.util.StringUtils.isNotBlank;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-06
 */
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final int COOKIE_EXPIRE_SECONDS = 180;

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "OAUTH2-AUTH-REQUEST";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
        String redirectUriAfterLogin = request.getParameter(QueryParams.REDIRECT_URI);
        if (isNotBlank(redirectUriAfterLogin)) {
            addCookie(response, REDIRECT_URI, redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        deleteCookie(request, response, REDIRECT_URI);
    }

    private static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> T deserialize(Cookie cookie, Class<T> clazz) {
        return clazz.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
