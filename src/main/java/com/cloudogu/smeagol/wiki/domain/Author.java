package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * Author value object represents the author of a wiki page. An author consists of an display name and a email address.
 */
public class Author {

    private DisplayName displayName;
    private Email email;

    public Author(DisplayName displayName, Email email) {
        this.displayName = Preconditions.checkNotNull(displayName);
        this.email = Preconditions.checkNotNull(email);
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public Email getEmail() {
        return email;
    }

    public String asString() {
        StringBuilder builder = new StringBuilder(displayName.getValue());
        builder.append(" <").append(email.getValue()).append(">");
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Author author = (Author) o;
        return Objects.equals(displayName, author.displayName) &&
                Objects.equals(email, author.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, email);
    }

    @Override
    public String toString() {
        return asString();
    }
}
