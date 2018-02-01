package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Handler for {@link EditOrCreatePageCommand}.
 */
@Component
public class EditOrCreatePageCommandHandler implements CommandHandler<Void, EditOrCreatePageCommand> {

    private final PageRepository repository;
    private final AccountService accountService;

    @Autowired
    public EditOrCreatePageCommandHandler(PageRepository repository, AccountService accountService) {
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Void handle(EditOrCreatePageCommand command) {
        Path path = command.getPath();
        Optional<Page> pageByPath = repository.findByWikiIdAndPath(command.getWikiId(), path);

        Commit commit = createNewCommit(command.getMessage());

        Page page;
        if (pageByPath.isPresent()) {
            page = pageByPath.get();
            page.edit(commit, command.getContent());
        } else {
            page = new Page(command.getWikiId(), path, command.getContent(), commit);
        }

        repository.save(page);

        return null;
    }

    private Commit createNewCommit(Message message) {
        Account account = accountService.get();

        Author author = new Author(
                DisplayName.valueOf(account.getDisplayName()),
                Email.valueOf(account.getMail())
        );

        return new Commit(Instant.now(), author, message);
    }
}
