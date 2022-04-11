package com.cloudogu.smeagol.wiki.infrastructure;


import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.History;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class HistoryResourceAssembler extends RepresentationModelAssemblerSupport<History, HistoryResource> {

    private final CommitResourceAssembler assembler = new CommitResourceAssembler();

    public HistoryResourceAssembler() {
        super(HistoryController.class, HistoryResource.class);
    }

    @Override
    public @NotNull HistoryResource toModel(History history) {
        List<CommitResource> commitsAsResources = new ArrayList<>(history.getCommits().size());
        for (Commit commit : history.getCommits()) {
            CommitResource commitResource = assembler.toModel(commit);
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
        return Link.of(href, pageLink.getRel());
    }

    private WebMvcLinkBuilder baseLink(History history) {
        WikiId id = history.getWikiId();
        return linkTo(HistoryController.class, id.getRepositoryID(), id.getBranch())
                .slash(history.getPath().toString());
    }

    private WebMvcLinkBuilder pageLink(History history) {
        WikiId id = history.getWikiId();
        return linkTo(PageController.class, id.getRepositoryID(), id.getBranch())
                .slash(history.getPath().toString());
    }
}
