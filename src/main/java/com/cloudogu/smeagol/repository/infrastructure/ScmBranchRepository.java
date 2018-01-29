package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.Branch;
import com.cloudogu.smeagol.repository.domain.BranchRepository;
import com.cloudogu.smeagol.repository.domain.Name;
import com.cloudogu.smeagol.repository.domain.RepositoryId;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScmBranchRepository implements BranchRepository {

    private ScmHttpClient scmHttpClient;

    @Autowired
    public ScmBranchRepository(ScmHttpClient scmHttpClient) {
        this.scmHttpClient = scmHttpClient;
    }

    @Override
    public Iterable<Branch> findByRepositoryId(RepositoryId repositoryId) {
        String url = String.format("/api/rest/repositories/%s/branches.json", repositoryId);
        BranchesDTO dto = scmHttpClient.get(url, BranchesDTO.class);
        return dto.branch.stream()
                .map(b -> new Branch(repositoryId, Name.valueOf(b.name)))
                .collect(Collectors.toList());
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class BranchesDTO {
        private List<BranchDTO> branch;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class BranchDTO {
        private String name;
    }
}
