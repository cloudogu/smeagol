package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.cloudogu.smeagol.wiki.domain.WikiSettings;

/**
 * Exception is thrown if failed to initialize a wiki.
 */
public class FailedToInitWikiException extends RuntimeException {

    private final WikiId wikiId;
    private final WikiSettings settings;

    public FailedToInitWikiException(WikiId id, WikiSettings settings) {
        super("failed to init wiki");
        this.wikiId = id;
        this.settings = settings;
    }
}
