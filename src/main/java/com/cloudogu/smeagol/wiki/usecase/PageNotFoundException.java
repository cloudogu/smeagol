package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.Path;

/**
 * The PageNotFoundException is thrown whenever a page could not be found.
 */
public class PageNotFoundException extends RuntimeException {

    private final Path path;

    /**
     * Creates a new exception.
     *
     * @param path path of the non existing page
     */
    public PageNotFoundException(Path path) {
        this.path = path;
    }

    /**
     * Returns the page of the non existing page.
     *
     * @return path of non existing page
     */
    public Path getPath() {
        return path;
    }
}
