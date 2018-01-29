package com.cloudogu.smeagol.repository.domain;

/**
 * Repository to query branches.
 */
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
