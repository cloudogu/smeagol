package com.cloudogu.smeagol.wiki.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * The page aggregate represents a single wiki page.
 */
public class Page {

    private WikiId wikiId;
    private Path path;
    private Optional<Author> author;
    private Content content;
    private Optional<Instant> lastModified;

    public Page(WikiId wikiId, Path path, Content content) {
        this.wikiId = wikiId;
        this.path = path;
        this.content = content;
        this.lastModified = Optional.empty();
    }

    public Page(WikiId wikiId, Path path, Author author, Content content, Instant lastModified) {
        this.wikiId = wikiId;
        this.path = path;
        this.author = Optional.of(author);
        this.content = content;
        this.lastModified = Optional.of(lastModified);
    }

    public void edit(Content content) {
        this.content = content;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }

    public Optional<Author> getAuthor() {
        return author;
    }

    public Content getContent() {
        return content;
    }

    public Optional<Instant> getLastModified() {
        return lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(path, page.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
