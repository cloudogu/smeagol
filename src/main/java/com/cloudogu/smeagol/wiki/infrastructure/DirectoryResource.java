package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class DirectoryResource extends RepresentationModel<DirectoryResource> {

    private final String path;
    private final Iterable<DirectoryEntryResource> children;

    public DirectoryResource(String path, Iterable<DirectoryEntryResource> children) {
        this.path = path;
        this.children = children;
    }

    public String getPath() {
        return path;
    }

    public Iterable<DirectoryEntryResource> getChildren() {
        return children;
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
        DirectoryResource resource = (DirectoryResource) o;
        return Objects.equals(path, resource.path) &&
                Objects.equals(children, resource.children);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), path, children);
    }
}
