package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;

/**
 * Always pull changes.
 */
public class AlwaysPullChangesStrategy implements PullChangesStrategy {

    @Override
    public boolean shouldPull(WikiId wikiId) {
        return true;
    }

}
