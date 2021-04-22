package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.WikiRepository;
import de.triology.cb.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cloudogu.smeagol.wiki.usecase.Commits.createNewCommit;

/**
 * Handler for {@link EditWikiCommand}.
 */
@Component
public class EditWikiCommandHandler implements CommandHandler<Void, EditWikiCommand> {

    private final WikiRepository repository;
    private final AccountService accountService;

    @Autowired
    public EditWikiCommandHandler(WikiRepository repository, AccountService accountService) {
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Void handle(EditWikiCommand command) {
        Commit commit = createNewCommit(accountService, command.getMessage());
        repository.save(command.getWikiId(), commit, command.getSettings());
        return null;
    }
}
