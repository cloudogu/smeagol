package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.Content;
import com.cloudogu.smeagol.wiki.domain.Message;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import de.triology.cb.Command;

/**
 * Command to create or edit a page.
 */
public class EditOrCreatePageCommand implements Command<Void> {

    private final WikiId wikiId;
    private final Path path;
    private final Content content;
    private final Message message;

    /**
     * Modifies an existing wiki page.
     *
     * @param wikiId id of wiki
     * @param path of the page
     * @param content new content
     */
    public EditOrCreatePageCommand(WikiId wikiId, Path path, Message message, Content content) {
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
