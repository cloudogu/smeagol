package com.cloudogu.smeagol.wiki.infrastructure;


import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.Objects;

public class HistoryResource extends RepresentationModel<HistoryResource> {

    private final String path;
    private final List<CommitResource> commits;

    HistoryResource(String path, List<CommitResource> commits) {
        this.path = path;
        this.commits = commits;
    }

    public String getPath() {
        return path;
    }

    public List<CommitResource> getCommits() {
        return commits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        HistoryResource that = (HistoryResource) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(commits, that.commits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), path, commits);
    }
}
