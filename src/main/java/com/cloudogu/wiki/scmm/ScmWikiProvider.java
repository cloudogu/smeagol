/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 *
 * Copyright notice
 */
package com.cloudogu.wiki.scmm;

import com.cloudogu.wiki.Account;
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
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import static java.util.Collections.singleton;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
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

        WikiContext context = WikiContextFactory.getInstance().get();
        final HttpSession session = context.getRequest().getSession(true);
        synchronized (session) {
            String cacheKey = createCacheKey(context, name);
            servlet = (HttpServlet) session.getAttribute(cacheKey);
            if (servlet == null) {
                servlet = createServlet(name, cacheKey);
                session.setAttribute(cacheKey, servlet);
            }
        }

        return servlet;
    }

    private String createCacheKey(WikiContext context, String name) {
        return "servlet." + name + "_" + context.getLocale().getLanguage();
    }

    @Override
    public void push(String wiki, String sha1) {
        File directory = getRepositoryDirectory(wiki);
        String branch = getDecodedBranchName(wiki);
        
        try (Git git = Git.open(directory)) {
            WikiContext context = WikiContextFactory.getInstance().get();
            Account account = context.getAccount();
            
            CredentialsProvider credentials = credentialsProvider(account);
            
            LOG.debug("pull changes from remote for wiki {} on branch {}", wiki, branch);
            git.pull()
                    .setRemote("origin")
                    .setRemoteBranchName(branch)
                    .setCredentialsProvider(credentials)
                    .call();

            LOG.info("push changes to remote for wiki {} on branch {}", wiki, branch);
            git.push()
                    .setRemote("origin")                    
                    .setRefSpecs(new RefSpec(branch+":"+branch))
                    .setCredentialsProvider(credentials)
                    .call();

        } catch (GitAPIException | IOException ex) {
            throw new WikiException("failed to push changes back to remote repository", ex);
        }
    }

    private HttpServlet createServlet(String name, String cacheKey) {
        LOG.trace("try to create servlet for scm repository {}", name);
        String repositoryId = getRepositoryId(name);
        String branch = getDecodedBranchName(name);

        WikiContext context = WikiContextFactory.getInstance().get();
        Account account = context.getAccount();

        ScmWiki wiki = ScmManager.getWiki(account, scmConfiguration.getInstanceUrl(), name, this.getAllBranches(repositoryId));

        if (wiki == null) {
            throw new WikiNotFoundException("could not find wiki with name ".concat(name));
        }

        if (Strings.isNullOrEmpty(wiki.getRemoteUrl())) {
            throw new WikiException(String.format("wiki %s does not return " + "remote url", name));
        }
        
        try {

            File repository = getRepositoryDirectory(name);
            if ( repository.exists() ) {
                pullChanges(account, repository, branch);
            } else {
                createClone(wiki, account, repository, branch);
            }
        
            WikiOptions options = WikiOptions.builder(repository.getAbsolutePath()).build();

            return servletCache.get(cacheKey, () -> {
                return servletFactory.create(wiki, options);
            });
        } catch (IOException | GitAPIException | ExecutionException ex) {
            throw new WikiException("failed to create or update wiki ".concat(name), ex);
        }
    }
    
    public void pullChanges(Account account, File direcory, String branch) 
            throws GitAPIException, IOException {
        LOG.trace("open repository {}", direcory);
        try (Git git = Git.open(direcory)) {
            LOG.debug("pull changes from remote for repository {}", direcory);
                git.pull()
                    .setRemote("origin")
                    .setRemoteBranchName(branch)
                    .setCredentialsProvider(credentialsProvider(account))
                    .call();            
        }
    }
    
    private void createClone(ScmWiki wiki, Account account, File direcory, String branch) 
            throws GitAPIException, IOException {
        LOG.info("clone repository {} for wiki {}", direcory, wiki.getName());
        Git.cloneRepository()
                  .setURI(wiki.getRemoteUrl())
                  .setDirectory(direcory)
                  .setBranchesToClone(singleton("refs/head" + branch))
                  .setBranch(branch)
                  .setCredentialsProvider(credentialsProvider(account))
                  .call()
                  .close();
        if (!"master".equals(branch)) {
            File newRef = new File(direcory, ".git/refs/heads/master");
            File refDirectory = newRef.getParentFile();
            if (!refDirectory.exists() && !refDirectory.mkdirs()) {
                throw new IOException("failed to create parent directory " + refDirectory);
            }
            if (!newRef.exists() && !newRef.createNewFile()) {
                throw new IOException("failed to create parent directory");
            }
            try (BufferedWriter output = new BufferedWriter(new FileWriter(newRef))) {
                output.write("ref: refs/heads/" + branch);
            }
        }
    }


    private String getRepositoryId(String wikiName) {
        int index = wikiName.indexOf('/');
        String repository = "";
        if(index > 0) {
            repository = wikiName.substring(0, index);
        }
        return repository;
    }
    
    public static String getDecodedBranchName(String wikiName) {
        String branch = getBranchName(wikiName);
        try {
            branch = URLDecoder.decode(branch, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw Throwables.propagate(e);
        }
        return branch;
    }
    
    public static String getBranchName(String wikiName) {
        int index = wikiName.indexOf('/');
        String branch = wikiName.substring(index + 1);
        return branch;
    }

    private ScmWikiListStrategy createWikiListStrategy() {
        ScmWikiListStrategy strategy = new SessionCacheScmWikiListStrategy(scmConfiguration);
        return strategy;
    }

    private ScmBranchListStrategy createScmBranchListStrategy() {
        ScmBranchListStrategy strategy = new SessionCacheScmBranchListStrategy(scmConfiguration);
        return strategy;
    }

    private Cache<String, HttpServlet> createServletCache() {
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder();
        return cacheBuilder.build();
    }

    public CredentialsProvider credentialsProvider(Account account) {
        return new UsernamePasswordCredentialsProvider(account.getUsername(), account.getPassword());
    }

    public File getRepositoryDirectory(String name) {
        File directory = new File(configuration.getHomeDirectory());
        if (!directory.exists() && !directory.mkdirs()) {
            throw new WikiException("could not create smeagol directory");
        }
        return new File(directory, name);
    }

}
