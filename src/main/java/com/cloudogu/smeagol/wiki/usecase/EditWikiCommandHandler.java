package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.WikiRepository;
import de.triology.cb.CommandHandler;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.cloudogu.smeagol.wiki.usecase.Commits.createNewCommit;

/**
 * Handler for {@link EditWikiCommand}.
 */
@Component
public class EditWikiCommandHandler implements CommandHandler<Void, EditWikiCommand> {

    private final ApplicationEventPublisher publisher;
    private final WikiRepository repository;
    private final AccountService accountService;

    @Autowired
    public EditWikiCommandHandler(ApplicationEventPublisher publisher, WikiRepository repository, AccountService accountService) {
        this.publisher = publisher;
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Void handle(EditWikiCommand command) {
        Commit commit = createNewCommit(accountService, command.getMessage());
        try {
            repository.save(command.getWikiId(), commit, command.getSettings());
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            throw new RuntimeException("");
        }
        return null;
    }
}
