package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static com.cloudogu.smeagol.wiki.DomainTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LuceneIndexerTest {

    @Mock
    private LuceneContext context;

    @InjectMocks
    private LuceneIndexer indexer;

    private IndexWriter writer;
    private RAMDirectory ramDirectory = new RAMDirectory();

    @Before
    public void setUp() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        writer = new IndexWriter(ramDirectory, config);

        when(context.openWriter(WIKI_ID_42)).thenReturn(writer);
    }

    @After
    public void tearDown() throws IOException {
        writer.close();
    }

    @Test
    public void testHandlePageCreatedEvent() throws IOException {
        indexer.handle(new PageCreatedEvent(PAGE));
        assertDocument();
    }

    @Test
    public void testHandlePageModifiedEvent() throws IOException {
        addDocument("docs/Home", "Strange content");

        indexer.handle(new PageModifiedEvent(PAGE));

        assertDocument();
    }

    @Test
    public void testHandlePageDeletedEvent() throws IOException {
        addDocument("docs/Home", "Strange content");

        indexer.handle(new PageDeletedEvent(WIKI_ID_42, PATH_HOME));
        assertDocumentNotFound("docs/Home");
    }

    private void addDocument(String path, String content) throws IOException {
        Document document = new Document();
        document.add(new StringField(LuceneFields.PATH, path, Field.Store.YES));
        document.add(new TextField(LuceneFields.CONTENT, content, Field.Store.YES));
        writer.addDocument(document);
    }

    @Test
    public void testHandlePageBatchEvent() throws IOException {
        addDocument("docs/Galaxy", "Strange content");
        addDocument("docs/ShouldBeRemoved", "Strange content");

        PageBatchEvent event = new PageBatchEvent(WIKI_ID_42);
        event.added(PAGE);

        Page page = new Page(WIKI_ID_42, Path.valueOf("docs/Galaxy"), Content.valueOf("Hitchhikers Guide"));
        event.modified(page);
        event.deleted(Path.valueOf("docs/ShouldBeRemoved"));

        indexer.handle(event);

        assertDocument();
        assertDocument("docs/Galaxy", "Hitchhikers Guide");
        assertDocumentNotFound("docs/ShouldBeRemoved");
    }

    private void assertDocumentNotFound(String path) throws IOException {
        try(IndexReader reader = DirectoryReader.open(ramDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new TermQuery(new Term("path", path));
            TopDocs docs = searcher.search(query, 1);
            assertThat(docs.totalHits).isZero();
        }
    }

    private void assertDocument() throws IOException {
        assertDocument("docs/Home", CONTENT_GUIDE.getValue(), MESSAGE_PANIC.getValue());
    }

    private void assertDocument(String path, String content) throws IOException {
        assertDocument(path, content, null);
    }

    private void assertDocument(String path, String content, String message) throws IOException {
        try(IndexReader reader = DirectoryReader.open(ramDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new TermQuery(new Term("path", path));
            TopDocs docs = searcher.search(query, 1);
            Document document = reader.document(docs.scoreDocs[0].doc);

            assertThat(document.get("path")).isEqualTo(path);
            assertThat(document.get("content")).isEqualTo(content);
            assertThat(document.get("message")).isEqualTo(message);
        }
    }

}
