package com.cloudogu.smeagol.repository.infrastructure;

public class CouldNotGetSCMRootException extends RuntimeException {
    public CouldNotGetSCMRootException() {
        super("Failed to get scm root endpoint");
    }
}
