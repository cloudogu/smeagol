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
public class EditPageCommandHandlerTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private EditPageCommandHandler commandHandler;

    @Test
    public void testEdit() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        Path path = Path.valueOf("Home");
        Page page = new Page(id, path, Content.valueOf("Old Content"));

        when(pageRepository.findByWikiIdAndPath(id, path)).thenReturn(Optional.of(page));

        Message message = Message.valueOf("hitchhiker is awesome");
        Content newContent = Content.valueOf("New Content");

        commandHandler.handle(new EditPageCommand(id, path, message, newContent));

        assertEquals(newContent, page.getContent());
        assertEquals(message, page.getCommit().get().getMessage());
        assertEquals("Tricia McMillan", page.getCommit().get().getAuthor().getDisplayName().getValue());

        verify(pageRepository).save(page);
    }

    @Test(expected = PageNotFoundException.class)
    public void testEditNotFound() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        Path path = Path.valueOf("Home");

        when(pageRepository.findByWikiIdAndPath(id, path)).thenReturn(Optional.empty());

        Message message = Message.valueOf("hitchhiker is awesome");
        Content newContent = Content.valueOf("New Content");

        commandHandler.handle(new EditPageCommand(id, path, message, newContent));
    }

}