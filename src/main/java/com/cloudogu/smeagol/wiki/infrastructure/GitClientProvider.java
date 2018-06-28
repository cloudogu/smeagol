package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Wiki;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class GitClientProvider {

    private final ApplicationEventPublisher publisher;
    private final DirectoryResolver directoryResolver;
    private final AccountService accountService;
    private final ScmWikiRepository wikiRepository;
    private final PullChangesStrategy strategy;

    @Autowired
    public GitClientProvider(ApplicationEventPublisher publisher, DirectoryResolver directoryResolver, PullChangesStrategy strategy, AccountService accountService, ScmWikiRepository wikiRepository) {
        this.publisher = publisher;
        this.directoryResolver = directoryResolver;
        this.wikiRepository = wikiRepository;
        this.accountService = accountService;
        this.strategy = strategy;
    }

    public GitClient createGitClient(WikiId wikiId) {
        Account account = accountService.get();
        Wiki wiki = wikiRepository
                .findById(wikiId)
                .orElseThrow(() -> new NoSuchElementException("no wiki found with id " + wikiId));

        return new GitClient(
                publisher,
                directoryResolver,
                strategy,
                account,
                wiki
        );
    }

}
