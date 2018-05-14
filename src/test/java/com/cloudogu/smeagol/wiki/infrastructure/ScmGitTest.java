package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Author;
import com.cloudogu.smeagol.wiki.domain.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScmGitTest {
    @Test
    public void testCreateAuthor() {
        String authorName = "Arthur";
        String authorEmail = "arthur@de.nt";
        PersonIdent person = mock(PersonIdent.class);
        when(person.getName()).thenReturn(authorName);
        when(person.getEmailAddress()).thenReturn(authorEmail);

        // REVIEW why only test this small amount?
//        Author author= new ScmGit().createAuthor(person);
//        assertEquals(authorName, author.getDisplayName().getValue());
//        assertEquals(authorEmail, author.getEmail().getValue());
    }
}