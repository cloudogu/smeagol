package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.Wiki;
import com.cloudogu.smeagol.wiki.domain.WikiRepository;
import de.triology.cb.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cloudogu.smeagol.wiki.usecase.Commits.createNewCommit;

/**
 * Handler for {@link InitWikiCommand}.
 */
@Component
public class InitWikiCommandHandler implements CommandHandler<Wiki, InitWikiCommand> {

    private final WikiRepository repository;
    private final AccountService accountService;

    @Autowired
    public InitWikiCommandHandler(WikiRepository repository, AccountService accountService) {
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Wiki handle(InitWikiCommand command) {
        Commit commit = createNewCommit(accountService, command.getMessage());
        return repository.init(command.getWikiId(), commit, command.getSettings());
    }
}
