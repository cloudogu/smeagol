package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

public class WikiResource extends ResourceSupport {

    private String displayName;
    private String landingPage;

    public WikiResource(String displayName, String landingPage) {
        this.displayName = displayName;
        this.landingPage = landingPage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLandingPage() {
        return landingPage;
    }
}
