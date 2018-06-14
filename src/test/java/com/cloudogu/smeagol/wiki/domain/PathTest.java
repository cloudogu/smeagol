package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class PathTest {

    @Test
    public void testValueOf() {
        assertEquals("some/path", Path.valueOf("some/path").getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithEmptyValue() {
        Path.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithNullValue() {
        Path.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithBackDirectory() {
        Path.valueOf("some/../../etc/passwd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithLeadingSlash() {
        Path.valueOf("/some/cool/path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithDoubleSlash() {
        Path.valueOf("some//path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithPercent() {
        Path.valueOf("some%path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithAmpersand() {
        Path.valueOf("some&path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithSemicolon() {
        Path.valueOf("some;path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithAsterisk() {
        Path.valueOf("some*path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithQuestionMark() {
        Path.valueOf("some?path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithExclamationMark() {
        Path.valueOf("some!path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithEndingDot() {
        Path.valueOf("some/path.");
    }

    @Test
    public void testValueOfWithEndingSlash() {
        Path path = Path.valueOf("some/cool/path/");
        assertEquals("some/cool/path/", path.getValue());
    }

    @Test
    public void testGetName() {
        assertEquals("file", Path.valueOf("file").getName());
        assertEquals("file", Path.valueOf("dir/file").getName());
        assertEquals("dir", Path.valueOf("dir/").getName());
        assertEquals("dir", Path.valueOf("sub/dir/").getName());
    }

    @Test
    public void testIsDirectory() {
        assertTrue(Path.valueOf("dir/").isDirectory());
        assertTrue(Path.valueOf("my/sub/dir/").isDirectory());
        assertFalse(Path.valueOf("file").isDirectory());
        assertFalse(Path.valueOf("dir/file").isDirectory());
    }

    @Test
    public void testIsFile() {
        assertTrue(Path.valueOf("file").isFile());
        assertTrue(Path.valueOf("dir/file").isFile());
        assertFalse(Path.valueOf("dir/").isFile());
        assertFalse(Path.valueOf("my/sub/dir/").isFile());
    }

    @Test(expected = IllegalStateException.class)
    public void testChildDirectoryOfFile() {
        Path.valueOf("file").childDirectory("child");
    }

    @Test
    public void testChildDirectory() {
        assertEquals(Path.valueOf("dir/child/"), Path.valueOf("dir/").childDirectory("child"));
        assertEquals(Path.valueOf("dir/child/"), Path.valueOf("dir/").childDirectory("child/"));
    }

    @Test(expected = IllegalStateException.class)
    public void testChildFileOfFile() {
        Path.valueOf("file").childFile("child");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChildFileWithDirectoryName() {
        Path.valueOf("dir/").childFile("child/");
    }

    @Test
    public void testChildFile() {
        assertEquals(Path.valueOf("dir/child"), Path.valueOf("dir/").childFile("child"));
    }

    @Test
    public void testConcat() {
        assertEquals("docs/Home", Path.valueOf("docs").concat(Path.valueOf("Home")).getValue());
    }

}
