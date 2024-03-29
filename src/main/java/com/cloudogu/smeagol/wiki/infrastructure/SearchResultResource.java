package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "searchResults")
public class SearchResultResource extends RepresentationModel<SearchResultResource> {

    private final String path;
    private final float score;
    private final String contentFragment;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SearchResultResource resource = (SearchResultResource) o;
        return Float.compare(resource.score, score) == 0 &&
                Objects.equals(path, resource.path) &&
                Objects.equals(contentFragment, resource.contentFragment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, score, contentFragment);
    }
}
