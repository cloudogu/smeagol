/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.google.common.base.MoreObjects;

/**
 * Account represents an authenticated user on the application.
 *
 * @author Sebastian Sdorra
 */
public final class Account {

    private final String username;
    private final char[] password;
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
     * @param password used for authentication
     * @param displayName displayName is used for git author name
     * @param mail mail is used for git author mail
     */    
    public Account(String username, char[] password, String displayName, String mail) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
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
                .add("password", password != null ? "xxx" : null)
                .add("displayName", displayName)
                .add("mail", mail)
                .toString();
    }
    
}
