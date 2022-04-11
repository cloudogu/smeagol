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
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreatePageCommandHandlerTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private CreatePageCommandHandler handler;

    @Captor
    private ArgumentCaptor<PageCreatedEvent> eventCaptor;

    // some test data
    private final WikiId id = new WikiId("42", "galaxis");
    private final Path path = Path.valueOf("HeartOfGold");
    private final Message message = Message.valueOf("Don't Panic");
    private final Content content = Content.valueOf("# Hitchhiker Guide to the Galaxy");

    @Test
    public void testHandle() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        CreatePageCommand command = new CreatePageCommand(id, path, message, content);

        // return same page as parameter get
        when(pageRepository.save(any(Page.class)))
                .then((Answer<Page>) invocation -> (Page) invocation.getArguments()[0]);

        Page page = handler.handle(command);

        verify(pageRepository).save(page);

        assertThat(page.getContent()).isEqualTo(content);
        Commit commit = page.getCommit().get();
        assertThat(commit.getMessage()).isEqualTo(message);
        Author author = commit.getAuthor();
        assertThat(author.getDisplayName().getValue()).isEqualTo(AccountTestData.TRILLIAN.getDisplayName());
        assertThat(author.getEmail().getValue()).isEqualTo(AccountTestData.TRILLIAN.getMail());

        // check published event
        verify(publisher).publishEvent(eventCaptor.capture());
        PageCreatedEvent event = eventCaptor.getValue();
        assertThat(event.getPage()).isSameAs(page);
    }

    @Test(expected = PageAlreadyExistsException.class)
    public void testHandleWithAlreadyExistingPage() {
        when(pageRepository.exists(id, path)).thenReturn(true);

        CreatePageCommand command = new CreatePageCommand(id, path, message, content);
        handler.handle(command);
    }
}
