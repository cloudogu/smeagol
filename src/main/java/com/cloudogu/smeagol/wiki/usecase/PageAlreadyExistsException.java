package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.Path;

/**
 * Exception is throw if a page already exists.
 */
public class PageAlreadyExistsException extends RuntimeException {

    private final Path path;

    public PageAlreadyExistsException(Path path, String message) {
        super(message);
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
