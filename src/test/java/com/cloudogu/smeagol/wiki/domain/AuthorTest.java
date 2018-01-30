package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class AuthorTest {

    @Test(expected = NullPointerException.class)
    public void testNewWithNullDisplayName() {
        new Author(null, Email.valueOf("trillian@hitchhiker.com"));
    }

    @Test(expected = NullPointerException.class)
    public void testNewWithNullEmail() {
        new Author(DisplayName.valueOf("Tricia McMillian"), null);
    }

    @Test
    public void testAsString() {
        Author author = new Author(DisplayName.valueOf("Tricia McMillian"), Email.valueOf("trillian@hitchhiker.com"));
        assertEquals("Tricia McMillian <trillian@hitchhiker.com>", author.asString());
    }
}