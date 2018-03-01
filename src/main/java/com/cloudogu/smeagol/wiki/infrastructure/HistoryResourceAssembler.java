package com.cloudogu.smeagol.wiki.infrastructure;


import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.History;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class HistoryResourceAssembler extends ResourceAssemblerSupport<History, HistoryResource> {

    public HistoryResourceAssembler() {
        super(HistoryController.class, HistoryResource.class);
    }


    private final CommitResourceAssembler assembler = new CommitResourceAssembler();

    @Override
    public HistoryResource toResource(History history) {
        List<CommitResource> commitsAsResources = new ArrayList<CommitResource>(history.getCommits().size());
        for (Commit commit : history.getCommits()) {
            commitsAsResources.add(assembler.toResource(commit));
        }
        String id = history.getWikiId().toString();
        String path = history.getPath().toString();
        HistoryResource resource = new HistoryResource(id, path, commitsAsResources);
        resource.add(selfLink(history));
        return resource;
    }

    private Link selfLink(History history) {
        return baseLink(history).withSelfRel();
    }

    private ControllerLinkBuilder baseLink(History history) {
        WikiId id = history.getWikiId();
        return linkTo(HistoryController.class, id.getRepositoryID(), id.getBranch())
                .slash(history.getPath().toString());
    }
}
