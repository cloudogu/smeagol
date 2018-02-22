package com.cloudogu.smeagol.wiki.domain;

import java.util.Optional;

/**
 * Directory repositories are able to traverse directories of wiki.
 */
@SuppressWarnings("squid:S1609") // ignore FunctionalInterface warning
public interface DirectoryRepository {

    /**
     * Returns the directory for the path within the wiki.
     *
     * @param wikiId id of wiki
     * @param path the path of the directory
     *
     * @return optional of directory or empty
     */
    Optional<Directory> findByWikiIdAndPath(WikiId wikiId, Path path);
}
