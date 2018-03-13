package com.cloudogu.smeagol.wiki.domain;

/**
 * Domain event which is fired whenever a page was deleted.
 */
public class PageDeletedEvent {

    private final WikiId wikiId;
    private final Path path;

    public PageDeletedEvent(Page page) {
        this(page.getWikiId(), page.getPath());
    }

    public PageDeletedEvent(WikiId wikiId, Path path) {
        this.wikiId = wikiId;
        this.path = path;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }
}
