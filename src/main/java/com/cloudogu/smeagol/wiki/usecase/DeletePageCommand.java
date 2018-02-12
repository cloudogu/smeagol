package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.Command;

/**
 * Command to delete a page.
 */
public class DeletePageCommand implements Command<Void> {

    private final WikiId wikiId;
    private final Path path;
    private final Message message;

    public DeletePageCommand(WikiId wikiId, Path path, Message message) {
        this.wikiId = wikiId;
        this.path = path;
        this.message = message;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }

    public Message getMessage() {
        return message;
    }
}
