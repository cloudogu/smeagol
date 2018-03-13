package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContentFragmentTest {

    @Test
    public void testValueOf() {
        ContentFragment abc = ContentFragment.valueOf("abc");
        assertThat(abc).isEqualTo(ContentFragment.valueOf("abc"));
        assertThat(abc.getValue()).isEqualTo("abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithNullArgument() {
        ContentFragment.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithEmptyStringArgument() {
        ContentFragment.valueOf("");
    }

}