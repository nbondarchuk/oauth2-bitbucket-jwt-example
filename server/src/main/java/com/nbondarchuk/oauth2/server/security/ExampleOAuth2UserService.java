package com.nbondarchuk.oauth2.server.security;

import com.nbondarchuk.oauth2.server.model.entity.User;
import com.nbondarchuk.oauth2.server.service.UserService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
public class ExampleOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public ExampleOAuth2UserService(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(oAuth2User, userRequest);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler.
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User, OAuth2UserRequest oAuth2UserRequest) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = new OAuth2UserInfo(oAuth2User.getAttributes());

        User user = findOrCreate(userInfo, registrationId);
        return new ExampleOAuth2User(user, oAuth2User.getAttributes());
    }

    private User findOrCreate(OAuth2UserInfo userInfo, String provider) {
        Optional<User> userOpt = userService.findByLogin(userInfo.getLogin());
        if (!userOpt.isPresent()) {
            User user = new User();
            user.setProvider(provider);
            user.setName(userInfo.getName());
            user.setLogin(userInfo.getLogin());
            return userService.create(user);
        }
        return userOpt.get();
    }

    private static class OAuth2UserInfo {

        private final Map<String, Object> attrs;

        public OAuth2UserInfo(Map<String, Object> attrs) {
            this.attrs = requireNonNull(attrs);
        }

        public String getName() {
            return getAttribute("display_name");
        }

        public String getLogin() {
            return getAttribute("username");
        }

        @SuppressWarnings("unchecked")
        private <A> A getAttribute(String attrName) {
            return (A) attrs.get(attrName);
        }
    }
}
