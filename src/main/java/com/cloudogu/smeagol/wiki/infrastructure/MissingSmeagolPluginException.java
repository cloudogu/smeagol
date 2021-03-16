package com.cloudogu.smeagol.wiki.infrastructure;

public class MissingSmeagolPluginException extends RuntimeException {
    public MissingSmeagolPluginException() {
        super("SCM is missing Smeagol plugin");
    }
}
