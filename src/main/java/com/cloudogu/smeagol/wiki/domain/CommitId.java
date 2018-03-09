package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Objects;

/**
 * The id of a commit in a source code management system.
 */
public final class CommitId {

    private final String value;

    private CommitId(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the commit id.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a commit id from its string representation.
     *
     * @param value string representation
     *
     * @return commmit id value object
     */
    public static CommitId valueOf(String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "commit id is null or empty");
        return new CommitId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommitId commitId = (CommitId) o;
        return Objects.equals(value, commitId.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
