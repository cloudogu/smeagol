package com.cloudogu.smeagol.repository.domain;

import java.util.Optional;

/**
 * Repository to query repositories.
 */
public interface RepositoryRepository {

    /**
     * Find all repositories.
     *
     * @param wikiEnabled Optional<Boolean> wikiEnabled
     * @return all repositories
     */
    Iterable<Repository> findAll(Optional<Boolean> wikiEnabled);

    /**
     * Find repository with given id.
     *
     * @param id repository id
     * @return repository
     */
    Optional<Repository> findById(RepositoryId id);
}
