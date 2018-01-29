package com.cloudogu.smeagol.repository.domain;

import com.google.common.base.MoreObjects;

import java.time.Instant;
import java.util.Objects;

/**
 * Repository is an aggregate which represents a source code repository.
 */
public class Repository {

    private RepositoryId id;
    private Name name;
    private Description description;
    private Instant lastModified;

    public Repository(RepositoryId id, Name name, Description description, Instant lastModified) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lastModified = lastModified;
    }

    public RepositoryId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Description getDescription() {
        return description;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("description", description)
                .add("lastModified", lastModified)
                .toString();
    }
}
