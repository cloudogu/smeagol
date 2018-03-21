package com.cloudogu.smeagol.wiki.domain;

@FunctionalInterface
public interface CommitRepository {
    History findHistoryByWikiIdAndPath(WikiId id, Path path);
}
