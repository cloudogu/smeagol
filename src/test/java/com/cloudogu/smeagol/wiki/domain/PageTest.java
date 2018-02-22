package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static com.cloudogu.smeagol.wiki.DomainTestData.COMMIT_WOID;
import static com.cloudogu.smeagol.wiki.DomainTestData.CONTENT_GUIDE;
import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
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

}