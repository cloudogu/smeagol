package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.wiki.domain.Wiki;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class GitClientProvider {

    private AccountService accountService;
    private ScmWikiRepository wikiRepository;
    private String homeDirectory;

    @Autowired
    public GitClientProvider(ScmWikiRepository wikiRepository, AccountService accountService, @Value("${homeDirectory}") String homeDirectory) {
        this.wikiRepository = wikiRepository;
        this.accountService = accountService;
        this.homeDirectory = homeDirectory;
    }

    public GitClient createGitClient(WikiId wikiId) {
        File repository = getRepositoryDirectory(wikiId);
        Account account = accountService.get();
        Wiki wiki = wikiRepository.findById(wikiId).get();
        return new GitClient(account, repository, wiki.getRepositoryUrl(), wikiId.getBranch());
    }


    private File getRepositoryDirectory(WikiId id) {
        File repositoryDirectory = new File(homeDirectory, id.getRepositoryID());
        return new File(repositoryDirectory, id.getBranch());
    }

}
