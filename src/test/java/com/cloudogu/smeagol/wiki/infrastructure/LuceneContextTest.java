package com.cloudogu.smeagol.wiki.infrastructure;

import org.apache.lucene.index.IndexWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.junit.Assert.*;

public class LuceneContextTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCreateWriter() throws IOException {
        File home = temporaryFolder.newFolder();
        String path = MessageFormat.format("42{0}galaxy{0}.git", File.separator);
        File dotGit = new File(home, path);

        LuceneContext context = new LuceneContext(home.getPath());
        IndexWriter writer = context.createWriter(WIKI_ID_42);
        writer.close();

        File indexDir = new File(dotGit, "search-index");
        assertTrue(indexDir.exists());
    }

}