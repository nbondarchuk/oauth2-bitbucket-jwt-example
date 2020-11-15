package com.nbondarchuk.oauth2.server.utils;

import org.springframework.http.HttpHeaders;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-13
 */
public class HttpHeadersBuilder {

    private final HttpHeaders headers = new HttpHeaders();

    public HttpHeadersBuilder acceptJson() {
        headers.set(ACCEPT, APPLICATION_JSON_VALUE);
        return this;
    }

    public HttpHeaders build() {
        return headers;
    }
}
