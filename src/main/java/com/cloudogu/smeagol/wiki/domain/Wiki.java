package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.MoreObjects;

import java.net.URL;
import java.util.Objects;

public class Wiki {

    private final WikiId id;
    private final URL repositoryUrl;
    private final DisplayName displayName;
    private final Path directory;
    private final Path landingPage;

    public Wiki(WikiId id, URL repositoryUrl, DisplayName displayName, Path directory, Path landingPage) {
        this.id = id;
        this.repositoryUrl = repositoryUrl;
        this.displayName = displayName;
        this.directory = directory;
        this.landingPage = landingPage;
    }

    public WikiId getId() {
        return id;
    }

    public URL getRepositoryUrl() {
        return repositoryUrl;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public Path getDirectory() {
        return directory;
    }

    public Path getLandingPage() {
        return landingPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wiki wiki = (Wiki) o;
        return Objects.equals(id, wiki.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("repositoryUrl", repositoryUrl)
                .add("displayName", displayName)
                .add("directory", directory)
                .add("landingPage", landingPage)
                .toString();
    }
}
