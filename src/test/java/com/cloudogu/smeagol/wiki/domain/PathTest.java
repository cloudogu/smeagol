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
    public void testValueOfWithEndingSlash() {
        Path.valueOf("some/cool/path/");
    }

    @Test
    public void testConcat() {
        assertEquals("docs/Home", Path.valueOf("docs").concat(Path.valueOf("Home")).getValue());
    }

}