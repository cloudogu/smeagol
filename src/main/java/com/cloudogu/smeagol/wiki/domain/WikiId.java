package com.cloudogu.smeagol.wiki.domain;

import java.util.Objects;

/**
 * Wiki identifier, a wiki can be identified by the id of the repository and its branch.
 */
public class WikiId {

    // TODO should be name and repositorID ???
    private final String repositoryID;
    private final String branch;

    public WikiId(String repositoryID, String branch) {
        this.repositoryID = repositoryID;
        this.branch = branch;
    }

    public String getRepositoryID() {
        return repositoryID;
    }

    public String getBranch() {
        return branch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WikiId wikiId = (WikiId) o;
        return Objects.equals(repositoryID, wikiId.repositoryID) &&
                Objects.equals(branch, wikiId.branch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repositoryID, branch);
    }

    @Override
    public String toString() {
        return branch.concat("@").concat(repositoryID);
    }
}
