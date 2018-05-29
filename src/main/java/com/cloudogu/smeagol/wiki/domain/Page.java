package com.cloudogu.smeagol.wiki.domain;

import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The page aggregate represents a single wiki page.
 */
public class Page {

    private WikiId wikiId;
    private Path path;
    private Content content;
    private Commit commit;

    private Path oldPath;

    public Page(WikiId wikiId, Path path, Content content) {
        this(wikiId, path, content, null);
    }

    public Page(WikiId wikiId, Path path, Path oldPath, Content content, Commit commit) {
        this.wikiId = wikiId;
        this.path = path;
        this.oldPath = oldPath;
        this.content = content;
        this.commit = commit;
    }

    public Page(WikiId wikiId, Path path, Content content, Commit commit) {
        this.wikiId = wikiId;
        this.path = checkPath(path);
        this.content = content;
        this.commit = commit;
    }

    public void edit(Commit commit, Content content) {
        this.commit = checkNotNull(commit, "commit is required for edit");
        this.content = checkNotNull(content, "content is required for edit");
    }

    public void move(Commit commit, Path newPath) {
        this.commit = commit;
        this.oldPath = path;
        this.path = newPath;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }

    public Content getContent() {
        return content;
    }

    public Optional<Commit> getCommit() {
        return Optional.ofNullable(commit);
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public Optional<Path> getOldPath() {
        return Optional.ofNullable(oldPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Page page = (Page) o;
        return Objects.equals(wikiId, page.wikiId) &&
                Objects.equals(path, page.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wikiId, path);
    }

    private static Path checkPath(Path path) {
        checkArgument(path.isFile(), "path %s is not file", path);
        return path;
    }
}
