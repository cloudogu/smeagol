package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

public class SearchResultResource extends ResourceSupport {

    private String path;

    public SearchResultResource(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
