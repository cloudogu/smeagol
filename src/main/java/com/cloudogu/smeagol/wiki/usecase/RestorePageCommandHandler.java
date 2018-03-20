package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.cloudogu.smeagol.wiki.usecase.Commits.createNewCommit;

/**
 * Handler for {@link RestorePageCommand}.
 */
@Component
public class RestorePageCommandHandler implements CommandHandler<Page, RestorePageCommand> {

    private final PageRepository repository;
    private final AccountService accountService;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public RestorePageCommandHandler(ApplicationEventPublisher publisher, PageRepository repository, AccountService accountService) {
        this.publisher = publisher;
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Page handle(RestorePageCommand command) {
        Path path = command.getPath();
        Page page = repository.findByWikiIdAndPathAndCommit(command.getWikiId(), path, command.getCommitId())
                .orElseThrow(() -> new PageNotFoundException(path, "page not found"));

        Commit commit = createNewCommit(accountService, command.getMessage());
        page.setCommit(commit);

        Page restoredPage = repository.save(page);

        publisher.publishEvent(new PageModifiedEvent(restoredPage));

        return restoredPage;
    }
}
