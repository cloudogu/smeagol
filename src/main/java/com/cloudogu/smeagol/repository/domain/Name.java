package com.cloudogu.smeagol.repository.domain;

import com.google.common.base.Strings;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Name is simple string value object, which can never be empty.
 */
public final class Name {

    private final String value;

    private Name(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the name.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a Name from a string.
     *
     * @param value string a name
     *
     * @return name
     */
    public static Name valueOf(String value) {
        checkArgument(!Strings.isNullOrEmpty(value), "name can not be null or empty");
        return new Name(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Name that = (Name) o;
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
