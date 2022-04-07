package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class DirectoryEntryResource extends RepresentationModel<DirectoryResource> {

    private final String name;
    private final String type;

    public DirectoryEntryResource(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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
        DirectoryEntryResource resource = (DirectoryEntryResource) o;
        return Objects.equals(name, resource.name) &&
                Objects.equals(type, resource.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), name, type);
    }
}
