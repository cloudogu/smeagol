package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

public class SearchResultResource extends ResourceSupport {

    private String path;
    private float score;
    private String contentFragment;

    public SearchResultResource(String path, float score, String contentFragment) {
        this.path = path;
        this.score = score;
        this.contentFragment = contentFragment;
    }

    public String getPath() {
        return path;
    }

    public float getScore() {
        return score;
    }

    public String getContentFragment() {
        return contentFragment;
    }
}
