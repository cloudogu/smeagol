package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.google.common.base.Throwables;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LuceneContext is responsible for creating writer and reader for lucene operations.
 */
@Component
public class LuceneContext {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneContext.class);

    private final DirectoryResolver directoryResolver;

    private ConcurrentHashMap<WikiId, IndexWriter> writers = new ConcurrentHashMap<>();

    @Autowired
    public LuceneContext(DirectoryResolver directoryResolver) {
        this.directoryResolver = directoryResolver;
    }


    /**
     * Opens an index writer for the given wiki, if no index writer exists a new one will be created.
     *
     * @param wikiId id of wiki
     *
     * @return index writer
     */
    public IndexWriter openWriter(WikiId wikiId) {
        return writers.compute(wikiId, (id, previous) -> {
            if (previous == null) {
                LOG.debug("create new index writer for {}", id);
                return createWriter(id);
            }
            if (!previous.isOpen()) {
                LOG.warn("create new index writer for {}, because the previous one was closed", id);
                return createWriter(id);
            }
            LOG.trace("return previous index writer for {}", id);
            return previous;
        });
    }

    private IndexWriter createWriter(WikiId wikiId) {
        File indexDirectory = directoryResolver.resolveSearchIndex(wikiId);
        try {
            return createWriter(indexDirectory);
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

    /**
     * Close all writers before the component gets destroyed.
     */
    @PreDestroy
    void closeWriters() {
        for (Map.Entry<WikiId, IndexWriter> entry : writers.entrySet()) {
            try {
                LOG.debug("close index writer of {}", entry.getKey());
                entry.getValue().close();
            } catch (IOException e) {
                LOG.warn("failed to close IndexWriter", e);
            }
        }
    }

    /**
     * Creates an index reader for the given wiki.
     *
     * @param wikiId id of wiki
     *
     * @return index reader
     *
     * @throws IOException
     */
    public IndexReader createReader(WikiId wikiId) throws IOException {
        File indexDirectory = directoryResolver.resolveSearchIndex(wikiId);
        return DirectoryReader.open(FSDirectory.open(indexDirectory.toPath()));
    }

    /**
     * Creates an analyzer which can be used for indexing and or searching.
     *
     * @return analyzer
     */
    public Analyzer createAnalyzer() {
        return new StandardAnalyzer();
    }

    private IndexWriter createWriter(File directory) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(createAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(FSDirectory.open(directory.toPath()), config);
    }

}
