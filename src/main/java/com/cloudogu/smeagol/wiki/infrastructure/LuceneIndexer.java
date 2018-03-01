package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.google.common.base.Throwables;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * LuceneIndexer captures all page related domain event and creates a search index.
 */
@Service
public class LuceneIndexer {

    private final LuceneContext context;

    @Autowired
    public LuceneIndexer(LuceneContext context) {
        this.context = context;
    }

    @EventListener
    public void handle(PageCreatedEvent event) {
        Page page = event.getPage();
        try (IndexWriter writer = context.createWriter(page.getWikiId())) {
            addPageToIndex(writer, page);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void addPageToIndex(IndexWriter writer, Page page) throws IOException {
        writer.addDocument(createDocumentFrom(page));
    }

    private Document createDocumentFrom(Page page) {
        Document doc = new Document();
        doc.add(new StringField("path", page.getPath().getValue(), Field.Store.YES));
        doc.add(new TextField("content", page.getContent().getValue(), Field.Store.YES));
        if (page.getCommit().isPresent()) {
            doc.add(new TextField("message", page.getCommit().get().getMessage().getValue(), Field.Store.YES));
            // TODO last modified ???
        }
        return doc;
    }

    @EventListener
    public void handle(PageModifiedEvent event) {
        Page page = event.getPage();
        try (IndexWriter writer = context.createWriter(page.getWikiId())) {
            updateIndexedPage(writer, page);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void updateIndexedPage(IndexWriter writer, Page page) throws IOException {
        writer.updateDocument(createPathTerm(page.getPath()), createDocumentFrom(page));
    }

    private Term createPathTerm(Path path) {
        return new Term("path", path.getValue());
    }

    @EventListener
    public void handle(PageDeletedEvent event) {
        try (IndexWriter writer = context.createWriter(event.getWikiId())) {
            deleteIndexedPage(writer, event.getPath());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void deleteIndexedPage(IndexWriter writer, Path path) throws IOException {
        writer.deleteDocuments(createPathTerm(path));
    }

    @EventListener
    public void handle(PageBatchEvent event) {
        try (IndexWriter writer = context.createWriter(event.getWikiId())) {
            for (PageBatchEvent.Change change : event) {
                handleBatchChange(writer, change);
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void handleBatchChange(IndexWriter writer, PageBatchEvent.Change change) throws IOException {
        if (change.getType() == ChangeType.ADDED) {
            addPageToIndex(writer, change.getPage().get());
        } else if (change.getType() == ChangeType.DELETED) {
            deleteIndexedPage(writer, change.getPath());
        } else {
            updateIndexedPage(writer, change.getPage().get());
        }
    }

}
