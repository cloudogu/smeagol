package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

import java.util.Objects;

public class WikiResource extends ResourceSupport {

    private final String displayName;
    private final String repositoryName;
    private final String directory;
    private final String landingPage;

    public WikiResource(String displayName, String repositoryName, String directory, String landingPage) {
        this.displayName = displayName;
        this.repositoryName = repositoryName;
        this.directory = directory;
        this.landingPage = landingPage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getDirectory() {
        return directory;
    }

    public String getLandingPage() {
        return landingPage;
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
        WikiResource resource = (WikiResource) o;
        return Objects.equals(displayName, resource.displayName) &&
                Objects.equals(directory, resource.directory) &&
                Objects.equals(landingPage, resource.landingPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), displayName, directory, landingPage);
    }
}
