package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.History;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Arrays;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScmGitCommitRepositoryTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
        File repository = temporaryFolder.newFolder();
        try (Git git = Git.init().setDirectory(repository).call()) {
            File file = new File(repository, "Home");
            Files.write("my content", file, Charsets.UTF_8);

            git.add().addFilepattern("Home.md").call();
            RevCommit revCommit = git.commit()
                    .setMessage("sample commit")
                    .setAuthor("Tricia McMillian", "trillian@hitchhiker.com")
                    .call();
            when(gitClient.findCommits("Home.md")).thenReturn(Arrays.asList(revCommit));

            WikiId id = WIKI_ID_42;
            Path path = Path.valueOf("Home");

            History history = commitRepository.findHistoryByWikiIdAndPath(id, path);
            assertEquals(id, history.getWikiId());
            assertEquals(path, history.getPath());

            Commit commitFromHistory = history.getCommits().get(0);
            assertEquals("trillian@hitchhiker.com", commitFromHistory.getAuthor().getEmail().getValue());
        }
    }

}