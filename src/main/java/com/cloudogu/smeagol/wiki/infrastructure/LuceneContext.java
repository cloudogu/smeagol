package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.repository.domain.RepositoryRepository;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.cloudogu.smeagol.wiki.domain.WikiRepository;
import com.google.common.base.Throwables;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LuceneContext is responsible for creating writer and reader for lucene operations.
 */
@Component
public class LuceneContext {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneContext.class);

    private final DirectoryResolver directoryResolver;
    private final RepositoryRepository repositoryRepository;
    private final WikiRepository wikiRepository;

    private ConcurrentHashMap<WikiId, IndexWriter> writers = new ConcurrentHashMap<>();

    @Autowired
    public LuceneContext(DirectoryResolver directoryResolver, WikiRepository wikiRepository, RepositoryRepository repositoryRepository) {
        this.directoryResolver = directoryResolver;
        this.wikiRepository = wikiRepository;
        this.repositoryRepository = repositoryRepository;
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
        final List<IndexReader> readers = new ArrayList<>();

        System.out.println(wikiId);
        this.repositoryRepository.findAll(true).forEach(repository -> {
            final WikiId mainId = new WikiId(repository.getId().getValue(), "main");
            final WikiId masterId = new WikiId(repository.getId().getValue(), "master");
            System.out.println(mainId);
            System.out.println(masterId);
            try {
                File mainDirectory = directoryResolver.resolveSearchIndex(mainId);
                DirectoryReader main = DirectoryReader.open(FSDirectory.open(mainDirectory.toPath()));
                readers.add(main);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                File masterDirectory = directoryResolver.resolveSearchIndex(masterId);
                DirectoryReader master = DirectoryReader.open(FSDirectory.open(masterDirectory.toPath()));
                readers.add(master);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return new MultiReader(readers.toArray(IndexReader[]::new), false);
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
