package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.Account;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;

import static java.util.Collections.singleton;

@SuppressWarnings("squid:S1160") // ignore multiple exception rule
public class GitClient implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(GitClient.class);

    private final Account account;
    private final File repository;
    private final URL remoteUrl;
    private final String branch;

    private Git gitRepository;

    public GitClient(Account account, File repository, URL remoteUrl, String branch) {
        this.account = account;
        this.repository = repository;
        this.remoteUrl = remoteUrl;
        this.branch = branch;
    }

    public void refresh() throws GitAPIException, IOException {
        if ( repository.exists() ) {
            pullChanges();
        } else {
            createClone();
        }
    }

    private Git open() throws IOException {
        if (gitRepository == null) {
            gitRepository = Git.open(repository);
        }
        return gitRepository;
    }

    public File file(String path) {
        return new File(repository, path);
    }

    public Optional<RevCommit> lastCommit(String path) throws IOException, GitAPIException {
        Git git = open();
        Iterator<RevCommit> iterator = git.log()
                .addPath(path)
                .setMaxCount(1)
                .call()
                .iterator();

        if (iterator.hasNext()) {
            return Optional.of(iterator.next());
        }

        return Optional.empty();
    }

    private void pullChanges()  throws GitAPIException, IOException {
        LOG.trace("open repository {}", repository);
        Git git = open();

        LOG.debug("pull changes from remote for repository {}", repository);
        git.pull()
                .setRemote("origin")
                .setRemoteBranchName(branch)
                .setCredentialsProvider(credentialsProvider(account))
                .call();
    }

    private void createClone()  throws GitAPIException, IOException {
        LOG.info("clone repository {} to {}", remoteUrl, repository);
        gitRepository = Git.cloneRepository()
                .setURI(remoteUrl.toExternalForm())
                .setDirectory(repository)
                .setBranchesToClone(singleton("refs/head" + branch))
                .setBranch(branch)
                .setCredentialsProvider(credentialsProvider(account))
                .call();

        if (!"master".equals(branch)) {
            File newRef = new File(repository, ".git/refs/heads/master");
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

    private CredentialsProvider credentialsProvider(Account account) {
        return new UsernamePasswordCredentialsProvider(account.getUsername(), account.getPassword());
    }

    public RevCommit commit(String path, String displayName, String email, String message) throws GitAPIException, IOException {
        String[] paths = {path};
        return commit(paths, displayName, email, message);
    }

    public RevCommit commit(String[] paths, String displayName, String email, String message) throws GitAPIException, IOException {
        Git git = open();

        for(String path : paths ) {
            git.add()
                    .addFilepattern(path)
                    .call();
        }

        RevCommit commit = git.commit()
                .setAuthor(displayName, email)
                .setMessage(message)
                .call();

        pushChanges();

        return commit;
    }

    private void pushChanges() throws GitAPIException, IOException {
        CredentialsProvider credentials = credentialsProvider(account);

        Git git = open();

        LOG.info("push changes to remote {} on branch {}", remoteUrl, branch);
        git.push()
                .setRemote("origin")
                .setRefSpecs(new RefSpec(branch+":"+branch))
                .setCredentialsProvider(credentials)
                .call();
    }


    @Override
    public void close()  {
        if (gitRepository != null) {
            gitRepository.close();
        }
    }
}
