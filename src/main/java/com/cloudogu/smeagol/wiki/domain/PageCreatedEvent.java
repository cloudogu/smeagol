package com.cloudogu.smeagol.wiki.domain;

/**
 * Domain event which is fired whenever a new page was created.
 */
public class PageCreatedEvent {

    private final Page page;

    public PageCreatedEvent(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }
}
