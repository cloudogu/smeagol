package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.Command;

/**
 * Command to create a new page.
 */
public class CreatePageCommand implements Command<Page> {

    private final WikiId wikiId;
    private final Path path;
    private final Message message;
    private final Content content;

    public CreatePageCommand(WikiId wikiId, Path path, Message message, Content content) {
        this.wikiId = wikiId;
        this.path = path;
        this.message = message;
        this.content = content;
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

    public Content getContent() {
        return content;
    }
}
