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
            CommitResource commitResource = assembler.toResource(commit);
            commitResource.add(linkToPageAtCommit(history, commit));
            commitResource.add(linkToRestorePage(history));

            commitsAsResources.add(commitResource);
        }
        String path = history.getPath().toString();
        HistoryResource resource = new HistoryResource(path, commitsAsResources);
        resource.add(selfLink(history));
        return resource;
    }

    private Link selfLink(History history) {
        return baseLink(history).withSelfRel();
    }

    private Link linkToRestorePage(History history) {
        return pageLink(history).withRel("restore");
    }

    private Link linkToPageAtCommit(History history, Commit commit) {
        Link pageLink = pageLink(history).withRel("page");
        String href = pageLink.getHref() + "?commit=" + commit.getId().get().getValue();
        return new Link(href, pageLink.getRel());
    }

    private ControllerLinkBuilder baseLink(History history) {
        WikiId id = history.getWikiId();
        return linkTo(HistoryController.class, id.getRepositoryID(), id.getBranch())
                .slash(history.getPath().toString());
    }

    private ControllerLinkBuilder pageLink(History history) {
        WikiId id = history.getWikiId();
        return linkTo(PageController.class, id.getRepositoryID(), id.getBranch())
                .slash(history.getPath().toString());
    }
}
