package com.cloudogu.smeagol.repository.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class DescriptionTest {

    @Test
    public void testValueOf() {
        Description description = Description.valueOf("Heart Of Gold");
        assertEquals("Heart Of Gold", description.getValue());
    }

    @Test
    public void testValueOfWithNullValue() {
        assertEquals("", Description.valueOf(null).getValue());
    }

}