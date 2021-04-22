package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.Message;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.cloudogu.smeagol.wiki.domain.WikiSettings;
import de.triology.cb.Command;

/**
 * Command to edit a wiki.
 */
public class EditWikiCommand implements Command<Void> {

    private final WikiId wikiId;
    private final Message message;
    private final WikiSettings settings;

    public EditWikiCommand(WikiId wikiId, Message message, WikiSettings settings) {
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
