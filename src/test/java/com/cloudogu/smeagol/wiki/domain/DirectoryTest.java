package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import java.util.Collections;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;

public class DirectoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithFilePath() {
        new Directory(WIKI_ID_42, Path.valueOf("somefile"), Collections.emptyList(), Collections.emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithoutWikiId() {
        new Directory(null, Path.valueOf("dir/"), Collections.emptyList(), Collections.emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithoutPath() {
        new Directory(WIKI_ID_42, null, Collections.emptyList(), Collections.emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithoutChildPages() {
        new Directory(WIKI_ID_42, Path.valueOf("dir/"), Collections.emptyList(), null);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithoutChildDirectories() {
        new Directory(WIKI_ID_42, Path.valueOf("dir/"), null, Collections.emptyList());
    }

}