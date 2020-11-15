package com.nbondarchuk.oauth2.server.service;

import com.nbondarchuk.oauth2.server.model.bitbucket.repository.BitbucketRepositoriesResponse;
import com.nbondarchuk.oauth2.server.model.bitbucket.repository.BitbucketRepository;
import com.nbondarchuk.oauth2.server.model.entity.Repository;
import com.nbondarchuk.oauth2.server.utils.HttpHeadersBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-09
 */
public class BitbucketServiceImpl implements BitbucketService {

    private static final String BASE_URL = "https://api.bitbucket.org";

    private final Supplier<RestTemplate> restTemplate;

    public BitbucketServiceImpl(Supplier<RestTemplate> restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Repository> getRepositories() {
        UriComponentsBuilder uriBuilder = fromHttpUrl(format("%s/2.0/repositories", BASE_URL));
        uriBuilder.queryParam("role", "member");

        ResponseEntity<BitbucketRepositoriesResponse> response = restTemplate.get().exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeadersBuilder()
                        .acceptJson()
                        .build()),
                BitbucketRepositoriesResponse.class);

        BitbucketRepositoriesResponse body = response.getBody();
        return body == null ? emptyList() : extractRepositories(body);
    }

    private List<Repository> extractRepositories(BitbucketRepositoriesResponse response) {
        return response.getValues() == null
                ? emptyList()
                : response.getValues().stream().map(BitbucketServiceImpl.this::convertRepository).collect(toList());
    }

    private Repository convertRepository(BitbucketRepository bbRepo) {
        Repository repo = new Repository();
        repo.setId(bbRepo.getUuid());
        repo.setFullName(bbRepo.getFullName());
        return repo;
    }
}
