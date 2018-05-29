package com.cloudogu.smeagol.repository.infrastructure;

import org.springframework.hateoas.ResourceSupport;

import java.util.Objects;

public class BranchResource extends ResourceSupport {

    private String name;

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
