package com.cloudogu.smeagol.wiki.domain;

import java.util.Optional;

/**
 * PageRepositories are able to execute crud actions on pages.
 */
public interface PageRepository {

    /**
     * Saves the page.
     *
     * @param page modified page
     */
    void save(Page page);

    /**
     * Find the page with the given path in the requested wiki.
     *
     * @param wikiId id of the wiki
     * @param path path of the page
     *
     * @return page with path
     */
    Optional<Page> findByWikiIdAndPath(WikiId wikiId, Path path);
}
