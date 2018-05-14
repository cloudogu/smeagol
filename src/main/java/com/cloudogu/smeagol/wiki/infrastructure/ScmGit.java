package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Util class for the ScmGit repositories.
 */
// REVIEW static methods removed for testability
@Component
class ScmGit {

    Commit createCommit(RevCommit revCommit) {
        CommitId id = CommitId.valueOf(revCommit.getId().getName());
        Author author = createAuthor(revCommit.getAuthorIdent());
        Instant lastModified = Instant.ofEpochSecond(revCommit.getCommitTime());
        Message message = Message.valueOf(revCommit.getFullMessage());
        return new Commit(id, lastModified, author, message);
    }

    private Author createAuthor(PersonIdent ident) {
        return new Author(DisplayName.valueOf(ident.getName()), Email.valueOf(ident.getEmailAddress()));
    }
}
