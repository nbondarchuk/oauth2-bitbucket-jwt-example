package com.nbondarchuk.oauth2.server.model.bitbucket;

import lombok.Data;

import java.util.List;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
@Data
public class BitbucketResponse<T> {

    private String next;

    private Integer size;

    private Integer page;

    private Integer pagelen;

    private List<T> values;
}
