package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommitIdTest {

    @Test
    public void testValueOf() {
        CommitId id = CommitId.valueOf("05b1c3b1e3d3e3e11cd337dd645fd19afe6086d0");
        assertThat(id).isNotNull();
        assertThat(id.getValue()).isEqualTo("05b1c3b1e3d3e3e11cd337dd645fd19afe6086d0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithEmptyValue() {
        CommitId.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithNullValue() {
        CommitId.valueOf(null);
    }

}