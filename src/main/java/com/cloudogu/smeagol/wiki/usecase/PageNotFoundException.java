package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.Path;

/**
 * Exception is throw if a page could not be found.
 */
public class PageNotFoundException extends RuntimeException {
    private final Path path;

    public PageNotFoundException(Path path, String message) {
        super(message);
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
