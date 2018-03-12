package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.AccountTestData;
import com.cloudogu.smeagol.wiki.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestorePageCommandHandlerTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private RestorePageCommandHandler commandHandler;

    private final CommitId commitId = CommitId.valueOf("42");

    @Test
    public void testRestore() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);
        WikiId id = new WikiId("123", "master");
        Path path = Path.valueOf("Home");
        Page page = new Page(id, path, Content.valueOf("Old Content"));

        when(pageRepository.findByWikiIdAndPathAndCommit(id, path, commitId)).thenReturn(Optional.of(page));

        Message message = Message.valueOf("hitchhiker is awesome");

        commandHandler.handle(new RestorePageCommand(id, path, commitId, message));

        assertEquals(message, page.getCommit().get().getMessage());
        assertEquals("Tricia McMillan", page.getCommit().get().getAuthor().getDisplayName().getValue());

        verify(pageRepository).save(page);
    }

    @Test(expected = PageNotFoundException.class)
    public void testRestoreNotFound() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        Path path = Path.valueOf("Home");

        when(pageRepository.findByWikiIdAndPathAndCommit(id, path, commitId)).thenReturn(Optional.empty());

        Message message = Message.valueOf("hitchhiker is awesome");

        commandHandler.handle(new RestorePageCommand(id, path, commitId, message));
    }

}