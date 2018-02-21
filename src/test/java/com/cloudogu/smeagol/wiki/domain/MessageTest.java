package com.cloudogu.smeagol.wiki.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageTest {

    @Test
    public void testValueOf() {
        Message message = Message.valueOf("Hi i'm Marvin");
        assertThat(message).isNotNull();
        assertThat(message.getValue()).isEqualTo("Hi i'm Marvin");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithEmptyValue() {
        Message.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfWithNullValue() {
        Message.valueOf("");
    }

}