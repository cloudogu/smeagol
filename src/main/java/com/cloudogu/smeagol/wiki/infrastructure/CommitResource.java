package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

import java.util.Objects;

public class CommitResource extends ResourceSupport {

    private AuthorResource author;
    private String date;
    private String message;

    public CommitResource(AuthorResource author, String date, String message) {
        this.author = author;
        this.date = date;
        this.message = message;
    }

    public AuthorResource getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
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
        if (!super.equals(o)) {
            return false;
        }
        CommitResource that = (CommitResource) o;
        return Objects.equals(author, that.author) &&
                Objects.equals(date, that.date) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), author, date, message);
    }
}
