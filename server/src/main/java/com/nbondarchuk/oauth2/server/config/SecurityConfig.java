package com.nbondarchuk.oauth2.server.config;

import com.nbondarchuk.oauth2.server.config.props.model.AuthProperties;
import com.nbondarchuk.oauth2.server.security.*;
import com.nbondarchuk.oauth2.server.security.filter.TokenAuthenticationFilter;
import com.nbondarchuk.oauth2.server.service.TokenService;
import com.nbondarchuk.oauth2.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.BASIC;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
@Configuration
public class SecurityConfig {

    @Value("${client_id}")
    private String clientId;

    @Value("${client_secret}")
    private String clientSecret;

    @Bean
    public ClientRegistration clientRegistration() {
        return ClientRegistration
                .withRegistrationId("bitbucket")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .userNameAttributeName("username")
                .clientAuthenticationMethod(BASIC)
                .authorizationGrantType(AUTHORIZATION_CODE)
                .userInfoUri("https://api.bitbucket.org/2.0/user")
                .tokenUri("https://bitbucket.org/site/oauth2/access_token")
                .authorizationUri("https://bitbucket.org/site/oauth2/authorize")
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .build();
    }

    @Bean
    @Autowired
    public AuthenticationFacade authenticationFacade(
            OAuth2AuthorizedClientService clientService) {
        return new AuthenticationFacadeImpl(clientService);
    }

    @Bean
    @Autowired
    public ExampleOAuth2UserService oAuth2userService(UserService userService) {
        return new ExampleOAuth2UserService(userService);
    }

    @Bean
    @Autowired
    public ClientRegistrationRepository clientRegistrationRepository(List<ClientRegistration> registrations) {
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    @Autowired
    public TokenAuthenticationFilter tokenAuthenticationFilter(
            UserService userService, TokenService tokenService) {
        return new TokenAuthenticationFilter(userService, tokenService);
    }

    @Bean
    @Autowired
    public AuthenticationFailureHandler authenticationFailureHandler(
            HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
        return new ExampleAuthenticationFailureHandler(authorizationRequestRepository);
    }

    @Bean
    @Autowired
    public AuthenticationSuccessHandler authenticationSuccessHandler(
            TokenService tokenService,
            AuthProperties authProperties,
            HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
        return new ExampleAuthenticationSuccessHandler(
                tokenService, authProperties, authorizationRequestRepository);
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Configuration
    @EnableWebSecurity
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        private final ExampleOAuth2UserService userService;

        private final TokenAuthenticationFilter tokenAuthenticationFilter;

        private final AuthenticationFailureHandler authenticationFailureHandler;

        private final AuthenticationSuccessHandler authenticationSuccessHandler;

        private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

        @Autowired
        public WebSecurityConfig(
                ExampleOAuth2UserService userService,
                TokenAuthenticationFilter tokenAuthenticationFilter,
                AuthenticationFailureHandler authenticationFailureHandler,
                AuthenticationSuccessHandler authenticationSuccessHandler,
                HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
            this.userService = userService;
            this.tokenAuthenticationFilter = tokenAuthenticationFilter;
            this.authenticationFailureHandler = authenticationFailureHandler;
            this.authenticationSuccessHandler = authenticationSuccessHandler;
            this.authorizationRequestRepository = authorizationRequestRepository;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .cors().and()
                    .csrf().disable()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .exceptionHandling(eh -> eh
                            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    )
                    .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
                    .authorizeRequests(authorizeRequests -> authorizeRequests
                            .antMatchers("/auth/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .oauth2Login(oauth2Login -> oauth2Login
                            .failureHandler(authenticationFailureHandler)
                            .successHandler(authenticationSuccessHandler)
                            .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(userService))
                            .authorizationEndpoint(authEndpoint -> authEndpoint.authorizationRequestRepository(authorizationRequestRepository))
                    );

            http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }
}
