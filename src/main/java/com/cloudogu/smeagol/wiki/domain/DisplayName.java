package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Simple string value object.
 */
public class DisplayName {

    private final String value;

    private DisplayName(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the display name.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a display name from a string.
     *
     * @param value string representation of display name
     *
     * @return repository name
     */
    public static DisplayName valueOf(String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "display name can not be null or empty");
        return new DisplayName(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisplayName that = (DisplayName) o;
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
