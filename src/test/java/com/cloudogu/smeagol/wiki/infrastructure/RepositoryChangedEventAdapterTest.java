package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.ChangeType;
import com.cloudogu.smeagol.wiki.domain.PageBatchEvent;
import com.cloudogu.smeagol.wiki.domain.PageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Iterator;
import java.util.Optional;

import static com.cloudogu.smeagol.wiki.DomainTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryChangedEventAdapterTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private PageRepository pageRepository;

    @InjectMocks
    private RepositoryChangedEventAdapter adapter;

    @Captor
    private ArgumentCaptor<PageBatchEvent> eventCaptor;

    @Test
    public void testHandle() {
        RepositoryChangedEvent repositoryChangedEvent = new RepositoryChangedEvent(WIKI_ID_42);
        repositoryChangedEvent.added("docs/Home.md");
        repositoryChangedEvent.modified("docs/Home.md");
        repositoryChangedEvent.deleted("docs/Home.md");

        when(pageRepository.findByWikiIdAndPath(WIKI_ID_42, PATH_HOME)).thenReturn(Optional.ofNullable(PAGE));

        adapter.handle(repositoryChangedEvent);

        verify(publisher).publishEvent(eventCaptor.capture());

        PageBatchEvent event = eventCaptor.getValue();
        Iterator<PageBatchEvent.Change> iterator = event.iterator();

        PageBatchEvent.Change change = iterator.next();
        assertThat(change.getType()).isSameAs(ChangeType.ADDED);
        assertThat(change.getPath()).isEqualTo(PATH_HOME);
        assertThat(change.getPage().get()).isSameAs(PAGE);

        change = iterator.next();
        assertThat(change.getType()).isSameAs(ChangeType.MODIFIED);
        assertThat(change.getPath()).isEqualTo(PATH_HOME);
        assertThat(change.getPage().get()).isSameAs(PAGE);

        change = iterator.next();
        assertThat(change.getType()).isSameAs(ChangeType.DELETED);
        assertThat(change.getPath()).isEqualTo(PATH_HOME);
        assertThat(change.getPage().isPresent()).isFalse();

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void testHandleWithNonExistingPage() {
        RepositoryChangedEvent repositoryChangedEvent = new RepositoryChangedEvent(WIKI_ID_42);
        repositoryChangedEvent.added("docs/Home.md");

        when(pageRepository.findByWikiIdAndPath(WIKI_ID_42, PATH_HOME)).thenReturn(Optional.empty());

        adapter.handle(repositoryChangedEvent);

        verify(publisher).publishEvent(eventCaptor.capture());

        PageBatchEvent event = eventCaptor.getValue();
        assertThat(event.isEmpty()).isTrue();
    }

}