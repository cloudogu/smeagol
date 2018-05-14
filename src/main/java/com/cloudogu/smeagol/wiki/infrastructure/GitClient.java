package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.wiki.domain.Wiki;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.io.*;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singleton;

@SuppressWarnings("squid:S1160") // ignore multiple exception rule
public class GitClient implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(GitClient.class);

    private final ApplicationEventPublisher publisher;

    private final Account account;
    private final DirectoryResolver directoryResolver;
    private final Wiki wiki;

    private final File repository;

    private Git gitRepository;

    private PullChangesStrategy strategy;

    GitClient(ApplicationEventPublisher publisher, DirectoryResolver directoryResolver, PullChangesStrategy strategy, Account account, Wiki wiki) {
        this.publisher = publisher;
        this.directoryResolver = directoryResolver;
        this.strategy = strategy;

        this.account = account;
        this.wiki = wiki;

        this.repository = directoryResolver.resolve(wiki.getId());
    }

    public void refresh() throws GitAPIException, IOException {
        if (repository.exists()) {
            checkSearchIndex();
            pullChangesIfNeeded();
        } else {
            createClone();
        }
    }

    private void pullChangesIfNeeded() throws GitAPIException, IOException {
        if (strategy.shouldPull(wiki.getId())) {
            pullChangesAndLogTime();
        } else {
            LOG.debug("skip pulling changes, because pull strategy {}", strategy.getClass());
        }
    }

    private void pullChangesAndLogTime() throws GitAPIException, IOException {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            pullChanges();
        } finally {
            LOG.trace("pull changes of {} finished in {}", wiki.getId(), sw);
        }
    }

    private void checkSearchIndex() throws IOException {
        File searchIndex = directoryResolver.resolveSearchIndex(wiki.getId());
        if (!searchIndex.exists()) {
            if (!searchIndex.mkdirs()) {
                throw new IOException("failed to create directory for search index: " + searchIndex);
            }
            createRepositoryChangedEvent();
        }
    }

    @VisibleForTesting
    Git open() throws IOException {
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

    public List<RevCommit> findCommits(String path) throws IOException, GitAPIException {
        Git git = open();
        // REVIEW no need for manual copy
        return ImmutableList.copyOf(
                git
                        .log()
                        .addPath(path)
                        .call()
                        .iterator());
    }

    private void pullChanges() throws GitAPIException, IOException {
        LOG.trace("open repository {}", repository);
        Git git = open();

        LOG.debug("pull changes from remote for repository {}", repository);
        ObjectId oldHead = git.getRepository().resolve("HEAD^{tree}");

        git.pull()
                .setRemote("origin")
                .setRemoteBranchName(wiki.getId().getBranch())
                .setCredentialsProvider(credentialsProvider(account))
                .call();

        ObjectId head = git.getRepository().resolve("HEAD^{tree}");
        if (hasRepositoryChanged(oldHead, head)) {
            RepositoryChangedEvent repositoryChangedEvent = createRepositoryChangedEvent(git, oldHead, head);
            if (!repositoryChangedEvent.isEmpty()) {
                publisher.publishEvent(repositoryChangedEvent);
            }
        }
    }

    private RepositoryChangedEvent createRepositoryChangedEvent(Git git, ObjectId oldHead, ObjectId head) throws IOException, GitAPIException {
        ObjectReader reader = git.getRepository().newObjectReader();

        CanonicalTreeParser newTree = new CanonicalTreeParser();
        newTree.reset(reader, head);

        CanonicalTreeParser oldTree = new CanonicalTreeParser();
        oldTree.reset(reader, oldHead);

        List<DiffEntry> diffs = git.diff()
                .setNewTree(newTree)
                .setOldTree(oldTree)
                .call();

        RepositoryChangedEvent event = new RepositoryChangedEvent(wiki.getId());
        for (DiffEntry entry : diffs) {
            if (isPageDiff(entry)) {
                addChangeToEvent(entry, event);
            }
        }
        return event;
    }

    private boolean isPageDiff(DiffEntry entry) {
        return Pages.isPageFilename(entry.getNewPath()) || Pages.isPageFilename(entry.getOldPath());
    }

    private void addChangeToEvent(DiffEntry entry, RepositoryChangedEvent event) {
        switch (entry.getChangeType()) {
            case ADD:
            case COPY:
                event.added(entry.getNewPath());
                break;
            case DELETE:
                event.deleted(entry.getOldPath());
                break;
            case MODIFY:
                event.modified(entry.getNewPath());
                break;
            case RENAME:
                event.deleted(entry.getOldPath());
                event.added(entry.getNewPath());
        }
    }

    private boolean hasRepositoryChanged(ObjectId oldHead, ObjectId head) {
        return !head.equals(oldHead);
    }

    private void createClone() throws GitAPIException, IOException {
        String branch = wiki.getId().getBranch();
        LOG.info("clone repository {} to {}", wiki.getRepositoryUrl(), repository);
        gitRepository = Git.cloneRepository()
                .setURI(wiki.getRepositoryUrl().toExternalForm())
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

        createRepositoryChangedEvent();
    }

    private void createRepositoryChangedEvent() throws IOException {
        URI repositoryUri = repository.toURI();

        RepositoryChangedEvent repositoryChangedEvent = new RepositoryChangedEvent(wiki.getId());
        Files.walkFileTree(repository.toPath(), new SimpleFileVisitor<java.nio.file.Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (".git".equals(dir.getFileName().toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs) {
                String path = repositoryUri.relativize(file.toUri()).getPath();
                if (Pages.isPageFilename(path)) {
                    repositoryChangedEvent.added(path);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        if (!repositoryChangedEvent.isEmpty()) {
            publisher.publishEvent(repositoryChangedEvent);
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

        for (String path : paths) {
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

    public RevCommit getCommitFromId(String commitId) throws IOException {
        Git git = open();
        try (RevWalk revWalk = new RevWalk(git.getRepository())) {
            RevCommit commit = revWalk.parseCommit(ObjectId.fromString(commitId));
            revWalk.dispose();
            return commit;
        }
    }

    public Optional<String> pathContentAtCommit(String path, RevCommit commit) throws IOException {
        Git git = open();
        RevTree tree = commit.getTree();
        try (TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
            // REVIEW split up long method
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(path));
            if (!treeWalk.next()) {
                return Optional.empty();
            }
            ObjectId objectId = treeWalk.getObjectId(0);
            return Optional.of(readContent(objectId));
        }
    }

    private String readContent(ObjectId objectId) throws IOException {
        ObjectLoader loader = open().getRepository().open(objectId);
        ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();
        loader.copyTo(resultBytes);
        return new String(resultBytes.toByteArray());
    }

    private void pushChanges() throws GitAPIException, IOException {
        String branch = wiki.getId().getBranch();
        CredentialsProvider credentials = credentialsProvider(account);

        Git git = open();

        LOG.info("push changes to remote {} on branch {}", wiki.getRepositoryUrl(), branch);
        git.push()
                .setRemote("origin")
                .setRefSpecs(new RefSpec(branch + ":" + branch))
                .setCredentialsProvider(credentials)
                .call();
    }

    @Override
    public void close() {
        if (gitRepository != null) {
            gitRepository.close();
        }
    }
}
