package com.cloudogu.smeagol.wiki.domain;

public class WikiSettings {
    private DisplayName displayName;
    private Path directory;
    private Path landingPage;

    public WikiSettings(DisplayName displayName, Path directory, Path landingPage) {
        this.displayName = displayName;
        this.directory = directory;
        this.landingPage = landingPage;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

    public Path getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(Path landingPage) {
        this.landingPage = landingPage;
    }
}
