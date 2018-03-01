package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Page;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Collection of page changes.
 */
public class PageBatchEvent implements Iterable<PageBatchEvent.Change> {

    private final WikiId wikiId;
    private List<Change> changes = new ArrayList<>();

    PageBatchEvent(WikiId wikiId) {
        this.wikiId = wikiId;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public void added(Page page) {
        changes.add(new Change(ChangeType.ADDED, page.getPath(), page));
    }

    public void modified(Page page) {
        changes.add(new Change(ChangeType.MODIFIED, page.getPath(), page));
    }

    public void deleted(Path path) {
        changes.add(new Change(ChangeType.DELETED, path));
    }

    public boolean isEmpty() {
        return changes.isEmpty();
    }

    @Override
    public Iterator<Change> iterator() {
        return changes.iterator();
    }

    public class Change {

        private ChangeType type;
        private Path path;
        private Page page;

        private Change(ChangeType type, Path path) {
            this.type = type;
            this.path = path;
        }

        private Change(ChangeType type, Path path, Page page) {
            this.type = type;
            this.path = path;
            this.page = page;
        }

        public ChangeType getType() {
            return type;
        }

        public Path getPath() {
            return path;
        }

        public Optional<Page> getPage() {
            return Optional.ofNullable(page);
        }
    }

}
