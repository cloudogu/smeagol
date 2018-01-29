package com.cloudogu.smeagol.repository.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class NameTest {

    @Test
    public void testValueOf() {
        Name name = Name.valueOf("heartOfGold");
        assertEquals("heartOfGold", name.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithNullValue() {
        Name.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithEmptyValue() {
        Name.valueOf("");
    }

}