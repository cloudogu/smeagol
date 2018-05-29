package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.apache.lucene.index.IndexWriter;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;

public class LuceneContextTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private LuceneContext context;
    private File home;

    @Before
    public void setUp() throws IOException {
        home = temporaryFolder.newFolder();
        context = new LuceneContext(new DirectoryResolver(home.getPath()));
    }

    @Test
    public void testOpenNewWriter() throws IOException {
        String path = MessageFormat.format("42{0}galaxy{0}.git", File.separator);
        File dotGit = new File(home, path);

        IndexWriter writer = context.openWriter(WIKI_ID_42);
        writer.close();

        File indexDir = new File(dotGit, "search-index");
        assertThat(indexDir.exists()).isTrue();
    }

    @Test
    public void testOpenWriterOnlyOncePerWiki() throws IOException {
        try (
            IndexWriter writer1 = context.openWriter(WIKI_ID_42);
            IndexWriter writer2 = context.openWriter(WIKI_ID_42)
        ) {
            assertThat(writer1).isSameAs(writer2);
        }
    }

    @Test
    public void testOpenWriterClosedBefore() throws IOException {
        IndexWriter writer = context.openWriter(WIKI_ID_42);
        assertThat(writer.isOpen()).isTrue();
        writer.close();
        assertThat(writer.isOpen()).isFalse();

        // reopen
        try (IndexWriter writer2 = context.openWriter(WIKI_ID_42)) {
            assertThat(writer2.isOpen()).isTrue();
        }

    }

    @Test
    public void testCloseWriters() {
        IndexWriter master = context.openWriter(new WikiId("42", "master"));
        IndexWriter develop = context.openWriter(new WikiId("42", "develop"));
        IndexWriter featureOne = context.openWriter(new WikiId("42", "feature/one"));

        context.closeWriters();

        assertThat(master.isOpen()).isFalse();
        assertThat(develop.isOpen()).isFalse();
        assertThat(featureOne.isOpen()).isFalse();
    }

}