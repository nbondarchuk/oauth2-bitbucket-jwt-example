package com.nbondarchuk.oauth2.server.service;

import com.nbondarchuk.oauth2.server.model.entity.Repository;

import java.util.List;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
public interface BitbucketService {

    List<Repository> getRepositories();
}
