package com.cloudogu.smeagol.repository.domain;

import java.util.Optional;

/**
 * Repository to query repositories.
 */
public interface RepositoryRepository {

    /**
     * Find all repositories.
     *
     * @return all repositories
     */
    Iterable<Repository> findAll();

    /**
     * Find repository with given id.
     *
     * @param id repository id
     *
     * @return repository
     */
    Optional<Repository> findById(RepositoryId id);
}
