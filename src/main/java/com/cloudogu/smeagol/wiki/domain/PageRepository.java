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
    Page save(Page page);

    /**
     * Returns {@code true} if the path exists.
     *
     * @return {@code true} if path exists
     */
    boolean exists(WikiId wikiId, Path path);

    /**
     * Find the page with the given path in the requested wiki.
     *
     * @param wikiId id of the wiki
     * @param path path of the page
     *
     * @return page with path
     */
    Optional<Page>  findByWikiIdAndPath(WikiId wikiId, Path path);

    /**
     * Find the page with the given path and commitId in the requested wiki.
     *
     * @param wikiId id of the wiki
     * @param path path of the page
     * @param commitId id of the commit
     *
     * @return page with path
     */
    Optional<Page> findByWikiIdAndPathAndCommit(WikiId wikiId, Path path, CommitId commitId);

    /**
     * Deletes the page.
     *
     * @param page deleted page
     * @param commit delete commit
     */
    void delete(Page page, Commit commit);
}
