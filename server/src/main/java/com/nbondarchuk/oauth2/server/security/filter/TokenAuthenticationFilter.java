package com.nbondarchuk.oauth2.server.security.filter;

import com.nbondarchuk.oauth2.server.model.entity.User;
import com.nbondarchuk.oauth2.server.security.ExampleOAuth2User;
import com.nbondarchuk.oauth2.server.service.TokenService;
import com.nbondarchuk.oauth2.server.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-06-28
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    private final TokenService tokenService;

    public TokenAuthenticationFilter(
            UserService userService, TokenService tokenService) {
        this.userService = requireNonNull(userService);
        this.tokenService = requireNonNull(tokenService);
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) throws ServletException, IOException {
        try {
            Optional<String> jwtOpt = getJwtFromRequest(request);
            if (jwtOpt.isPresent()) {
                String jwt = jwtOpt.get();
                if (isNotEmpty(jwt) && tokenService.isValidAccessToken(jwt)) {
                    String login = tokenService.getUsername(jwt);
                    Optional<User> userOpt = userService.findByLogin(login);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        ExampleOAuth2User oAuth2User = new ExampleOAuth2User(user);
                        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), oAuth2User.getProvider());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Could not set user authentication in security context", e);
        }

        chain.doFilter(request, response);
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (isNotEmpty(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Optional.ofNullable(token);
    }
}
