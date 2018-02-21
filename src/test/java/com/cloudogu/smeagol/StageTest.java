package com.cloudogu.smeagol;

import org.junit.Test;

import static org.junit.Assert.*;

public class StageTest {

    @Test
    public void testFromString() {
        assertEquals(Stage.PRODUCTION, Stage.fromString("PRODUCTION"));
        assertEquals(Stage.DEVELOPMENT, Stage.fromString("DEVELOPMENT"));
    }

    @Test
    public void testFromStringCaseInsensitive() {
        assertEquals(Stage.PRODUCTION, Stage.fromString("production"));
        assertEquals(Stage.DEVELOPMENT, Stage.fromString("devElopmenT"));
    }

    @Test
    public void testFromStringWithEmptOrNullyName() {
        assertEquals(Stage.PRODUCTION, Stage.fromString(""));
        assertEquals(Stage.PRODUCTION, Stage.fromString(null));
    }
}