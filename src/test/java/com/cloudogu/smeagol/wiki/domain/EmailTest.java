package com.cloudogu.smeagol.wiki.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class EmailTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testValueOfWithInvalidEmail() {
        expectedException.expect(IllegalArgumentException.class);
        Email.valueOf("abc");
    }

    @Test
    public void testToString() {
        Email email = Email.valueOf("trillian@hitchhiker.com");
        assertEquals("trillian@hitchhiker.com", email.toString());
    }

    @Test
    public void testEquals() {
        Email email = Email.valueOf("trillian@hitchhiker.com");
        assertEquals(Email.valueOf("trillian@hitchhiker.com"), email);
    }

    @Test
    public void testHashCode() {
        Email email = Email.valueOf("trillian@hitchhiker.com");
        assertEquals(Email.valueOf("trillian@hitchhiker.com").hashCode(), email.hashCode());
    }


}