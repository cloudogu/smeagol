package com.cloudogu.smeagol.wiki.infrastructure;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

final class GitConfig {

    static final String DEFAULT_REMOTE = "origin";
    private static final String SECTION = "remote";
    private static final String SUBSECTION = DEFAULT_REMOTE;
    private static final String NAME = "url";

    private static final Logger LOG = LoggerFactory.getLogger(GitConfig.class);

    private final StoredConfig config;

    GitConfig(StoredConfig config) {
        this.config = config;
    }

    void ensureOriginMatchesUrl(String url) throws IOException {
        String configuredUrl = config.getString(SECTION, SUBSECTION, NAME);
        if (!configuredUrl.equals(url)) {
            LOG.info("changed origin url from {} to {}", configuredUrl, url);
            config.setString(SECTION, SUBSECTION, NAME, url);
            config.save();
        }
    }

    static GitConfig from(Repository repository) {
        return new GitConfig(repository.getConfig());
    }

    static GitConfig from(Git git) {
        return from(git.getRepository());
    }
}
