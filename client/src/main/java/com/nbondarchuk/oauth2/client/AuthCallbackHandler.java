package com.nbondarchuk.oauth2.client;

import com.google.common.collect.Iterables;
import com.nbondarchuk.oauth2.client.model.Tokens;
import fi.iki.elonen.NanoHTTPD;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.nbondarchuk.oauth2.client.shared.Cookies.REFRESH_TOKEN;
import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Response.Status.INTERNAL_ERROR;
import static fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-13
 */
public class AuthCallbackHandler extends NanoHTTPD {

    private Tokens tokens;

    private final Lock lock = new ReentrantLock();

    private final Condition tokenSet = lock.newCondition();

    public AuthCallbackHandler(int port) {
        super(port);
    }

    public Tokens getTokens() throws InterruptedException, AuthenticationException {
        if (tokens != null) {
            return tokens;
        }

        lock.lock();
        try {
            while (tokens == null) {
                if (!tokenSet.await(5, MINUTES)) {
                    throw new AuthenticationException("Token wasn't received in 1 minute.");
                }
            }
        } finally {
            lock.unlock();
        }

        return tokens;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (GET == session.getMethod() && "/oauth2/redirect".equals(session.getUri())) {
            Map<String, List<String>> params = session.getParameters();

            List<String> errors = params.get("error");
            if (errors != null) {
                String error = Iterables.getFirst(errors, null);
                if (error != null) {
                    return newFixedLengthResponse(INTERNAL_ERROR, MIME_PLAINTEXT, error);
                }
            }

            List<String> tokenList = params.get("token");
            if (tokenList != null) {
                String accessToken = Iterables.getFirst(tokenList, null);
                String refreshToken = session.getCookies().read(REFRESH_TOKEN);
                lock.lock();
                try {
                    this.tokens = Tokens.of(accessToken, refreshToken);
                    tokenSet.signal();
                } finally {
                    lock.unlock();
                }
                return newFixedLengthResponse("Token received, check client console for test output.");
            }
        }
        return newFixedLengthResponse(NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }
}
