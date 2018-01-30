package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Path represents the path of a page within the wiki.
 */
public final class Path {

    private final String value;

    private Path(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Creates a new path from its string representation.
     *
     * @param path in the wiki
     *
     * @return path value object
     */
    public static Path valueOf(String path) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "path can not be null or empty");
        Preconditions.checkArgument(!path.contains(".."), "path contains .., which is not allowed");
        Preconditions.checkArgument(!path.startsWith("/"), "path starts with a '/', which is not allowed");
        Preconditions.checkArgument(!path.endsWith("/"), "path ends with a '/', which is not allowed");
        return new Path(path);
    }

    /**
     * Concatenates a path to this one.
     *
     * @param path path to concat
     *
     * @return new path
     */
    public Path concat(Path path) {
        return Path.valueOf(getValue().concat("/").concat(path.getValue()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(value, path.value);
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
