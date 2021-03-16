package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.repository.domain.*;
import com.cloudogu.smeagol.wiki.infrastructure.CouldNotGetSCMRootException;
import com.cloudogu.smeagol.wiki.infrastructure.MissingSmeagolPluginException;
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
    private String repositoriesURL;

    @Autowired
    public ScmRepositoryRepository(ScmHttpClient scmHttpClient) {
        this.scmHttpClient = scmHttpClient;
    }

    synchronized private String getRepositoriesURL() throws MissingSmeagolPluginException {
        if (repositoriesURL != null) {
            return repositoriesURL;
        }
        Optional<SCMRootEndpointDTO> dto = scmHttpClient.get("/api/v2", SCMRootEndpointDTO.class);
        if (!dto.isPresent()) {
            throw new CouldNotGetSCMRootException();
        }

        if (dto.get()._links.smeagol == null) {
            throw new MissingSmeagolPluginException();
        }

        Optional<SmeagolLinkDTO> smeagolLink = Arrays.stream(dto.get()._links.smeagol)
            .filter(link -> "repositories".equals(link.name))
            .findFirst();

        if (!smeagolLink.isPresent()) {
            throw new MissingSmeagolPluginException();
        }

        repositoriesURL = smeagolLink.get().href;
        return repositoriesURL;
    }

    @Override
    public Iterable<Repository> findAll(boolean wikiEnabled) throws MissingSmeagolPluginException {
        String queryParam = "";
        if (wikiEnabled) {
            queryParam = "?wikiEnabled=true";
        }

        Optional<RepositoriesEndpointDTO> dto = scmHttpClient.get(getRepositoriesURL() + queryParam, RepositoriesEndpointDTO.class);

        return Arrays.stream(dto.get()._embedded.repositories)
            .filter(repository -> "git".equals(repository.type))
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

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class SCMRootEndpointDTO {
        private LinksDTO _links;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class LinksDTO {
        private SmeagolLinkDTO[] smeagol;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class SmeagolLinkDTO {
        private String name;
        private String href;
    }
}
