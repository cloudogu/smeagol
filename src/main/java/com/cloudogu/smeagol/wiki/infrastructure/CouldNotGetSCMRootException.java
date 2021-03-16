package com.cloudogu.smeagol.wiki.infrastructure;

public class CouldNotGetSCMRootException extends RuntimeException {
    public CouldNotGetSCMRootException() {
        super("Failed to get scm root endpoint");
    }
}
