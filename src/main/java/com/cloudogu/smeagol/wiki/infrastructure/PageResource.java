package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

public class PageResource extends ResourceSupport {

    private String path;
    private String content;
    private AuthorResource author;
    private String lastModified;

    public PageResource(String path, String content, AuthorResource author, String lastModified) {
        this.path = path;
        this.content = content;
        this.author = author;
        this.lastModified = lastModified;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public AuthorResource getAuthor() {
        return author;
    }

    public String getLastModified() {
        return lastModified;
    }

    public static class AuthorResource {

        private String displayName;
        private String email;

        public AuthorResource(String displayName, String email) {
            this.displayName = displayName;
            this.email = email;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getEmail() {
            return email;
        }
    }

}
