package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.Repository;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.time.format.DateTimeFormatter;

public class RepositoryResourceAssembler extends ResourceAssemblerSupport<Repository, RepositoryResource> {

    public RepositoryResourceAssembler() {
        super(RepositoryController.class, RepositoryResource.class);
    }

    @Override
    public RepositoryResource toResource(Repository repository) {
        RepositoryResource resource = new RepositoryResource(
                repository.getName().getValue(),
                repository.getDescription().getValue(),
                DateTimeFormatter.ISO_INSTANT.format(repository.getLastModified())
        );
        resource.add(selfLink(repository));
        return resource;
    }

    public RepositoryResource toResource(Repository repository, Iterable<BranchResource> branches) {
        RepositoryResource resource = toResource(repository);
        resource.embed("branches", branches);
        return resource;
    }

    private Link selfLink(Repository repository) {
        return linkTo(methodOn(RepositoryController.class).findById(repository.getId()))
                .withSelfRel();
    }
}
