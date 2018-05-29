package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class PagesTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testFilePath() {
        assertEquals("one.md", Pages.filepath(Path.valueOf("one")));
    }

    @Test
    public void testIsPagefilename() {
        assertTrue(Pages.isPageFilename("some.md"));
        assertTrue(Pages.isPageFilename("ka/some.md"));
        assertFalse(Pages.isPageFilename("some"));
        assertFalse(Pages.isPageFilename("ka/some"));
        assertFalse(Pages.isPageFilename("ka/some.md.rdoc"));
        assertFalse(Pages.isPageFilename(""));
        assertFalse(Pages.isPageFilename(null));
    }

    @Test
    public void testIsPage() throws IOException {
        File pageFile = temporaryFolder.newFile("one.md");
        File file = temporaryFolder.newFile("one");
        File folder = temporaryFolder.newFolder();

        assertTrue(Pages.isPage(pageFile));
        assertFalse(Pages.isPage(file));
        assertFalse(Pages.isPage(folder));
    }

    @Test
    public void testPath() throws IOException {
        File pageFile = temporaryFolder.newFile("one.md");
        Path parent = Path.valueOf("dir/");
        assertEquals(Path.valueOf("dir/one"), Pages.path(parent, pageFile));
    }

    @Test
    public void testPagePath() {
        assertEquals(Path.valueOf("docs/Home"), Pages.pagepath("docs/Home.md"));
    }

}