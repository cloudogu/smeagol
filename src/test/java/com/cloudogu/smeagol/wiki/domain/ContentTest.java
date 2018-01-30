package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContentTest {

    @Test
    public void testValueOf() {
        assertEquals(
            "Hitchhickers Guide to the Galaxy",
            Content.valueOf("Hitchhickers Guide to the Galaxy").getValue()
        );
    }

    @Test
    public void testValueOfWithNullValue() {
        assertEquals("", Content.valueOf(null).getValue());
    }

}