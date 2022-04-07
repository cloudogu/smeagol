package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
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
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static com.cloudogu.smeagol.wiki.DomainTestData.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScmGitPageRepositoryTest {

    private final WikiId wikiId = WIKI_ID_42;

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

    @Test
    public void testFindByWikiIdAndPathAndCommit() throws Exception {
        CommitId commitId = COMMIT_ID;
        RevCommit rc = createRevCommit();
        when(gitClient.getCommitFromId(commitId.getValue())).thenReturn(rc);
        when(gitClient.pathContentAtCommit(Pages.filepath(Path.valueOf("Home")), rc)).thenReturn(Optional.of("Content 0"));
        Optional<Page> optionalPage = pageRepository.findByWikiIdAndPathAndCommit(wikiId, Path.valueOf("Home"), commitId);
        assertEquals("Content 0", optionalPage.get().getContent().getValue());
    }

    @Test(expected = MalformedCommitIdException.class)
    public void testFindByWikiIdAndPathAndCommitWithIncorrectCommit() throws Exception {
        File repository = temporaryFolder.newFolder();
        try (Git git = Git.init().setDirectory(repository).call()) {
            when(gitClient.getCommitFromId(anyString())).thenCallRealMethod();
            when(gitClient.open()).thenReturn(git);
            Optional<Page> optionalPage = pageRepository.findByWikiIdAndPathAndCommit(wikiId, Path.valueOf("Home"), CommitId.valueOf("123"));
        }
    }

    @Test
    public void testFindByWikiIdAndPathAndCommitWithNonExistingCommit() throws Exception {
        File repository = temporaryFolder.newFolder();
        try (Git git = Git.init().setDirectory(repository).call()) {
            when(gitClient.getCommitFromId(anyString())).thenCallRealMethod();
            when(gitClient.open()).thenReturn(git);
            Optional<Page> optionalPage = pageRepository.findByWikiIdAndPathAndCommit(wikiId, Path.valueOf("Home"), COMMIT_ID);
            assertFalse(optionalPage.isPresent());
        }
    }
    
    @Test
    public void testDelete() throws IOException, GitAPIException {
        File file = temporaryFolder.newFile();
        String homePagePath = PATH_HOME.getValue().concat(".md");
        when(gitClient.file(homePagePath)).thenReturn(file);

        RevCommit rc = createRevCommit();

        when(gitClient.commit(
                homePagePath,
                DISPLAY_NAME_TRILLIAN.getValue(),
                EMAIL_TRILLIAN.getValue(),
                MESSAGE_PANIC.getValue()
        )).thenReturn(rc);

        assertTrue(file.exists());
        pageRepository.delete(PAGE, COMMIT);
        assertFalse(file.exists());

        verify(gitClient).refresh();
    }

    @Test
    public void testSave() throws IOException, GitAPIException {
        File file = temporaryFolder.newFile();
        testSaveWithFile(file);
    }

    @Test
    public void testSaveWithMissingDirectory() throws IOException, GitAPIException {
        File folder = temporaryFolder.newFolder();
        File file = new File(folder, String.format("docs%stest%sHome.md", File.separator, File.separator));
        assertFalse(file.exists());
        testSaveWithFile(file);
        assertTrue(file.exists());
    }

    private void testSaveWithFile(File file) throws IOException, GitAPIException {
        String homePagePath = PATH_HOME.getValue().concat(".md");
        when(gitClient.file(homePagePath)).thenReturn(file);

        RevCommit rc = createRevCommit();

        when(gitClient.commit(
                homePagePath,
                DISPLAY_NAME_TRILLIAN.getValue(),
                EMAIL_TRILLIAN.getValue(),
                MESSAGE_PANIC.getValue()
        )).thenReturn(rc);

        Page page = pageRepository.save(PAGE);
        assertNotNull(page);
        assertNotSame(page, PAGE);

        verify(gitClient).refresh();
    }

    @Test
    public void testSaveOnMove() throws GitAPIException, IOException {
        String targetFileWithoutExtension = String.format("docs%stest", File.separator);
        testSaveOnMoveWithTargetName(targetFileWithoutExtension);
    }

    @Test
    public void testSaveOnMoveTargetInSubdir() throws GitAPIException, IOException {
        String targetFileWithoutExtension = String.format("docs%ssub%sdir%stest", File.separator, File.separator, File.separator);
        testSaveOnMoveWithTargetName(targetFileWithoutExtension);
    }

    private void testSaveOnMoveWithTargetName(String targetFileWithoutExtension) throws IOException, GitAPIException {
        File folder = temporaryFolder.newFolder();

        String sourceFileString = String.format("docs%sHome.md", File.separator);
        File sourceFile = new File(folder, sourceFileString);
        sourceFile.getParentFile().mkdirs();
        sourceFile.createNewFile();

        String targetFileString = targetFileWithoutExtension + ".md";
        File targetFile = new File(folder, targetFileString);

        when(gitClient.file(sourceFileString)).thenReturn(sourceFile);
        when(gitClient.file(targetFileString)).thenReturn(targetFile);

        RevCommit rc = createRevCommit();

        String[] paths = {sourceFileString, targetFileString};
        when(gitClient.commit(
                paths,
                DISPLAY_NAME_TRILLIAN.getValue(),
                EMAIL_TRILLIAN.getValue(),
                MESSAGE_PANIC.getValue()
        )).thenReturn(rc);

        assertTrue(sourceFile.exists());
        assertFalse(targetFile.exists());
        Path targetPath = Path.valueOf(targetFileWithoutExtension);

        Page page = new Page(WIKI_ID_42, PATH_HOME, CONTENT_GUIDE, COMMIT);
        page.move(COMMIT, targetPath);

        Page movedPage = pageRepository.save(page);
        assertEquals(targetPath, movedPage.getPath());
        assertEquals(PATH_HOME, movedPage.getOldPath().get());

        assertTrue(targetFile.exists());
        assertFalse(sourceFile.exists());

        verify(gitClient).refresh();
    }

    private RevCommit createRevCommit() throws IOException, GitAPIException {
        File folder = temporaryFolder.newFolder();
        try ( Git git = Git.init().setDirectory(folder).call() ) {
            return git.commit()
                    .setMessage(MESSAGE_PANIC.getValue())
                    .setAuthor(DISPLAY_NAME_TRILLIAN.getValue(), EMAIL_TRILLIAN.getValue())
                    .call();
        }
    }

    @Test
    public void testExists() throws IOException {
        File existingFile = temporaryFolder.newFile();
        when(gitClient.file("docs/Index.md")).thenReturn(existingFile);
        when(gitClient.file("docs/Home.md")).thenReturn(new File("file/that/does/not/exists"));

        assertFalse(pageRepository.exists(wikiId, Path.valueOf("docs/Home")));
        assertTrue(pageRepository.exists(wikiId, Path.valueOf("docs/Index")));
    }

}
