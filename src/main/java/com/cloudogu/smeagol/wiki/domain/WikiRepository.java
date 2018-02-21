package com.cloudogu.smeagol.wiki.domain;

import java.util.Optional;

/**
 * Wiki repositories are able to query wiki objects.
 */
@SuppressWarnings("squid:S1609") // ignore FunctionalInterface warning
public interface WikiRepository {

    /**
     * Returns the wiki with the given id.
     *
     * @param id id of wiki
     *
     * @return wiki
     */
    Optional<Wiki> findById(WikiId id);
}
