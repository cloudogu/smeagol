package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.History;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Arrays;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScmGitCommitRepositoryTest {

    private GitClientProvider gitClientProvider = mock(GitClientProvider.class);

    private GitClient gitClient = mock(GitClient.class);

    private ScmGitCommitRepository commitRepository = new ScmGitCommitRepository(gitClientProvider);

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private Git git;

    @Before
    public void setUp() throws GitAPIException {
        when(gitClientProvider.createGitClient(WIKI_ID_42)).thenReturn(gitClient);
        git = Git.init().setDirectory(tmpFolder.getRoot()).call();
    }

    @After
    public void tearDown() {
        git.getRepository().close();
    }

    @Test
    public void testFindHistoryByWikiIdAndPath() throws Exception {
        RevCommit revCommit = git.commit().setMessage("Test commit").setAuthor("Arthur Dent","arthur@dent.com").call();
        when(gitClient.findCommits("Home.md")).thenReturn(Arrays.asList(revCommit));

        WikiId id = WIKI_ID_42;
        Path path = Path.valueOf("Home");

        History history = commitRepository.findHistoryByWikiIdAndPath(id, path);

        assertEquals(id, history.getWikiId());
        assertEquals(path, history.getPath());
        assertEquals(1, history.getCommits().size());
        assertEquals(revCommit.getFullMessage(), history.getCommits().get(0).getMessage().getValue());
        assertEquals("Arthur Dent", history.getCommits().get(0).getAuthor().getDisplayName().getValue());
        assertEquals("arthur@dent.com", history.getCommits().get(0).getAuthor().getEmail().getValue());
    }

    @Test
    public void testFindHistoryByWikiIdAndPathWithoutCommits() throws Exception {
        when(gitClient.findCommits("Home.md")).thenReturn(emptyList());

        WikiId id = WIKI_ID_42;
        Path path = Path.valueOf("Home");

        History history = commitRepository.findHistoryByWikiIdAndPath(id, path);

        assertEquals(id, history.getWikiId());
        assertEquals(path, history.getPath());
        assertTrue(history.getCommits().isEmpty());
    }

}
