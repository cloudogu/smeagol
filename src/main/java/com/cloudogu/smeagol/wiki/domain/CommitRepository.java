package com.cloudogu.smeagol.wiki.domain;

/**
 * CommitRepository supplies the commits of a wiki.
 */
@SuppressWarnings("squid:S1609") // ignore functional interface warning
public interface CommitRepository {
    /**
     * Find the commits to a given path for a wiki.
     *
     * @param id id of wiki
     * @param path search path
     *
     * @return History which contains the list of commits
     */
    History findHistoryByWikiIdAndPath(WikiId id, Path path);
}
