package com.nbondarchuk.oauth2.server.security;

import com.nbondarchuk.oauth2.server.utils.Cookies;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.nbondarchuk.oauth2.server.utils.Cookies.getCookie;
import static java.util.Objects.requireNonNull;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
public class ExampleAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    public ExampleAuthenticationFailureHandler(
            HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
        this.authorizationRequestRepository = requireNonNull(authorizationRequestRepository);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String targetUrl = getFailureUrl(request, exception);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private String getFailureUrl(HttpServletRequest request, AuthenticationException exception) {
        String targetUrl = getCookie(request, Cookies.REDIRECT_URI)
                .map(Cookie::getValue)
                .orElse(("/"));

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();
    }
}
