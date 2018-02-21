package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.Command;

/**
 * Command to edit a page.
 */
public class EditPageCommand implements Command<Page> {

    private final WikiId wikiId;
    private final Path path;
    private final Message message;
    private final Content content;

    /**
     * Modifies an existing wiki page.
     *
     * @param wikiId id of wiki
     * @param path of the page
     * @param content new content
     */
    public EditPageCommand(WikiId wikiId, Path path, Message message, Content content) {
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

    public Content getContent() {
        return content;
    }

    public Message getMessage() {
        return message;
    }
}
