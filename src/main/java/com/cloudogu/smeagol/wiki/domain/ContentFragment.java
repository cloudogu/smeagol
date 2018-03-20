package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Value object which represents a single fragment of the content of a page.
 */
public final class ContentFragment {

    private final String value;

    private ContentFragment(String value) {
        this.value = value;
    }

    /**
     * Returns string representation of content fragment.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates content fragment from its string representation.
     *
     * @param value string representation
     *
     * @return content fragment value object
     */
    public static ContentFragment valueOf(String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "value is required");
        return new ContentFragment(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentFragment that = (ContentFragment) o;
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
