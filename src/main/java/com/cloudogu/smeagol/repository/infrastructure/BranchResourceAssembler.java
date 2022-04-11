package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.Branch;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class BranchResourceAssembler extends RepresentationModelAssemblerSupport<Branch, BranchResource> {

    public BranchResourceAssembler() {
        super(RepositoryController.class, BranchResource.class);
    }

    @Override
    public @NotNull BranchResource toModel(Branch branch) {
        BranchResource resource = new BranchResource(branch.getName().toString());
        resource.add(selfLink(branch));
        return resource;
    }

    private Link selfLink(Branch branch) {
        return linkTo(methodOn(RepositoryController.class).findById(branch.getRepositoryId()))
                .slash("branches")
                .slash(branch.getName().getValue())
                .withSelfRel();
    }

}
