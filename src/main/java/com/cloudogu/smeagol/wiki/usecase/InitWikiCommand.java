package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.Command;

/**
 * Command to init a new wiki.
 */
public class InitWikiCommand implements Command<Wiki> {

    private final WikiId wikiId;
    private final Message message;
    private final WikiSettings settings;

    public InitWikiCommand(WikiId wikiId, Message message, WikiSettings settings) {
        this.wikiId = wikiId;
        this.message = message;
        this.settings = settings;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Message getMessage() {
        return message;
    }

    public WikiSettings getSettings() {
        return settings;
    }
}
