package com.cloudogu.smeagol.repository.infrastructure;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class BranchResource extends RepresentationModel<BranchResource> {

    private final String name;

    public BranchResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BranchResource resource = (BranchResource) o;
        return Objects.equals(name, resource.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
