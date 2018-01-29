package com.cloudogu.smeagol.repository.infrastructure;

import org.springframework.hateoas.ResourceSupport;

public class BranchResource extends ResourceSupport {

    private String name;

    public BranchResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
