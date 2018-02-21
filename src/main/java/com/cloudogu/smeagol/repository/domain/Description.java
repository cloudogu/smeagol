package com.cloudogu.smeagol.repository.domain;

import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Description is simple string value object, which can be empty.
 */
public class Description {

    private final String value;

    private Description(String value) {
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
     * Creates a description from a string.
     *
     * @param value string representation of a description
     *
     * @return repository name
     */
    public static Description valueOf(String value) {
        return new Description(Strings.nullToEmpty(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Description that = (Description) o;
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
