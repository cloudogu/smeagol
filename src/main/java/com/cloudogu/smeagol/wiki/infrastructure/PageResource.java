package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class PageResource extends RepresentationModel<PageResource> {

    private final String path;
    private final String content;

    private final CommitResource commit;

    public PageResource(String path, String content, CommitResource commit) {
        this.path = path;
        this.content = content;
        this.commit = commit;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public CommitResource getCommit() {
        return commit;
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
        PageResource resource = (PageResource) o;
        return Objects.equals(path, resource.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), path);
    }
}
