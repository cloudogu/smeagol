package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Simple string value object.
 */
public class RepositoryName {

    private final String value;

    private RepositoryName(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the repository name.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a repository name from a string.
     *
     * @param value string representation of repository name
     * @return repository name
     */
    public static RepositoryName valueOf(String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "repository name can not be null or empty");
        return new RepositoryName(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RepositoryName that = (RepositoryName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
