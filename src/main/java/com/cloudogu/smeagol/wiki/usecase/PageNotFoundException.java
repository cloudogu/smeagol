package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.Path;

/**
 * Exception is throw if a page could not be found.
 */
public class PageNotFoundException extends RuntimeException {
    private final Path path;

    // REVIEW individual messages were not used
    public PageNotFoundException(Path path) {
        super("page not found");
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
