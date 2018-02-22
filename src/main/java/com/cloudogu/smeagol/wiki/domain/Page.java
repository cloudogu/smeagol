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

    private Path oldPath;

    public Page(WikiId wikiId, Path path, Content content) {
        this.wikiId = wikiId;
        this.path = path;
        this.content = content;
    }

    public Page(WikiId wikiId, Path path, Path oldPath, Content content, Commit commit) {
        this.wikiId = wikiId;
        this.path = path;
        this.oldPath = oldPath;
        this.content = content;
        this.commit = Optional.of(commit);
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

    public void move(Commit commit, Path newPath) {
        this.commit = Optional.of(commit);
        this.oldPath = path;
        this.path = newPath;
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

    public Optional<Path> getOldPath() {
        return Optional.ofNullable(oldPath);
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
