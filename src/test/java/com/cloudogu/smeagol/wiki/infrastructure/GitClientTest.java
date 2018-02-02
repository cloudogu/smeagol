package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.AccountTestData;
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

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class GitClientTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private GitClient target;
    private File targetDirectory;

    private Git remote;
    private File remoteDirectory;

    @Before
    public void setUp() throws IOException, GitAPIException {
        remoteDirectory = temporaryFolder.newFolder();
        remote = createGitRepo(remoteDirectory);

        targetDirectory = temporaryFolder.newFolder();
        targetDirectory.delete();

        target = new GitClient(
                AccountTestData.TRILLIAN,
                targetDirectory,
                remoteDirectory.toURI().toURL(),
                "master"
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
        System.out.println( git.getRepository().getWorkTree() );
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

        target.commit("myfile.md", "Tricia McMillian", "trillian@hitchhiker.com", "added myfile");

        remote.checkout().setName("master").call();

        RevCommit lastRemoteCommit = remote.log().addPath("myfile.md").setMaxCount(1).call().iterator().next();
        assertEquals("added myfile", lastRemoteCommit.getFullMessage());
        assertEquals("Tricia McMillian", lastRemoteCommit.getAuthorIdent().getName());
        assertEquals("trillian@hitchhiker.com", lastRemoteCommit.getAuthorIdent().getEmailAddress());
    }

}