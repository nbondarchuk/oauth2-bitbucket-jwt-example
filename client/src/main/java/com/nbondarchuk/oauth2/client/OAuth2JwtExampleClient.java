package com.nbondarchuk.oauth2.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nbondarchuk.oauth2.client.model.Tokens;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import static com.nbondarchuk.oauth2.client.shared.Cookies.REFRESH_TOKEN;
import static fi.iki.elonen.NanoHTTPD.SOCKET_READ_TIMEOUT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-13
 */
public class OAuth2JwtExampleClient {

    /**
     * Start client, then navigate to http://localhost:8080/auth/login.
     */
    public static void main(String[] args) throws Exception {
        AuthCallbackHandler authEndpoint = new AuthCallbackHandler(8081);
        authEndpoint.start(SOCKET_READ_TIMEOUT, true);

        HttpResponse response = getRepositories(null);
        assert (response.getStatusLine().getStatusCode() == SC_UNAUTHORIZED);

        Tokens tokens = authEndpoint.getTokens();
        System.out.println("Received tokens: " + tokens);
        response = getRepositories(tokens.getAccessToken());
        assert (response.getStatusLine().getStatusCode() == SC_OK);
        System.out.println("Repositories: " + IOUtils.toString(response.getEntity().getContent(), UTF_8));

        // emulate token usage - wait for some time until iat and exp attributes get updated
        // otherwise we will receive the same token
        Thread.sleep(5000);

        tokens = refreshToken(tokens.getRefreshToken());
        System.out.println("Refreshed tokens: " + tokens);

        // use refreshed token
        response = getRepositories(tokens.getAccessToken());
        assert (response.getStatusLine().getStatusCode() == SC_OK);
    }

    private static Tokens refreshToken(String refreshToken) throws IOException {
        BasicClientCookie cookie = new BasicClientCookie(REFRESH_TOKEN, refreshToken);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        BasicCookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);

        HttpPost request = new HttpPost("http://localhost:8080/auth/token");
        request.setHeader(ACCEPT, APPLICATION_JSON.getMimeType());

        HttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        HttpResponse execute = httpClient.execute(request);

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> response = gson.fromJson(IOUtils.toString(execute.getEntity().getContent(), UTF_8), type);

        Cookie refreshTokenCookie = cookieStore.getCookies().stream()
                .filter(c -> REFRESH_TOKEN.equals(c.getName()))
                .findAny()
                .orElseThrow(() -> new IOException("Refresh token cookie not found."));
        return Tokens.of(response.get("token"), refreshTokenCookie.getValue());
    }

    private static HttpResponse getRepositories(String accessToken) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:8080/api/repositories");
        request.setHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        if (accessToken != null) {
            request.setHeader(AUTHORIZATION, "Bearer " + accessToken);
        }
        return httpClient.execute(request);
    }
}
