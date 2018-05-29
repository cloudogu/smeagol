package com.cloudogu.smeagol.wiki.domain;

import java.util.List;

public class History {

    private final WikiId wikiId;
    private final Path path;
    private final List<Commit> commits;

    public History(WikiId wikiId, Path path, List<Commit> commits) {
        this.wikiId = wikiId;
        this.path = path;
        this.commits = commits;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }

    public List<Commit> getCommits() {
        return commits;
    }
}
