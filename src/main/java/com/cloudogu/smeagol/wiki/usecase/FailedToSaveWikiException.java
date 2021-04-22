package com.cloudogu.smeagol.wiki.usecase;

/**
 * Exception is thrown if failed to save a wiki.
 */
public class FailedToSaveWikiException extends RuntimeException {

    public FailedToSaveWikiException(String message) {
        super(message);
    }

    public FailedToSaveWikiException(Throwable cause) {
        super(cause);
    }
}
