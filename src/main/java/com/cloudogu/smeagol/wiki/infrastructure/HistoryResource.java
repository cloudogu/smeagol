package com.cloudogu.smeagol.wiki.infrastructure;


import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class HistoryResource extends ResourceSupport {

    private final String path;
    private final List<CommitResource> commits;

    public HistoryResource(String path, List<CommitResource> commits) {
        this.path = path;
        this.commits = commits;
    }

    public String getPath() {
        return path;
    }

    public List<CommitResource> getCommits() {
        return commits;
    }
}
