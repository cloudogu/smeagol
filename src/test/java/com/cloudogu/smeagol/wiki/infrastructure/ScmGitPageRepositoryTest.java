package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Author;
import com.cloudogu.smeagol.wiki.domain.Page;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScmGitPageRepositoryTest {

    private final WikiId wikiId = new WikiId("123", "master");

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private GitClientProvider gitClientProvider;

    @Mock
    private GitClient gitClient;

    @InjectMocks
    private ScmGitPageRepository pageRepository;

    @Before
    public void setUp() {
        when(gitClientProvider.createGitClient(wikiId)).thenReturn(gitClient);
    }

    @Test
    public void testFindByWikiIdAndPathWithNonExistingFile() {
        when(gitClient.file("docs/Home.md")).thenReturn(new File("file/that/does/not/exists"));

        Optional<Page> optional = pageRepository.findByWikiIdAndPath(wikiId, Path.valueOf("docs/Home"));
        assertFalse(optional.isPresent());
    }

    @Test
    public void testFindByWikiIdAndPathWithNonExistingCommit() throws IOException, GitAPIException {
        when(gitClient.file("docs/Home.md")).thenReturn(temporaryFolder.newFile());
        when(gitClient.lastCommit("docs/Home.md")).thenReturn(Optional.empty());

        Optional<Page> optional = pageRepository.findByWikiIdAndPath(wikiId, Path.valueOf("docs/Home"));
        assertFalse(optional.isPresent());
    }

    @Test
    public void testFindByWikiIdAndPath() throws Exception {
        File repository = temporaryFolder.newFolder();
        try (Git git = Git.init().setDirectory(repository).call()) {

            File file = new File(repository, "Home");
            Files.write("my content", file, Charsets.UTF_8);
            when(gitClient.file("Home.md")).thenReturn(file);

            git.add().addFilepattern("Home.md").call();
            RevCommit commit = git.commit()
                    .setMessage("sample commit")
                    .setAuthor("Tricia McMillian", "trillian@hitchhiker.com")
                    .call();
            when(gitClient.lastCommit("Home.md")).thenReturn(Optional.of(commit));

            Optional<Page> optionalPage = pageRepository.findByWikiIdAndPath(wikiId, Path.valueOf("Home"));
            assertTrue(optionalPage.isPresent());

            Page page = optionalPage.get();
            assertEquals("Home", page.getPath().getValue());
            assertEquals("my content", page.getContent().getValue());

            Author author = page.getCommit().get().getAuthor();
            assertEquals("Tricia McMillian", author.getDisplayName().getValue());
            assertEquals("trillian@hitchhiker.com", author.getEmail().getValue());
        }
    }

}