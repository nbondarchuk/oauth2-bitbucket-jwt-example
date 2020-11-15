package com.nbondarchuk.oauth2.server.endpoint;

import com.nbondarchuk.oauth2.server.model.entity.RefreshToken;
import com.nbondarchuk.oauth2.server.model.entity.User;
import com.nbondarchuk.oauth2.server.security.exception.InvalidTokenException;
import com.nbondarchuk.oauth2.server.service.TokenService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.nbondarchuk.oauth2.server.shared.QueryParams.REDIRECT_URI;
import static com.nbondarchuk.oauth2.server.service.TokenService.UserContext.of;
import static com.nbondarchuk.oauth2.server.utils.Cookies.REFRESH_TOKEN;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.temporaryRedirect;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
@Log4j2
@Path("/auth")
public class AuthEndpoint extends EndpointBase {

    @Autowired
    private TokenService tokenService;

    @GET
    @Path("/login")
    public Response authorize(@QueryParam(REDIRECT_URI) String redirectUri) {
        String authUri = "/oauth2/authorization/bitbucket";
        UriComponentsBuilder builder = fromPath(authUri).queryParam(REDIRECT_URI, redirectUri);
        return handle(() -> temporaryRedirect(builder.build().toUri()).build());
    }

    @POST
    @Path("/token")
    @Produces(APPLICATION_JSON)
    public Response refreshToken(@CookieParam(REFRESH_TOKEN) String refreshToken) {
        return handle(() -> {
            if (refreshToken == null) {
                throw new InvalidTokenException("Refresh token was not provided.");
            }
            RefreshToken oldRefreshToken = tokenService.findRefreshToken(refreshToken);
            if (oldRefreshToken == null || !tokenService.isValidRefreshToken(oldRefreshToken)) {
                throw new InvalidTokenException("Refresh token is not valid or expired.");
            }

            Map<String, String> result = new HashMap<>();
            result.put("token", tokenService.newAccessToken(of(oldRefreshToken.getUser())));

            RefreshToken newRefreshToken = newRefreshTokenFor(oldRefreshToken.getUser());
            return Response.ok(result).cookie(createRefreshTokenCookie(newRefreshToken)).build();
        });
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    private RefreshToken newRefreshTokenFor(User user) {
        return tokenService.newRefreshToken(of(user));
    }

    private NewCookie createRefreshTokenCookie(RefreshToken token) {
        return new NewCookie(REFRESH_TOKEN, token.getId(), "/", null, null, (int) token.getValiditySeconds(), false, true);
    }
}
