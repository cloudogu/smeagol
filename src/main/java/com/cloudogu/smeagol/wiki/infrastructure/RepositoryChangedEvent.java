package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.ChangeType;
import com.cloudogu.smeagol.wiki.domain.WikiId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The RepositoryChangedEvent is fired whenever a repository as changed from the outside (git clone or git pull).
 */
public class RepositoryChangedEvent implements Iterable<RepositoryChangedEvent.Change> {

    private List<Change> changes = new ArrayList<>();

    private final WikiId wikiId;

    RepositoryChangedEvent(WikiId wikiId) {
        this.wikiId = wikiId;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public void added(String path) {
        changes.add(new Change(ChangeType.ADDED, path));
    }

    public void deleted(String path) {
        changes.add(new Change(ChangeType.DELETED, path));
    }

    public void modified(String path) {
        changes.add(new Change(ChangeType.MODIFIED, path));
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
        private String path;

        private Change(ChangeType type, String path) {
            this.type = type;
            this.path = path;
        }

        public ChangeType getType() {
            return type;
        }

        public String getPath() {
            return path;
        }
    }

}
