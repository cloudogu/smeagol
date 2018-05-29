package com.cloudogu.smeagol.wiki.domain;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Directory is a folder in the filesystem and consists of a file and the pages within this directory.
 */
public class Directory {

    private final WikiId wikiId;
    private final Path path;
    private final Iterable<Path> childPages;
    private final Iterable<Path> childDirectories;

    public Directory(WikiId wikiId, Path path, Iterable<Path> childDirectories, Iterable<Path> childPages) {
        this.wikiId = checkNotNull(wikiId);
        this.path = checkPath(path);
        this.childDirectories = checkNotNull(childDirectories);
        this.childPages = checkNotNull(childPages);
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }

    public Iterable<Path> getChildDirectories() {
        return childDirectories;
    }

    public Iterable<Path> getChildPages() {
        return childPages;
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Directory directory = (Directory) o;
        return Objects.equals(path, directory.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    private static Path checkPath(Path path) {
        checkArgument(path.isDirectory(), "path %s is not a directory path", path);
        return path;
    }
}
