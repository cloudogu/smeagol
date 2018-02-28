package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Handler for {@link DeletePageCommand}.
 */
@Component
public class DeletePageCommandHandler implements CommandHandler<Void, DeletePageCommand> {

    private final ApplicationEventPublisher publisher;
    private final PageRepository repository;
    private final AccountService accountService;

    @Autowired
    public DeletePageCommandHandler(ApplicationEventPublisher publisher, PageRepository repository, AccountService accountService) {
        this.publisher = publisher;
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Void handle(DeletePageCommand command) {
        Path path = command.getPath();
        Page page = repository.findByWikiIdAndPath(command.getWikiId(), path)
                .orElseThrow(() -> new PageNotFoundException(path, "page not found"));

        Commit commit = createNewCommit(command.getMessage());

        repository.delete(page, commit);

        publisher.publishEvent(new PageDeletedEvent(page));
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
