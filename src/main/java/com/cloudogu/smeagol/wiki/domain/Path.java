package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Path represents the path of a page within the wiki.
 */
public final class Path implements Serializable {

    private static final Pattern CHARACTER_WHITELIST = Pattern.compile("^[\\w\\.\\-_/ ]+$");

    private static final long serialVersionUID = 1L;

    private final String value;

    private Path(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of path.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the last part of the path.
     *
     * @return name of file or directory
     */
    public String getName() {
        return Iterables.getLast(Splitter.on('/').omitEmptyStrings().split(value));
    }

    /**
     * Returns {@code true} if the path points to a directory.
     *
     * @return {@code true} for a directory
     */
    public boolean isDirectory() {
        return value.endsWith("/");
    }

    /**
     * Returns {@code true} if the path points to a file.
     *
     * @return {@code true} for a file
     */
    public boolean isFile() {
        return !isDirectory();
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
        Preconditions.checkArgument(!path.contains(".."), "path contains '..', which is not allowed");
        Preconditions.checkArgument(!path.contains("//"), "path contains '//', which is not allowed");
        Preconditions.checkArgument(!path.startsWith("/"), "path starts with a '/', which is not allowed");
        Preconditions.checkArgument(!path.endsWith("."), "path ends with a '.', which is not allowed");
        Preconditions.checkArgument(!path.startsWith(" "), "path starts with a ' ', which is not allowed");
        Preconditions.checkArgument(!path.endsWith(" "), "path ends with a ' ', which is not allowed");
        Preconditions.checkArgument(CHARACTER_WHITELIST.matcher(path).matches(), "path contains illegal characters");
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

    /**
     * Creates a child directory path with the current path as parent.
     *
     * @param name name of child directory
     *
     * @return child path
     */
    public Path childDirectory(String name) {
        checkState(isDirectory(), "child can only be used on directory path");
        String suffix = "";
        if (!name.endsWith("/")) {
            suffix = "/";
        }
        return valueOf(value + name + suffix);
    }

    /**
     * Creates a child path tih the curent path as parent.
     *
     * @param name name of child file
     *
     * @return child path
     */
    public Path childFile(String name) {
        checkState(isDirectory(), "child can only be used on directory path");
        checkArgument(!name.endsWith("/"), "name must be file");
        return valueOf(value + name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
