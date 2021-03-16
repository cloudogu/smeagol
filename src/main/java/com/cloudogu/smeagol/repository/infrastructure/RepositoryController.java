package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.BranchRepository;
import com.cloudogu.smeagol.repository.domain.Repository;
import com.cloudogu.smeagol.repository.domain.RepositoryId;
import com.cloudogu.smeagol.repository.domain.RepositoryRepository;
import com.cloudogu.smeagol.wiki.infrastructure.MissingSmeagolPluginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

import static org.springframework.hateoas.Resources.wrap;

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

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findAll(@RequestParam(defaultValue = "false") boolean wikiEnabled) {
        try {
            Collection<RepositoryResource> repositories = repositoryAssembler.toResources(repositoryRepository.findAll(wikiEnabled));
            return ResponseEntity.ok(wrap(repositories));
        } catch (MissingSmeagolPluginException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
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
