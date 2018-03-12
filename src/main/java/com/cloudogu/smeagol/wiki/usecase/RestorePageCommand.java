package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.Command;

/**
 * Command to restore a page.
 */
public class RestorePageCommand implements Command<Page> {

    private final WikiId wikiId;
    private final Path path;
    private final CommitId commitId;
    private final Message message;

    /**
     * Restores an existing wiki page.
     *
     * @param wikiId id of wiki
     * @param path of the page
     * @param commitId of the commit which should be restored
     * @param message of the commit
     */
    public RestorePageCommand(WikiId wikiId, Path path, CommitId commitId, Message message) {
        this.wikiId = wikiId;
        this.path = path;
        this.commitId = commitId;
        this.message = message;
    }

    public WikiId getWikiId() {
        return wikiId;
    }

    public Path getPath() {
        return path;
    }

    public CommitId getCommitId() {
        return commitId;
    }

    public Message getMessage() {
        return message;
    }
}
