package com.cloudogu.smeagol.repository.infrastructure;

public class CouldNotReachSCMException extends RuntimeException {
    public CouldNotReachSCMException() {
        super("Could not reach SCM");
    }
}
