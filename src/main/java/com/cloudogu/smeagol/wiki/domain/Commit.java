package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.MoreObjects;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Commit of a source code management system such as git.
 */
public class Commit {

    private Optional<CommitId> id;
    private Instant date;
    private Author author;
    private Message message;

    public Commit(CommitId id, Instant date, Author author, Message message) {
        this(date, author, message);
        this.id = Optional.of(id);
    }

    public Commit(Instant date, Author author, Message message) {
        this.id = Optional.empty();
        this.date = checkNotNull(date, "date is required");
        this.author = checkNotNull(author, "author is required");
        this.message = checkNotNull(message, "message is required");
    }

    public Optional<CommitId> getId() {
        return id;
    }

    public Instant getDate() {
        return date;
    }

    public Author getAuthor() {
        return author;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Commit commit = (Commit) o;
        return Objects.equals(id, commit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("date", date)
                .add("author", author)
                .add("message", message)
                .toString();
    }
}
