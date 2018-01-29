package com.cloudogu.smeagol.repository.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class RepositoryIdTest {

    @Test
    public void testValueOf() {
        RepositoryId id = RepositoryId.valueOf("abc123");
        assertEquals("abc123", id.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithNullValue() {
        RepositoryId.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithEmptyValue() {
        RepositoryId.valueOf("");
    }

}