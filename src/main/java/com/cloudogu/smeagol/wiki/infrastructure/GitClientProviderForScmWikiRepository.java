package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Wiki;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class GitClientProviderForScmWikiRepository {

    private final DirectoryResolver directoryResolver;
    private final AccountService accountService;
    private final PullChangesStrategy strategy;

    @Autowired
    public GitClientProviderForScmWikiRepository(DirectoryResolver directoryResolver, PullChangesStrategy strategy, AccountService accountService) {
        this.directoryResolver = directoryResolver;
        this.accountService = accountService;
        this.strategy = strategy;
    }

    public GitClient createGitClient(ApplicationEventPublisher publisher, Wiki wiki) {
        Account account = accountService.get();

        return new GitClient(
            publisher,
            directoryResolver,
            strategy,
            account,
            wiki
        );
    }
}
