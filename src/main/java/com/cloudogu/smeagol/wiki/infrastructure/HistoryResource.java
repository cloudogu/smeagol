package com.cloudogu.smeagol.wiki.infrastructure;


import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class HistoryResource extends ResourceSupport {

    private final String wikiId;
    private final String path;
    private final List<CommitResource> commits;

    public HistoryResource(String wikiId, String path, List<CommitResource> commits) {
        this.wikiId = wikiId;
        this.path = path;
        this.commits = commits;
    }

    public String getWikiId() {
        return wikiId;
    }

    public String getPath() {
        return path;
    }

    public List<CommitResource> getCommits() {
        return commits;
    }
}
