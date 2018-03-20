package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;

/**
 * A {@link PullChangesStrategy} decides, if changes should be pulled from the remote repository of the wiki.
 */
@SuppressWarnings("squid:S1609") // ignore functional interface warning
public interface PullChangesStrategy {

    /**
     * Returns {@code true} if the changes should be pulled for the given wiki
     *
     * @param wikiId id of wiki
     *
     * @return {@code true} if changes should be pulled
     */
    boolean shouldPull(WikiId wikiId);

}
