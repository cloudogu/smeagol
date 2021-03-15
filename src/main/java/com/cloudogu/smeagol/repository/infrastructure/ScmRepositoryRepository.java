package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.repository.domain.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScmRepositoryRepository implements RepositoryRepository {

    private ScmHttpClient scmHttpClient;

    @Autowired
    public ScmRepositoryRepository(ScmHttpClient scmHttpClient) {
        this.scmHttpClient = scmHttpClient;
    }

    @Override
    public Iterable<Repository> findAll(Optional<Boolean> wikiEnabled) {
        String queryParam = "";
        if (wikiEnabled.isPresent() && wikiEnabled.get()) {
            queryParam = "?wikiEnabled=true";
        }
        Optional<RepositoriesEndpointDTO> dtos = scmHttpClient.get("/api/v2/smeagol/repositories"+queryParam, RepositoriesEndpointDTO.class);

        return Arrays.stream(dtos.get()._embedded.repositories)
            .filter(dto -> "git".equals(dto.type))
            .map(this::map)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Repository> findById(RepositoryId id) {
        return scmHttpClient.get("/api/rest/repositories/{id}.json", RepositoryDTO.class, id)
            .map(this::map);
    }

    private Repository map(RepositoryDTO dto) {
        return new Repository(
            RepositoryId.valueOf(dto.id),
            Name.valueOf(dto.name),
            Description.valueOf(dto.description),
            dto.lastModified != null ? new Date(dto.lastModified).toInstant() : null
        );
    }

    private Repository map(RepositoryDTOv2 dto) {
        return new Repository(
            RepositoryId.valueOf(dto.id),
            Name.valueOf(dto.namespace + "/" + dto.name),
            Description.valueOf(dto.description),
            null
        );
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class RepositoryDTO {
        private String id;
        private String type;
        private String name;
        private String description;
        private Long lastModified;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class RepositoriesEndpointDTO {
        private RepositoriesEndpointEmbeddedDTO _embedded;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class RepositoriesEndpointEmbeddedDTO {
        private RepositoryDTOv2[] repositories;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class RepositoryDTOv2 {
        private String id;
        private String type;
        private String name;
        private String namespace;
        private String description;
        private String defaultBranch;
    }
}
