package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Wiki;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class GitClientProvider {

    private final ApplicationEventPublisher publisher;
    private final DirectoryResolver directoryResolver;
    private final AccountService accountService;
    private final ScmWikiRepository wikiRepository;

    @Autowired
    public GitClientProvider(ApplicationEventPublisher publisher, DirectoryResolver directoryResolver, ScmWikiRepository wikiRepository, AccountService accountService) {
        this.publisher = publisher;
        this.directoryResolver = directoryResolver;
        this.wikiRepository = wikiRepository;
        this.accountService = accountService;
    }

    public GitClient createGitClient(WikiId wikiId) {
        Account account = accountService.get();
        Wiki wiki = wikiRepository.findById(wikiId).get();

        return new GitClient(
                publisher,
                directoryResolver,
                account,
                wiki.getRepositoryUrl(),
                wikiId
        );
    }

}
