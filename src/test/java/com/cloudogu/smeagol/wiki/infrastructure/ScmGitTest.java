package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Author;
import org.eclipse.jgit.lib.PersonIdent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
        Author author= ScmGit.createAuthor(person);
        assertEquals(authorName, author.getDisplayName().getValue());
        assertEquals(authorEmail, author.getEmail().getValue());
    }
}