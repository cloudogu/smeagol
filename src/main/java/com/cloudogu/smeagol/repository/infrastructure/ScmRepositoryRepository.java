package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
    public Iterable<Repository> findAll() {
        RepositoryDTO[] dtos = scmHttpClient.get("/api/rest/repositories.json", RepositoryDTO[].class);

        return Arrays.stream(dtos)
            .filter(dto -> dto.type.equals("git"))
            .map(this::map)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Repository> findById(RepositoryId id) {
        String url = String.format("/api/rest/repositories/%s.json", id);
        Repository repository = null;
        try {
            RepositoryDTO dto = scmHttpClient.get(url, RepositoryDTO.class);
            repository = map(dto);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw ex;
            }
        }
        return Optional.ofNullable(repository);
    }

    private Repository map(RepositoryDTO dto) {
        return new Repository(
            RepositoryId.valueOf(dto.id),
            Name.valueOf(dto.name),
            Description.valueOf(dto.description),
            dto.lastModified != null ? new Date(dto.lastModified).toInstant() : null
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

}
