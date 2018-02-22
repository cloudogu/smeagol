package com.cloudogu.smeagol.wiki.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * The page aggregate represents a single wiki page.
 */
public class Page {

    private WikiId wikiId;
    private Path path;
    private Content content;
    private Optional<Commit> commit;

    public Page(WikiId wikiId, Path path, Content content) {
        this.wikiId = wikiId;
        this.path = path;
        this.content = content;
    }

    public Page(WikiId wikiId, Path path, Content content, Commit commit) {
        this.wikiId = wikiId;
        this.path = path;
        this.content = content;
        this.commit = Optional.of(commit);
    }

    public void edit(Commit commit, Content content) {
        this.commit = Optional.of(commit);
        this.content = content;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }

    public Content getContent() {
        return content;
    }

    public Optional<Commit> getCommit() {
        return commit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Page page = (Page) o;
        return Objects.equals(wikiId, page.wikiId) &&
                Objects.equals(path, page.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(wikiId, path);
    }
}
