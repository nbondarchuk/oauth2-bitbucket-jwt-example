package com.nbondarchuk.oauth2.server.security;

import com.nbondarchuk.oauth2.server.config.props.model.AuthProperties;
import com.nbondarchuk.oauth2.server.model.entity.RefreshToken;
import com.nbondarchuk.oauth2.server.service.TokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.nbondarchuk.oauth2.server.utils.Cookies.*;
import static java.util.Objects.requireNonNull;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
@Log4j2
public class ExampleAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;

    private final AuthProperties authProperties;

    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    public ExampleAuthenticationSuccessHandler(
            TokenService tokenService,
            AuthProperties authProperties,
            HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
        this.tokenService = requireNonNull(tokenService);
        this.authProperties = requireNonNull(authProperties);
        this.authorizationRequestRepository = requireNonNull(authorizationRequestRepository);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Logged in user {}", authentication.getPrincipal());
        super.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = getCookie(request, REDIRECT_URI).map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Received unauthorized redirect URI.");
        }

        return UriComponentsBuilder.fromUriString(redirectUri.orElse(getDefaultTargetUrl()))
                .queryParam("token", tokenService.newAccessToken(toUserContext(authentication)))
                .build().toUriString();
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        redirectToTargetUrl(request, response, authentication);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return authProperties.getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to.
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }

    private TokenService.UserContext toUserContext(Authentication authentication) {
        ExampleOAuth2User principal = (ExampleOAuth2User) authentication.getPrincipal();
        return TokenService.UserContext.builder()
                .login(principal.getName())
                .name(principal.getFullName())
                .build();
    }

    private void addRefreshTokenCookie(HttpServletResponse response, Authentication authentication) {
        RefreshToken token = tokenService.newRefreshToken(toUserContext(authentication));
        addCookie(response, REFRESH_TOKEN, token.getId(), (int) token.getValiditySeconds());
    }

    private void redirectToTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        addRefreshTokenCookie(response, authentication);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
