package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Message is a simple non empty string value object.
 */
public final class Message {

    private final String value;

    private Message(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the message.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a message from its string representation.
     *
     * @param value string representation of message
     *
     * @return message value object
     */
    public static Message valueOf(String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "message is null or empty");
        return new Message(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(value, message.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
