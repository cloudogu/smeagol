package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Directory;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.google.common.collect.Iterables;
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

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScmGitDirectoryRepositoryTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private GitClientProvider gitClientProvider;

    @Mock
    private GitClient gitClient;

    @InjectMocks
    private ScmGitDirectoryRepository directoryRepository;

    @Before
    public void setUp() {
        when(gitClientProvider.createGitClient(WIKI_ID_42)).thenReturn(gitClient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByWikiIdAndPathWithFilePath() {
        directoryRepository.findByWikiIdAndPath(WIKI_ID_42, Path.valueOf("file"));
    }

    @Test(expected = IllegalStateException.class)
    public void testFindByWikiIdAndPathWithFile() throws IOException {
        when(gitClient.file("dir/")).thenReturn(temporaryFolder.newFile());
        directoryRepository.findByWikiIdAndPath(WIKI_ID_42, Path.valueOf("dir/"));
    }

    @Test
    public void testFindByWikiIdAndPathWithNonExistingDirectory() throws IOException {
        File file = temporaryFolder.newFile();
        assertTrue(file.delete());
        when(gitClient.file("dir/")).thenReturn(file);

        Optional<Directory> directory = directoryRepository.findByWikiIdAndPath(WIKI_ID_42, Path.valueOf("dir/"));
        assertFalse(directory.isPresent());
    }

    @Test
    public void testFindByWikiIdAndPath() throws IOException {
        File folder = temporaryFolder.newFolder();
        assertTrue(new File(folder, "sub").mkdir());
        assertTrue(new File(folder, "unknownFile").createNewFile());
        assertTrue(new File(folder, "one.md").createNewFile());
        when(gitClient.file("dir/")).thenReturn(folder);

        Optional<Directory> directory = directoryRepository.findByWikiIdAndPath(WIKI_ID_42, Path.valueOf("dir/"));
        assertTrue(directory.isPresent());

        Iterable<Path> childDirectories = directory.get().getChildDirectories();
        assertEquals(1, Iterables.size(childDirectories));
        assertEquals(Path.valueOf("dir/sub/"), Iterables.getFirst(childDirectories, null));

        Iterable<Path> childPages = directory.get().getChildPages();
        assertEquals(1, Iterables.size(childPages));
        assertEquals(Path.valueOf("dir/one"), Iterables.getFirst(childPages, null));
    }

}
