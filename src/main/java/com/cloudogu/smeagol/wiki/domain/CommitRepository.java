package com.cloudogu.smeagol.wiki.domain;

public interface CommitRepository {
    History findHistoryByWikiIdAndPath(WikiId id, Path path);
}
