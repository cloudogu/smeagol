package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;

/**
 * This event is fired if the search index must be cleared.
 */
public class ClearIndexEvent {

    private final WikiId wikiId;

    ClearIndexEvent(WikiId wikiId) {
        this.wikiId = wikiId;
    }

    public WikiId getWikiId() {
        return wikiId;
    }
}
