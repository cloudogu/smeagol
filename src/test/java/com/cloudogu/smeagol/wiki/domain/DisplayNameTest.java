package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class DisplayNameTest {

    @Test
    public void testValueOf() {
        assertEquals("Tricia McMillian", DisplayName.valueOf("Tricia McMillian").getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithEmptyValue() {
        DisplayName.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithNullValue() {
        DisplayName.valueOf(null);
    }

}