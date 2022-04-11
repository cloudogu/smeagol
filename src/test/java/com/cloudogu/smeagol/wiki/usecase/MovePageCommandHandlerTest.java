package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.AccountTestData;
import com.cloudogu.smeagol.wiki.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MovePageCommandHandlerTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private MovePageCommandHandler commandHandler;

    @Captor
    private ArgumentCaptor<Object> eventCaptor;

    @Test
    public void testMove() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        Path sourcePath = Path.valueOf("Home");
        Path targetPath = Path.valueOf("Target");
        Page page = new Page(id, sourcePath, Content.valueOf("Content"));

        when(pageRepository.findByWikiIdAndPath(id, sourcePath)).thenReturn(Optional.of(page));
        when(pageRepository.save(page)).thenReturn(page);

        Message message = Message.valueOf("hitchhiker is awesome");

        Page newPage = commandHandler.handle(new MovePageCommand(id, sourcePath, targetPath, message));

        assertEquals(message, newPage.getCommit().get().getMessage());
        assertEquals(targetPath, newPage.getPath());
        assertEquals(sourcePath, newPage.getOldPath().get());

        verify(pageRepository).save(page);

        verify(publisher, times(2)).publishEvent(eventCaptor.capture());
        List<Object> events = eventCaptor.getAllValues();

        PageDeletedEvent deletedEvent = (PageDeletedEvent) events.get(0);
        assertEquals(deletedEvent.getPath(), sourcePath);

        PageCreatedEvent createdEvent = (PageCreatedEvent) events.get(1);
        assertEquals(createdEvent.getPage().getPath(), targetPath);
    }

    @Test(expected = PageNotFoundException.class)
    public void testMoveSourceNotFound() {
        WikiId id = new WikiId("123", "master");
        Path sourePath = Path.valueOf("Home");
        Path targetPath = Path.valueOf("Target");

        when(pageRepository.findByWikiIdAndPath(id, sourePath)).thenReturn(Optional.empty());

        Message message = Message.valueOf("hitchhiker is awesome");

        commandHandler.handle(new MovePageCommand(id, sourePath, targetPath, message));
    }

    @Test(expected = PageAlreadyExistsException.class)
    public void testMoveTargetAlreadyExists() {
        WikiId id = new WikiId("123", "master");
        Path sourcePath = Path.valueOf("Home");
        Path targetPath = Path.valueOf("Target");

        Page page = new Page(id, sourcePath, Content.valueOf("Content"));
        when(pageRepository.findByWikiIdAndPath(id, sourcePath)).thenReturn(Optional.of(page));

        Message message = Message.valueOf("hitchhiker is awesome");

        when(pageRepository.exists(id,targetPath)).thenReturn(true);
        commandHandler.handle(new MovePageCommand(id, sourcePath, targetPath, message));
    }
}
