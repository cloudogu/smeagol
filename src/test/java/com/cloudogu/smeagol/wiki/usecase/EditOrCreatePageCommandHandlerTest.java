package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.repository.infrastructure.AccountTestData;
import com.cloudogu.smeagol.wiki.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EditOrCreatePageCommandHandlerTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private AccountService accountService;

    @Captor
    private ArgumentCaptor<Page> pageCaptor;

    @InjectMocks
    private EditOrCreatePageCommandHandler commandHandler;

    @Test
    public void testEdit() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        Path path = Path.valueOf("Home");
        Page page = new Page(id, path, Content.valueOf("Old Content"));

        when(pageRepository.findByWikiIdAndPath(id, path)).thenReturn(Optional.of(page));

        Message message = Message.valueOf("hitchhiker is awesome");
        Content newContent = Content.valueOf("New Content");

        commandHandler.handle(new EditOrCreatePageCommand(id, path, message, newContent));

        assertEquals(newContent, page.getContent());
        assertEquals(message, page.getCommit().get().getMessage());
        assertEquals("Tricia McMillan", page.getCommit().get().getAuthor().getDisplayName().getValue());

        verify(pageRepository).save(page);
    }

    @Test
    public void testCreate() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        Path path = Path.valueOf("Home");

        when(pageRepository.findByWikiIdAndPath(id, path)).thenReturn(Optional.empty());

        Message message = Message.valueOf("hitchhiker is awesome");
        Content newContent = Content.valueOf("New Content");

        commandHandler.handle(new EditOrCreatePageCommand(id, path, message, newContent));

        verify(pageRepository).save(pageCaptor.capture());

        Page page = pageCaptor.getValue();
        assertEquals(newContent, page.getContent());
        assertEquals(message, page.getCommit().get().getMessage());
        assertEquals("Tricia McMillan", page.getCommit().get().getAuthor().getDisplayName().getValue());
    }

}