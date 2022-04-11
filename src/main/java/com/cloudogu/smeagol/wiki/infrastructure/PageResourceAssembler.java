package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


public class PageResourceAssembler extends RepresentationModelAssemblerSupport<Page, PageResource> {

    public PageResourceAssembler() {
        super(PageController.class, PageResource.class);
    }

    @Override
    public @NotNull PageResource toModel(Page page) {
        PageResource resource = new PageResource(
                page.getPath().getValue(),
                page.getContent().getValue(),
                createCommitResource(page.getCommit())
        );
        resource.add(selfLink(page));
        resource.add(editLink(page));
        resource.add(moveLink(page));
        resource.add(deleteLink(page));

        return resource;
    }

    public PageResource toCommitFixedResource(Page page) {
        PageResource resource = new PageResource(
                page.getPath().getValue(),
                page.getContent().getValue(),
                createCommitResource(page.getCommit())
        );
        Link linkToPage = selfLink(page);
        String href = linkToPage.getHref() + "?commit=" + page.getCommit().get().getId().get().getValue();
        Link linkToPageCommitFixed = Link.of(href, linkToPage.getRel());
        resource.add(linkToPageCommitFixed);
        resource.add(restoreLink(page));

        return resource;
    }

    private CommitResource createCommitResource(Optional<Commit> optionalCommit) {
        if (optionalCommit.isEmpty()) {
            return null;
        }

        Commit commit = optionalCommit.get();
        String id = commit.getId().get().getValue();
        AuthorResource author = createAuthor(commit.getAuthor());
        String date = createDate(commit.getDate());
        return new CommitResource(id, author, date, commit.getMessage().getValue());
    }

    private String createDate(Instant date) {
        return DateTimeFormatter.ISO_INSTANT.format(date);
    }

    private AuthorResource createAuthor(Author author) {
        return new AuthorResource(
                author.getDisplayName().getValue(),
                author.getEmail().getValue()
        );
    }

    private Link editLink(Page page) {
        return baseLink(page).withRel("edit");
    }

    private Link moveLink(Page page) {
        return baseLink(page).withRel("move");
    }

    private Link selfLink(Page page) {
        return baseLink(page).withSelfRel();
    }

    private Link deleteLink(Page page) {
        return baseLink(page).withRel("delete");
    }

    private Link restoreLink(Page page) {
        return baseLink(page).withRel("restore");
    }

    private WebMvcLinkBuilder baseLink(Page page) {
        WikiId id = page.getWikiId();
        return linkTo(PageController.class, id.getRepositoryID(), id.getBranch())
                .slash(page.getPath().toString());
    }
}
