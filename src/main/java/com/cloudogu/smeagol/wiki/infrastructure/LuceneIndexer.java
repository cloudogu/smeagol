package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.google.common.base.Throwables;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * LuceneIndexer captures all page related domain event and creates a search index.
 */
@Service
public class LuceneIndexer {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneIndexer.class);

    private final LuceneContext context;

    @Autowired
    public LuceneIndexer(LuceneContext context) {
        this.context = context;
    }

    @EventListener
    public void handle(PageCreatedEvent event) {
        Page page = event.getPage();
        IndexWriter writer = context.openWriter(page.getWikiId());

        try {
            addPageToIndex(writer, page);
            writer.commit();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void addPageToIndex(IndexWriter writer, Page page) throws IOException {
        LOG.debug("add page {} to index {}", page.getPath(), page.getWikiId());
        writer.addDocument(createDocumentFrom(page));
    }

    private Document createDocumentFrom(Page page) {
        Document doc = new Document();
        doc.add(new StringField(LuceneFields.PATH, page.getPath().getValue(), Field.Store.YES));
        doc.add(new TextField(LuceneFields.CONTENT, page.getContent().getValue(), Field.Store.YES));
        if (page.getCommit().isPresent()) {
            Commit commit = page.getCommit().get();
            doc.add(new TextField(LuceneFields.MESSAGE, commit.getMessage().getValue(), Field.Store.YES));
        }
        return doc;
    }

    @EventListener
    public void handle(PageModifiedEvent event) {
        Page page = event.getPage();
        IndexWriter writer = context.openWriter(page.getWikiId());
        try {
            updateIndexedPage(writer, page);
            writer.commit();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void updateIndexedPage(IndexWriter writer, Page page) throws IOException {
        LOG.debug("update page {} of index {}", page.getPath(), page.getWikiId());
        writer.updateDocument(createPathTerm(page.getPath()), createDocumentFrom(page));
    }

    private Term createPathTerm(Path path) {
        return new Term(LuceneFields.PATH, path.getValue());
    }

    @EventListener
    public void handle(PageDeletedEvent event) {
        IndexWriter writer = context.openWriter(event.getWikiId());
        try {
            deleteIndexedPage(writer, event.getWikiId(), event.getPath());
            writer.commit();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void deleteIndexedPage(IndexWriter writer, WikiId wikiId, Path path) throws IOException {
        LOG.debug("delete page {} from index {}", path, wikiId);
        writer.deleteDocuments(createPathTerm(path));
    }

    @EventListener
    public void handle(PageBatchEvent event) {
        IndexWriter writer = context.openWriter(event.getWikiId());
        try {
            for (PageBatchEvent.Change change : event) {
                handleBatchChange(writer, event.getWikiId(), change);
            }
            writer.commit();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void handleBatchChange(IndexWriter writer, WikiId wikiId, PageBatchEvent.Change change) throws IOException {
        if (change.getType() == ChangeType.ADDED) {
            addPageToIndex(writer, change.getPage().get());
        } else if (change.getType() == ChangeType.DELETED) {
            deleteIndexedPage(writer, wikiId, change.getPath());
        } else {
            updateIndexedPage(writer, change.getPage().get());
        }
    }

    @EventListener
    public void handle(ClearIndexEvent event) {
        LOG.debug("received clear index event {}, removing all documents from search index", event.getWikiId());
        IndexWriter writer = context.openWriter(event.getWikiId());
        try {
            long documents = writer.deleteAll();
            writer.commit();
            LOG.debug("removed {} documents, from search index", documents);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
