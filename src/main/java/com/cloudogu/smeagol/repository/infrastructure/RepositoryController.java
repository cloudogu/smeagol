package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/repositories")
public class RepositoryController {

    private final RepositoryRepository repositoryRepository;
    private final BranchRepository branchRepository;
    private final RepositoryResourceAssembler repositoryAssembler = new RepositoryResourceAssembler();
    private final BranchResourceAssembler branchAssembler = new BranchResourceAssembler();

    @Autowired
    public RepositoryController(RepositoryRepository repositoryRepository, BranchRepository branchRepository) {
        this.repositoryRepository = repositoryRepository;
        this.branchRepository = branchRepository;
    }

    @RequestMapping
    public List<RepositoryResource> findAll() {
        return repositoryAssembler.toResources(repositoryRepository.findAll());
    }

    @RequestMapping("/{repositoryId}")
    public ResponseEntity<RepositoryResource> findById(@PathVariable("repositoryId") RepositoryId id) {
        Optional<Repository> repository = repositoryRepository.findById(id);
        if (repository.isPresent()) {
            Iterable<BranchResource> branches = branchAssembler.toResources(branchRepository.findByRepositoryId(id));
            return ResponseEntity.ok(repositoryAssembler.toResource(repository.get(), branches));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
