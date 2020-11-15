package com.nbondarchuk.oauth2.server.model.bitbucket.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
@Data
public class BitbucketRepository {

    private String uuid;

    @JsonProperty("full_name")
    private String fullName;
}
