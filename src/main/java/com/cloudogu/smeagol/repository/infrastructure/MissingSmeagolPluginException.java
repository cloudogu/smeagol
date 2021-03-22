package com.cloudogu.smeagol.repository.infrastructure;

public class MissingSmeagolPluginException extends RuntimeException {
    public MissingSmeagolPluginException() {
        super("SCM is missing Smeagol plugin");
    }
}
