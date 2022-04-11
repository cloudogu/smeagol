package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Commit;
import org.jetbrains.annotations.NotNull;

public class CommitResourceAssembler {
    public CommitResource toModel(@NotNull Commit commit) {
        return new CommitResource(commit.getId().get().getValue(),
                new AuthorResource(commit.getAuthor().getDisplayName().toString(),
                        commit.getAuthor().getEmail().toString()),
                        commit.getDate().toString(), commit.getMessage().toString());
    }
}
