package com.cloudogu.smeagol;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Account represents an authenticated user on the application.
 *
 * @author Sebastian Sdorra
 */
public final class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String accessToken;
    private final String displayName;
    private final String mail;

    /**
     * Constructs a new account.
     *
     * @param username used for authentication
     * @param displayName displayName is used for git author name
     * @param mail mail is used for git author mail
     */
    public Account(String username, String displayName, String mail) {
        this(username, null, displayName, mail);
    }

    /**
     * Constructs a new account.
     *
     * @param username used for authentication
     * @param accessToken used for authentication
     * @param displayName displayName is used for git author name
     * @param mail mail is used for git author mail
     */
    public Account(String username, String accessToken, String displayName, String mail) {
        this.username = username;
        this.accessToken = accessToken;
        this.displayName = displayName;
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMail() {
        return mail;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("accessToken", accessToken != null ? "xxx" : null)
                .add("displayName", displayName)
                .add("mail", mail)
                .toString();
    }

}
