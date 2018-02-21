package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email address value object.
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    );

    private String value;

    private Email(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the email.
     *
     * @return string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a email from a string.
     *
     * @param value string representation of email
     *
     * @return repository name
     */
    public static Email valueOf(String value) {
        Preconditions.checkArgument(EMAIL_PATTERN.matcher(value).matches(), "%s is not a valid email", value);
        return new Email(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email that = (Email) o;
        return Objects.equals(value, that.value);
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
