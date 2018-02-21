package com.cloudogu.smeagol.repository.domain;

/**
 * Repository to query branches.
 */
@SuppressWarnings("squid:S1609") // ignore FunctionalInterface warning
public interface BranchRepository {

    /**
     * Find all branches of the repository with the given repository id.
     *
     * @param repositoryId id of repository
     *
     * @return all branches of the repository
     */
    Iterable<Branch> findByRepositoryId(RepositoryId repositoryId);
}
