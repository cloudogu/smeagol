package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.History;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ScmGit.class)
public class ScmGitCommitRepositoryTest {

    private GitClientProvider gitClientProvider = mock(GitClientProvider.class);

    private GitClient gitClient = mock(GitClient.class);

    private ScmGitCommitRepository commitRepository = new ScmGitCommitRepository(gitClientProvider);

    @Before
    public void setUp() {
        when(gitClientProvider.createGitClient(WIKI_ID_42)).thenReturn(gitClient);
    }

    @Test
    public void testFindHistoryByWikiIdAndPath() throws Exception {
        RevCommit revCommit = mock(RevCommit.class);
        Commit commit = mock(Commit.class);

        when(gitClient.findCommits("Home.md")).thenReturn(Arrays.asList(revCommit));
        PowerMockito.mockStatic(ScmGit.class);
        when(ScmGit.createCommit(revCommit)).thenReturn(commit);

        WikiId id = WIKI_ID_42;
        Path path = Path.valueOf("Home");

        History history = commitRepository.findHistoryByWikiIdAndPath(id, path);

        assertEquals(id, history.getWikiId());
        assertEquals(path, history.getPath());
        assertEquals(1, history.getCommits().size());
        assertSame(commit, history.getCommits().get(0));
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