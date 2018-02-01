package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

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
}
