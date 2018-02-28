package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.PageCreatedEvent;
import com.cloudogu.smeagol.wiki.domain.PageDeletedEvent;
import com.cloudogu.smeagol.wiki.domain.PageModifiedEvent;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static com.cloudogu.smeagol.wiki.DomainTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LuceneIndexerTest {

    @Mock
    private LuceneContext context;

    @Mock
    private IndexWriter writer;

    @InjectMocks
    private LuceneIndexer indexer;

    @Captor
    private ArgumentCaptor<Iterable<? extends IndexableField>> documentCaptor;

    @Captor
    private ArgumentCaptor<Term> termCaptor;

    @Before
    public void setUp() throws IOException {
        when(context.createWriter(WIKI_ID_42)).thenReturn(writer);
    }

    @Test
    public void testHandlePageCreatedEvent() throws IOException {
        indexer.handle(new PageCreatedEvent(PAGE));

        verify(writer).addDocument(documentCaptor.capture());
        assertDocument();
    }

    @Test
    public void testHandlePageModifiedEvent() throws IOException {
        indexer.handle(new PageModifiedEvent(PAGE));

        verify(writer).updateDocument(termCaptor.capture(), documentCaptor.capture());
        assertTerm();
        assertDocument();
    }

    @Test
    public void testHandlePageDeletedEvent() throws IOException {
        indexer.handle(new PageDeletedEvent(WIKI_ID_42, PATH_HOME));

        verify(writer).deleteDocuments(termCaptor.capture());

        assertTerm();
    }

    private void assertTerm() {
        Term term = termCaptor.getValue();
        assertThat(term.field()).isEqualTo("path");
        assertThat(term.text()).isEqualTo("docs/Home");
    }

    private void assertDocument() {
        Document document = getCapturedDocument();
        assertThat(document.get("path")).isEqualTo(PATH_HOME.getValue());
        assertThat(document.get("content")).isEqualTo(CONTENT_GUIDE.getValue());
        assertThat(document.get("message")).isEqualTo(MESSAGE_PANIC.getValue());
    }

    private Document getCapturedDocument() {
        return (Document) documentCaptor.getValue();
    }
}