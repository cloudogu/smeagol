package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Content is a simple string value object.
 */
public class Content {

    private final String value;

    private Content(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the content.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a content from a string.
     *
     * @param value string representation of content
     *
     * @return repository name
     */
    public static Content valueOf(String value) {
        return new Content(Strings.nullToEmpty(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Content that = (Content) o;
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
