package com.cloudogu.smeagol.wiki.usecase;

/**
 * Exception is thrown if failed to initialize a wiki.
 */
public class FailedToInitWikiException extends RuntimeException {

    public FailedToInitWikiException(String message) {
        super(message);
    }

    public FailedToInitWikiException(Throwable cause) {
        super(cause);
     }
}
