package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.ContentFragment;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.Score;
import com.cloudogu.smeagol.wiki.domain.SearchResult;
import net.bytebuddy.utility.RandomString;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LuceneSearchResultRepositoryTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private LuceneContext context;

    @InjectMocks
    private LuceneSearchResultRepository resultRepository;

    private FSDirectory directory;

    @Before
    public void setUp() throws IOException {
        File indexDirectory = temporaryFolder.newFolder();
        directory = FSDirectory.open(indexDirectory.toPath());
        Document doc = createSampleDocument();
        addDocumentToIndex(doc);

        when(context.createAnalyzer()).thenReturn(new StandardAnalyzer());
        when(context.createReader(WIKI_ID_42)).then((Answer<IndexReader>) invocationOnMock -> DirectoryReader.open(directory));
    }

    private void addDocumentToIndex(Document document) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            writer.addDocument(document);
        }
    }

    private Document createSampleDocument() {
        String longGeneratedString = RandomString.make(60000);
        longGeneratedString += "Hitchers guide to the Galaxy";

        return createDocument("docs/Home", longGeneratedString, "don't panic");
    }

    private Document createDocument(String path, String content, String message) {
        Document doc = new Document();
        doc.add(new StringField(LuceneFields.PATH, path, Field.Store.YES));
        doc.add(new TextField(LuceneFields.CONTENT, content, Field.Store.YES));
        doc.add(new TextField(LuceneFields.MESSAGE, message, Field.Store.YES));
        return doc;
    }

    @Test
    public void testSearch() {
        Iterable<SearchResult> results = resultRepository.search(WIKI_ID_42, "content:guide");
        Iterator<SearchResult> iterator = results.iterator();
        assertThat(iterator.hasNext()).isTrue();
        SearchResult result = iterator.next();
        assertThat(result.getPath()).isEqualTo(Path.valueOf("docs/Home"));
        assertThat(result.getScore().getValue()).isGreaterThan(0f);
        assertThat(result.getContentFragment().getValue()).containsIgnoringCase("guide");
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void testSearchMultipleResults() throws IOException {
        Document doc = createDocument("docs/Galaxy", "A Guide to the galaxy", "keep calm");
        addDocumentToIndex(doc);

        SearchResult resultHome = new SearchResult(WIKI_ID_42, Path.valueOf("docs/Home"), Score.valueOf(42), ContentFragment.valueOf("as"));
        SearchResult resultGalaxy = new SearchResult(WIKI_ID_42, Path.valueOf("docs/Galaxy"), Score.valueOf(42), ContentFragment.valueOf("as"));

        Iterable<SearchResult> results = resultRepository.search(WIKI_ID_42, "content:guide");
        assertThat(results).containsExactlyInAnyOrder(resultHome, resultGalaxy);
    }

    @Test
    public void testSearchNotFound() {
        Iterable<SearchResult> results = resultRepository.search(WIKI_ID_42, "content:sorbot");
        assertThat(results).isEmpty();
    }

}
