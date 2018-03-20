package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;

/**
 * Util class for the ScmGit repositories.
 */
public final class ScmGit {

    public static Commit createCommit(RevCommit revCommit) {
        CommitId id = CommitId.valueOf(revCommit.getId().getName());
        Author author = createAuthor(revCommit.getAuthorIdent());
        Instant lastModified = Instant.ofEpochSecond(revCommit.getCommitTime());
        Message message = Message.valueOf(revCommit.getFullMessage());
        return new Commit(id, lastModified, author, message);
    }

    public static Author createAuthor(PersonIdent ident) {
        return new Author(DisplayName.valueOf(ident.getName()), Email.valueOf(ident.getEmailAddress()));
    }
}
