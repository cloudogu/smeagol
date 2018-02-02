package com.cloudogu.smeagol.wiki;

import com.cloudogu.smeagol.AccountTestData;
import com.cloudogu.smeagol.wiki.domain.*;

import java.time.Instant;

/**
 * Test data for wiki domain objects.
 */
public final class DomainTestData {

    private DomainTestData() {
    }

    public static final String REPOSITORYID_42 = "42";
    public static final String BRANCH_42 = "galaxy";
    public static final WikiId WIKI_ID_42 = new WikiId(REPOSITORYID_42, BRANCH_42);

    public static final Path PATH_HOME = Path.valueOf("docs/Home");

    public static final DisplayName DISPLAY_NAME_TRILLIAN = DisplayName.valueOf(
            AccountTestData.TRILLIAN.getDisplayName()
    );

    public static final Email EMAIL_TRILLIAN = Email.valueOf(
            AccountTestData.TRILLIAN.getMail()
    );

    public static final Author AUTHOR_TRILLIAN = new Author(DISPLAY_NAME_TRILLIAN, EMAIL_TRILLIAN);
    public static final Content CONTENT_GUIDE = Content.valueOf("Hitchhikers Guide to the Galaxy");
    public static final Message MESSAGE_PANIC = Message.valueOf("Don't Panic");

    public static final Instant INSTANT_BIRTHDAY = Instant.ofEpochSecond(481905762L);

    public static final CommitId COMMIT_ID = CommitId.valueOf("9bc6083953f48ba4991e5a1d926d42a098fcbe46");

    public static final Commit COMMIT = new Commit(COMMIT_ID, INSTANT_BIRTHDAY, AUTHOR_TRILLIAN, MESSAGE_PANIC);
    public static final Commit COMMIT_WOID = new Commit(INSTANT_BIRTHDAY, AUTHOR_TRILLIAN, MESSAGE_PANIC);

    public static final Page PAGE = new Page(WIKI_ID_42, PATH_HOME, CONTENT_GUIDE, COMMIT);
    public static final Page PAGE_NEW = new Page(WIKI_ID_42, PATH_HOME, CONTENT_GUIDE, COMMIT_WOID);

}
