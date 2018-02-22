package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static com.cloudogu.smeagol.wiki.DomainTestData.*;
import static org.junit.Assert.*;

public class PageTest {

    @Test
    public void testMove() {
        Path oldPath = Path.valueOf("docs/Home");
        Path newPath = Path.valueOf("docs/NewHome");
        Page page = new Page(WIKI_ID_42, oldPath, CONTENT_GUIDE);
        page.move(COMMIT_WOID, newPath);

        assertSame(COMMIT_WOID, page.getCommit().get());
        assertEquals(oldPath, page.getOldPath().get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithDirectoryPath() {
        new Page(WIKI_ID_42, Path.valueOf("dir/"), CONTENT_GUIDE);
    }

    @Test(expected = NullPointerException.class)
    public void testEditWithoutCommit() {
        Page page = new Page(WIKI_ID_42, PATH_HOME, CONTENT_GUIDE);
        page.edit(null, Content.valueOf("some"));
    }

    @Test(expected = NullPointerException.class)
    public void testEditWithoutContent() {
        Page page = new Page(WIKI_ID_42, PATH_HOME, CONTENT_GUIDE);
        page.edit(COMMIT, null);
    }

    @Test
    public void testEdit() {
        Page page = new Page(WIKI_ID_42, PATH_HOME, Content.valueOf("something"));
        page.edit(COMMIT, CONTENT_GUIDE);
        assertSame(COMMIT, page.getCommit().get());
        assertSame(CONTENT_GUIDE, page.getContent());
    }

}