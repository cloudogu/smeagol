package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.CommandHandler;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.cloudogu.smeagol.wiki.usecase.Commits.createNewCommit;

/**
 * Handler for {@link InitWikiCommand}.
 */
@Component
public class InitWikiCommandHandler implements CommandHandler<Wiki, InitWikiCommand> {

    private final ApplicationEventPublisher publisher;
    private final WikiRepository repository;
    private final AccountService accountService;

    @Autowired
    public InitWikiCommandHandler(ApplicationEventPublisher publisher, WikiRepository repository, AccountService accountService) {
        this.publisher = publisher;
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Wiki handle(InitWikiCommand command) {
        Commit commit = createNewCommit(accountService, command.getMessage());
        Wiki wiki = null;
        try {
            wiki = repository.init(command.getWikiId(), commit, command.getSettings());
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            throw new FailedToInitWikiException(command.getWikiId(), command.getSettings());
        }
        return wiki;
    }
}
