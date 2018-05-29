package com.cloudogu.smeagol.wiki.domain;

/**
 * Domain event which is fired whenever a page was modified.
 */
public class PageModifiedEvent {

    private final Page page;

    public PageModifiedEvent(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }
}
