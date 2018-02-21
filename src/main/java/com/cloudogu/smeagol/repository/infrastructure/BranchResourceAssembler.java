package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.Branch;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class BranchResourceAssembler extends ResourceAssemblerSupport<Branch, BranchResource> {

    public BranchResourceAssembler() {
        super(RepositoryController.class, BranchResource.class);
    }

    @Override
    public BranchResource toResource(Branch branch) {
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
