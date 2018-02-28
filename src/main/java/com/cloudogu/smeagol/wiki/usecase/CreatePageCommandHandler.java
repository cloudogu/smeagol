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
 * Handler for {@link CreatePageCommand}.
 */
@Component
public class CreatePageCommandHandler implements CommandHandler<Page, CreatePageCommand> {

    private final ApplicationEventPublisher publisher;
    private final PageRepository repository;
    private final AccountService accountService;

    @Autowired
    public CreatePageCommandHandler(ApplicationEventPublisher publisher, PageRepository repository, AccountService accountService) {
        this.publisher = publisher;
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Page handle(CreatePageCommand command) {
        if (repository.exists(command.getWikiId(), command.getPath())) {
            throw new PageAlreadyExistsException(command.getPath(), "the page already exists");
        }

        Commit commit = createNewCommit(command.getMessage());
        Page page = new Page(command.getWikiId(), command.getPath(), command.getContent(), commit);

        Page createdPage = repository.save(page);

        publisher.publishEvent(new PageCreatedEvent(createdPage));

        return createdPage;
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
