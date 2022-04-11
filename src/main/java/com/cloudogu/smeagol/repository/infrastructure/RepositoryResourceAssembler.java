package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.Repository;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.time.format.DateTimeFormatter;

public class RepositoryResourceAssembler extends RepresentationModelAssemblerSupport<Repository, RepositoryResource> {

    public RepositoryResourceAssembler() {
        super(RepositoryController.class, RepositoryResource.class);
    }

    @Override
    public @NotNull RepositoryResource toModel(Repository repository) {
        RepositoryResource resource = new RepositoryResource(
                repository.getId().getValue(),
                repository.getName().getValue(),
                repository.getDescription().getValue(),
                repository.getLastModified() != null ? DateTimeFormatter.ISO_INSTANT.format(repository.getLastModified()) : null
        );
        resource.add(selfLink(repository));
        return resource;
    }

    public RepositoryResource toModel(Repository repository, Iterable<BranchResource> branches) {
        RepositoryResource resource = toModel(repository);
        resource.embed("branches", branches);
        return resource;
    }

    private Link selfLink(Repository repository) {
        return linkTo(methodOn(RepositoryController.class).findById(repository.getId()))
                .withSelfRel();
    }
}
