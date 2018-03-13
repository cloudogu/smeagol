package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.AccountTestData;
import com.cloudogu.smeagol.wiki.domain.ChangeType;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitClientTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private DirectoryResolver directoryResolver;

    private GitClient target;
    private File targetDirectory;
    private File targetSearchIndexDirectory;

    private Git remote;
    private File remoteDirectory;

    @Captor
    private ArgumentCaptor<RepositoryChangedEvent> eventCaptor;

    private final WikiId wikiId = new WikiId("42", "master");

    @Before
    public void setUp() throws IOException, GitAPIException {
        remoteDirectory = temporaryFolder.newFolder();
        remote = createGitRepo(remoteDirectory);

        targetDirectory = temporaryFolder.newFolder();
        targetDirectory.delete();

        targetSearchIndexDirectory = temporaryFolder.newFolder();
        when(directoryResolver.resolveSearchIndex(wikiId)).thenReturn(targetSearchIndexDirectory);

        when(directoryResolver.resolve(wikiId)).thenReturn(targetDirectory);

        target = new GitClient(
                publisher,
                directoryResolver,
                AccountTestData.TRILLIAN,
                remoteDirectory.toURI().toURL(),
                wikiId
        );
    }

    private Git createGitRepo(File directory) throws GitAPIException {
        return Git.init()
                .setDirectory(directory)
                .call();
    }

    @After
    public void tearDown() {
        close(target);
        close(remote);
    }

    private void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    @Test
    public void testRefreshWithClone() throws IOException, GitAPIException {
        RevCommit commit = commit(remote, "a.md", "# My Headline");

        target.refresh();

        try (Git git = Git.open(targetDirectory)) {
            RevCommit c = git.log().call().iterator().next();
            assertEquals(commit, c);
        }
    }

    private RevCommit commit( Git git, String fileName, String content ) throws IOException, GitAPIException {
        File file = new File(git.getRepository().getWorkTree(), fileName);
        Files.write(content, file, Charsets.UTF_8);

        git.add().addFilepattern(fileName).call();

        return git.commit()
                .setMessage("added ".concat(fileName))
                .setAuthor("Tricia McMillian", "trillian@hitchhiker.com")
                .call();
    }

    @Test
    public void testRefreshWithPull() throws IOException, GitAPIException {
        commit(remote, "a.md", "# My Headline");

        Git.cloneRepository()
                .setDirectory(targetDirectory)
                .setURI(remoteDirectory.toURI().toURL().toExternalForm())
                .call()
                .close();

        RevCommit commit = commit(remote, "b.md", "File b");

        target.refresh();

        try (Git git = Git.open(targetDirectory)) {
            RevCommit c = git.log().call().iterator().next();
            assertEquals(commit, c);
        }
    }

    @Test
    public void testLastCommit() throws IOException, GitAPIException {
        try (Git git = Git.init().setDirectory(targetDirectory).call()) {
            commit(git, "a.md", "# My Headline");
            commit(git, "b.md", "# My Headline");
        }

        Optional<RevCommit> commit = target.lastCommit("b.md");
        assertEquals("added b.md", commit.get().getFullMessage());
    }

    @Test
    public void testCommit() throws IOException, GitAPIException {
        target.refresh();

        File file = new File(targetDirectory, "myfile.md");
        Files.write("# My Files Headline", file, Charsets.UTF_8);

        RevCommit commit = target.commit("myfile.md", "Tricia McMillian", "trillian@hitchhiker.com", "added myfile");

        remote.checkout().setName("master").call();

        RevCommit lastRemoteCommit = remote.log().addPath("myfile.md").setMaxCount(1).call().iterator().next();
        assertEquals(commit, lastRemoteCommit);
        assertEquals("added myfile", lastRemoteCommit.getFullMessage());
        assertEquals("Tricia McMillian", lastRemoteCommit.getAuthorIdent().getName());
        assertEquals("trillian@hitchhiker.com", lastRemoteCommit.getAuthorIdent().getEmailAddress());
    }

    @Test
    public void testRepositoryChangedEventOnClone() throws IOException, GitAPIException {
        commit(remote, "a.md", "# My Headline");
        commit(remote, "b.md", "# My Second Headline");

        target.refresh();

        verify(publisher).publishEvent(eventCaptor.capture());

        RepositoryChangedEvent event = eventCaptor.getValue();
        assertSame(wikiId, event.getWikiId());

        Iterator<RepositoryChangedEvent.Change> iterator = event.iterator();
        RepositoryChangedEvent.Change change = iterator.next();
        assertEquals(ChangeType.ADDED, change.getType());
        assertTrue(change.getPath().matches("(a|b).md"));

        change = iterator.next();
        assertEquals(ChangeType.ADDED, change.getType());
        assertTrue(change.getPath().matches("(a|b).md"));

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testRepositoryChangedEventOnPull() throws IOException, GitAPIException {
        commit(remote, "a.md", "# My Headline");
        commit(remote, "b.md", "# My Second Headline");

        Git.cloneRepository()
                .setDirectory(targetDirectory)
                .setURI(remoteDirectory.toURI().toURL().toExternalForm())
                .call()
                .close();

        new File(new File(targetDirectory, ".git"), "search-index").mkdirs();

        commit(remote, "a.md", "# My Changed Headline");
        remote.rm().addFilepattern("b.md").call();
        remote.commit()
                .setMessage("removed b.md")
                .setAuthor("Tricia McMillian", "trillian@hitchhiker.com")
                .call();
        commit(remote, "c.md", "# My Third Headline");

        target.refresh();

        verify(publisher).publishEvent(eventCaptor.capture());

        RepositoryChangedEvent event = eventCaptor.getValue();
        assertSame(wikiId, event.getWikiId());

        Iterator<RepositoryChangedEvent.Change> iterator = event.iterator();

        RepositoryChangedEvent.Change change = iterator.next();
        assertEquals(ChangeType.MODIFIED, change.getType());
        assertEquals("a.md", change.getPath());

        change = iterator.next();
        assertEquals(ChangeType.DELETED, change.getType());
        assertEquals("b.md", change.getPath());

        change = iterator.next();
        assertEquals(ChangeType.ADDED, change.getType());
        assertEquals("c.md", change.getPath());

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testRepositoryChangedEventOnPullWithoutSearchIndex() throws IOException, GitAPIException {
        commit(remote, "a.md", "# My Headline");
        commit(remote, "b.md", "# My Second Headline");

        Git.cloneRepository()
                .setDirectory(targetDirectory)
                .setURI(remoteDirectory.toURI().toURL().toExternalForm())
                .call()
                .close();

        targetSearchIndexDirectory.delete();

        target.refresh();

        verify(publisher).publishEvent(eventCaptor.capture());
        RepositoryChangedEvent event = eventCaptor.getValue();

        Iterator<RepositoryChangedEvent.Change> iterator = event.iterator();
        RepositoryChangedEvent.Change change = iterator.next();
        assertEquals(ChangeType.ADDED, change.getType());
        assertTrue(change.getPath().matches("(a|b).md"));

        change = iterator.next();
        assertEquals(ChangeType.ADDED, change.getType());
        assertTrue(change.getPath().matches("(a|b).md"));

        assertFalse(iterator.hasNext());
    }
}