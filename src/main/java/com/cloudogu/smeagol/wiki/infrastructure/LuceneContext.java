package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * LuceneContext is responsible for creating writer and reader for lucene operations.
 */
@Component
public class LuceneContext {

    private final String homeDirectory;

    @Autowired
    public LuceneContext(@Value("${homeDirectory}") String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    /**
     * Creates a index writer for the given wiki.
     *
     * @param wikiId id of wiki
     *
     * @return index writer
     *
     * @throws IOException
     */
    public IndexWriter createWriter(WikiId wikiId) throws IOException {
        File indexDirectory = indexDirectory(wikiId);
        return createWriter(indexDirectory);
    }

    private File indexDirectory(WikiId wikiId) {
        return indexDirectory(wikiDirectory(wikiId));
    }

    private File indexDirectory(File repositoryDirectory) {
        File gitDirectory = new File(repositoryDirectory, ".git");
        return new File(gitDirectory, "search-index");
    }

    private File wikiDirectory(WikiId wikiId) {
        File repositoryDirectory = new File(homeDirectory, wikiId.getRepositoryID());
        return new File(repositoryDirectory, wikiId.getBranch());
    }

    private IndexWriter createWriter(File directory) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(FSDirectory.open(directory.toPath()), config);
    }

}
