package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.Command;

/**
 * Command to move a page.
 */
public class MovePageCommand implements Command<Page> {

    private final WikiId wikiId;
    private final Path source;
    private final Path target;
    private final Message message;

    /**
     * Modifies an existing wiki page.
     *
     * @param wikiId id of wiki
     * @param source path of the page
     * @param target new path of the page
     * @param message message of command
     */
    public MovePageCommand(WikiId wikiId, Path source, Path target, Message message) {
        this.wikiId = wikiId;
        this.source = source;
        this.target = target;
        this.message = message;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getSource() {
        return source;
    }

    public Path getTarget() {
        return target;
    }

    public Message getMessage() {
        return message;
    }
}
