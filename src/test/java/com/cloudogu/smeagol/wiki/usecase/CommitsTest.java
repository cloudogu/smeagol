package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.Message;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommitsTest {

    @Test
    public void testCreateNewCommit() {
        String userName = "arthur dent";
        String displayName = "arthur";
        String email = "arthur@de.nt";
        Message message = Message.valueOf("hitchhiker");
        Instant beforeCommit = Instant.now();

        Account acc = new Account(userName, displayName, email);
        AccountService as = mock(AccountService.class);
        when(as.get()).thenReturn(acc);
        Commit commit = Commits.createNewCommit(as, message);
        Instant afterCommit = Instant.now();

        assertEquals(message, commit.getMessage());
        assertEquals(displayName, commit.getAuthor().getDisplayName().getValue());
        assertEquals(email, commit.getAuthor().getEmail().getValue());
        assertTrue(! commit.getDate().isBefore(beforeCommit) && ! commit.getDate().isAfter(afterCommit));
    }
}