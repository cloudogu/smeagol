package com.cloudogu.smeagol.repository.domain;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Branch represents an named branch of a repository.
 */
public class Branch {

    private RepositoryId repositoryId;
    private Name name;

    public Branch(RepositoryId repositoryId, Name name) {
        this.repositoryId = repositoryId;
        this.name = name;
    }

    public RepositoryId getRepositoryId() {
        return repositoryId;
    }

    public Name getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return Objects.equals(repositoryId, branch.repositoryId) &&
                Objects.equals(name, branch.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repositoryId, name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("repositoryId", repositoryId)
                .add("name", name)
                .toString();
    }
}
