package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

public class ScoreTest {

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithNegativeValue() {
        Score.valueOf(-1f);
    }

}