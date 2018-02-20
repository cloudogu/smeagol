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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeletePageCommandHandlerTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private DeletePageCommandHandler commandHandler;

    @Test
    public void testDelete() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        Path path = Path.valueOf("Home");
        Page page = new Page(id, path, Content.valueOf("Old Content"));

        when(pageRepository.findByWikiIdAndPath(id, path)).thenReturn(Optional.of(page));

        Message message = Message.valueOf("hitchhiker is awesome");

        commandHandler.handle(new DeletePageCommand(id, path, message));

        verify(pageRepository).delete(eq(page), any(Commit.class));
    }

    @Test(expected = PageNotFoundException.class)
    public void testDeleteNotFound() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        Path path = Path.valueOf("Home");
        Page page = new Page(id, path, Content.valueOf("Old Content"));

        when(pageRepository.findByWikiIdAndPath(id, path)).thenReturn(Optional.empty());

        Message message = Message.valueOf("hitchhiker is awesome");

        commandHandler.handle(new DeletePageCommand(id, path, message));
    }

}