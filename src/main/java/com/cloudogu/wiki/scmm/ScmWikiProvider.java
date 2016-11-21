/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 *
 * Copyright notice
 */
package com.cloudogu.wiki.scmm;

import com.cloudogu.wiki.Account;
import com.cloudogu.wiki.Stage;
import com.cloudogu.wiki.Wiki;
import com.cloudogu.wiki.WikiContext;
import com.cloudogu.wiki.WikiContextFactory;
import com.cloudogu.wiki.WikiException;
import com.cloudogu.wiki.WikiNotFoundException;
import com.cloudogu.wiki.WikiOptions;
import com.cloudogu.wiki.WikiProvider;
import com.cloudogu.wiki.WikiServerConfiguration;
import com.cloudogu.wiki.WikiServletFactory;
import com.github.sdorra.milieu.Configurations;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra
 */
public class ScmWikiProvider implements WikiProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ScmWikiProvider.class);

    private final WikiServerConfiguration configuration;
    private final WikiServletFactory servletFactory;
    private final ScmConfiguration scmConfiguration;
    private final ScmWikiListStrategy wikiListStrategy;
    private final ScmBranchListStrategy branchListStrategy;
    private final Cache<String, HttpServlet> servletCache;

    public ScmWikiProvider(WikiServerConfiguration configuration) {
        this.configuration = configuration;
        this.servletFactory = new WikiServletFactory(configuration);
        this.scmConfiguration = Configurations.get(ScmConfiguration.class);
        this.wikiListStrategy = createWikiListStrategy();
        this.servletCache = createServletCache();
        this.branchListStrategy = createScmBranchListStrategy();
    }

    @Override
    public Iterable<Wiki> getAll() {
        return wikiListStrategy.getWikis();
    }

    @Override
    public Iterable<Wiki> getAllBranches(String name) {
        return branchListStrategy.getWikis(name);
    }

    @Override
    public HttpServlet getServlet(String name) {
        HttpServlet servlet;
        if (configuration.getStage() == Stage.DEVELOPMENT) {
            servlet = createServlet(name);
        } else {
            WikiContext context = WikiContextFactory.getInstance().get();
            final HttpSession session = context.getRequest().getSession(true);
            synchronized (session) {
                servlet = (HttpServlet) session.getAttribute("servlet." + name);
                if (servlet == null) {
                    servlet = createServlet(name);
                    session.setAttribute("servlet." + name, servlet);
                }
            }
        }

        return servlet;
    }

    @Override
    public void push(String wiki, String sha1) {
        File directory = getRepositoryDirectory(wiki);
        int index = wiki.indexOf('/');
        String branch = wiki.substring(index + 1);
        try {
            Git git = Git.open(directory);

            WikiContext context = WikiContextFactory.getInstance().get();
            Account account = context.getAccount();

            CredentialsProvider credentials = credentialsProvider(account);

            LOG.debug("pull changes from remote for wiki {} on branch {}", wiki, branch);
            git.pull()
                    .setRemote("origin")
                    .setCredentialsProvider(credentials)
                    .call();

            LOG.info("push changes to remote for wiki {} on branch {}", wiki, branch);
            git.push()
                    .setRemote("origin")
                    .setCredentialsProvider(credentials)
                    .call();

        } catch (GitAPIException | IOException ex) {
            throw new WikiException("failed to push changes back to remote repository", ex);
        }
    }

    private HttpServlet createServlet(String name) {
        LOG.trace("try to create servlet for scm repository {}", name);
        int index = name.indexOf('/');
        String branch = name.substring(index + 1);
        WikiContext context = WikiContextFactory.getInstance().get();
        Account account = context.getAccount();

        ScmWiki wiki = ScmManager.getWiki(account, scmConfiguration.getInstanceUrl(), name);

        if (wiki == null) {
            throw new WikiNotFoundException("could not find wiki with name ".concat(name));
        }

        if (Strings.isNullOrEmpty(wiki.getRemoteUrl())) {
            throw new WikiException(String.format("wiki %s does not return remote url", name));
        }

        try { //TODO: decrease size of try-catch-block

            File repository = getRepositoryDirectory(name);

            Git git;
            if (!repository.exists()) {
                LOG.info("init repository {} for wiki {}", repository, name);
                git = Git.init().setDirectory(repository).call();
                RemoteAddCommand add = git.remoteAdd();
                add.setName("origin");
                add.setUri(new URIish(wiki.getRemoteUrl()));
                add.call();

                git.pull()
                        .setRemote("origin")
                        .setRemoteBranchName(branch)
                        .setCredentialsProvider(credentialsProvider(account))
                        .call();

                LOG.debug("checkout branch {}", branch);
                git.branchCreate()
                        .setName(branch)
                        .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
                        .setStartPoint("origin/" + branch)
                        .setForce(true)
                        .call();

                git.checkout()
                        .setName(branch)
                        .call();

            } else {
                LOG.trace("open repository {} for wiki {}", repository, name);
                git = Git.open(repository);
            }

            LOG.debug("pull changes from remote for wiki {}", name);

            git.pull()
                    .setRemote("origin")
                    .setRemoteBranchName(branch)
                    .setCredentialsProvider(credentialsProvider(account))
                    .call();

            WikiOptions options = WikiOptions.builder(repository.getAbsolutePath()).build();

            return servletCache.get(name, () -> {
                return servletFactory.create(wiki, options);
            });
        } catch (IOException | GitAPIException | URISyntaxException | ExecutionException ex) {
            throw new WikiException("failed to create or update wiki ".concat(name), ex);
        }
    }

    private ScmWikiListStrategy createWikiListStrategy() {
        ScmWikiListStrategy strategy;
        if (configuration.getStage() == Stage.DEVELOPMENT) {
            LOG.warn("use non caching wiki list strategy for development");
            strategy = new DevelopmentScmWikiListStrategy(scmConfiguration);
        } else {
            strategy = new SessionCacheScmWikiListStrategy(scmConfiguration);
        }
        return strategy;
    }

    private ScmBranchListStrategy createScmBranchListStrategy() {
        ScmBranchListStrategy strategy;
        if (configuration.getStage() == Stage.DEVELOPMENT) {
            LOG.warn("use non caching branch list strategy for development");
            strategy = new DevelopmentScmBranchListStrategy(scmConfiguration);
        } else {
            strategy = new SessionCacheScmBranchListStrategy(scmConfiguration);
        }
        return strategy;
    }

    private Cache<String, HttpServlet> createServletCache() {
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder();
        if (configuration.getStage() == Stage.DEVELOPMENT) {
            LOG.warn("disable servlet cache for development");
            cacheBuilder = cacheBuilder.expireAfterAccess(10, TimeUnit.SECONDS);
        }
        return cacheBuilder.build();
    }

    private CredentialsProvider credentialsProvider(Account account) {
        return new UsernamePasswordCredentialsProvider(account.getUsername(), account.getPassword());
    }

    private File getRepositoryDirectory(String name) {
        File directory = new File(configuration.getHomeDirectory());
        if (!directory.exists() && !directory.mkdirs()) {
            throw new WikiException("could not create smeagol directory");
        }
        return new File(directory, name);
    }

}
