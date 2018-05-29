package com.cloudogu.smeagol.repository.domain;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Unique id of a repository.
 */
public class RepositoryId {

    private final String value;

    private RepositoryId(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the repository id.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a repository id from a string.
     *
     * @param value string a name
     *
     * @return name
     */
    public static RepositoryId valueOf(String value) {
        checkArgument(!Strings.isNullOrEmpty(value), "repository id can not be null or empty");
        return new RepositoryId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RepositoryId that = (RepositoryId) o;
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
