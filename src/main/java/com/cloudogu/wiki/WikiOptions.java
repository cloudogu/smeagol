/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

/**
 * Wiki configuration options.
 * 
 * @author Sebastian Sdorra
 * @see <a href="https://github.com/gollum/gollum/wiki/Gollum-via-Rack#the-options-hash">Gollum Options</a>
 */
public final class WikiOptions {

    private final String repository;
    private final String directory;
    private final boolean universalToc;
    private final boolean bareRepo;
    private final boolean livePreview;

    private WikiOptions(String repository, String directory, boolean universalToc, boolean bareRepo, boolean livePreview) {
        this.repository = repository;
        this.directory = directory;
        this.universalToc = universalToc;
        this.bareRepo = bareRepo;
        this.livePreview = livePreview;
    }

    public String getRepository() {
        return repository;
    }

    public String getDirectory() {
        return directory;
    }

    public boolean isUniversalToc() {
        return universalToc;
    }

    public boolean isBareRepo() {
        return bareRepo;
    }

    public boolean isLivePreview() {
        return livePreview;
    }

    public static Builder builder(String repository) {
        return new Builder(repository);
    }

    public static class Builder {

        private final String repository;
        private String directory = "docs";
        private boolean universalToc = true;
        private boolean bareRepo = false;
        private boolean livePreview = true;

        private Builder(String repository) {
            this.repository = repository;
        }

        public Builder withDirectory(String directory) {
            this.directory = directory;
            return this;
        }

        public Builder withUniversalToc(boolean universalToc) {
            this.universalToc = universalToc;
            return this;
        }

        public Builder withBareRepo(boolean bareRepo) {
            this.bareRepo = bareRepo;
            return this;
        }

        public Builder withLivePreview(boolean livePreview) {
            this.livePreview = livePreview;
            return this;
        }

        public WikiOptions build() {
            return new WikiOptions(repository, directory, universalToc, bareRepo, livePreview);
        }

    }
}
