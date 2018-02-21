package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.hateoas.ResourceSupport;

public class PageResource extends ResourceSupport {

    private String path;
    private String content;

    private CommitResource commit;

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
}
