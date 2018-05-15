package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.*;
import de.triology.cb.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.cloudogu.smeagol.wiki.usecase.Commits.createNewCommit;

/**
 * Handler for {@link MovePageCommand}.
 */
@Component
public class MovePageCommandHandler implements CommandHandler<Page, MovePageCommand> {

    private final ApplicationEventPublisher publisher;
    private final PageRepository repository;
    private final AccountService accountService;

    @Autowired
    public MovePageCommandHandler(ApplicationEventPublisher publisher, PageRepository repository, AccountService accountService) {
        this.publisher = publisher;
        this.repository = repository;
        this.accountService = accountService;
    }

    @Override
    public Page handle(MovePageCommand command) {
        Path source = command.getSource();
        Page page = repository.findByWikiIdAndPath(command.getWikiId(), source)
                .orElseThrow(() -> new PageNotFoundException(source));

        Path target = command.getTarget();
        if (repository.exists(command.getWikiId(), target)) {
            throw new PageAlreadyExistsException(target, "the page already exists");
        }

        Commit commit = createNewCommit(accountService, command.getMessage());

        page.move(commit, target);

        Page movedPage = repository.save(page);

        publisher.publishEvent(new PageDeletedEvent(command.getWikiId(), source));
        publisher.publishEvent(new PageCreatedEvent(movedPage));

        return movedPage;
    }
}
