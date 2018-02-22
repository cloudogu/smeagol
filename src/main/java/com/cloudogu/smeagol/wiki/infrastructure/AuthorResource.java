package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

import java.util.Objects;

public class AuthorResource extends ResourceSupport {

    private String displayName;
    private String email;

    public AuthorResource(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AuthorResource that = (AuthorResource) o;
        return Objects.equals(displayName, that.displayName) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), displayName, email);
    }
}
