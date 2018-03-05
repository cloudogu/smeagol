package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Result of a search.
 */
public class SearchResult {

    private final WikiId wikiId;
    private final Path path;

    public SearchResult(WikiId wikiId, Path path) {
        this.wikiId = wikiId;
        this.path = path;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchResult that = (SearchResult) o;
        return Objects.equals(wikiId, that.wikiId) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wikiId, path);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("wikiId", wikiId)
                .add("path", path)
                .toString();
    }
}
