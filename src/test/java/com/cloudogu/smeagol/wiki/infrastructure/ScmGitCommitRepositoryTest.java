package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.History;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.cloudogu.smeagol.wiki.DomainTestData.COMMIT;
import static com.cloudogu.smeagol.wiki.DomainTestData.PATH_HOME;
import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScmGitCommitRepositoryTest {

    @Mock
    private GitClientProvider gitClientProvider;

    @Mock
    private GitClient gitClient;

    @InjectMocks
    private ScmGitCommitRepository commitRepository;

    @Before
    public void setUp() {
        when(gitClientProvider.createGitClient(WIKI_ID_42)).thenReturn(gitClient);
    }

    @Test
    public void testFindHistoryByWikiIdAndPath() throws Exception {
        WikiId id = WIKI_ID_42;
        Path path = PATH_HOME;
        Commit commit = COMMIT;

        History history = commitRepository.findHistoryByWikiIdAndPath(id ,path);
        assertEquals(id, history.getWikiId());
        assertEquals(path, history.getPath());

        Commit commitFromHistory = history.getCommits().get(0);
        assertEquals(commit, commitFromHistory);
    }

}