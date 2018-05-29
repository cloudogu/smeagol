package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Commit;

public class CommitResourceAssembler {
    public CommitResource toResource(Commit commit) {
        return new CommitResource(commit.getId().get().getValue(),
                new AuthorResource(commit.getAuthor().getDisplayName().toString(),
                        commit.getAuthor().getEmail().toString()),
                        commit.getDate().toString(), commit.getMessage().toString());
    }
}
