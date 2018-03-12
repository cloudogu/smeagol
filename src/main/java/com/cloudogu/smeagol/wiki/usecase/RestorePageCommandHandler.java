package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.Page;
import com.cloudogu.smeagol.wiki.domain.PageRepository;
import com.cloudogu.smeagol.wiki.domain.Path;
import de.triology.cb.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cloudogu.smeagol.wiki.usecase.Commits.createNewCommit;

/**
 * Handler for {@link RestorePageCommand}.
 */
@Component
public class RestorePageCommandHandler implements CommandHandler<Page, RestorePageCommand> {

    private final PageRepository repository;
    private final AccountService accountService;

    @Autowired
    public RestorePageCommandHandler(PageRepository repository, AccountService accountService) {
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

        return repository.save(page);
    }
}
