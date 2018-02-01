package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Author;
import com.cloudogu.smeagol.wiki.domain.Commit;
import com.cloudogu.smeagol.wiki.domain.Page;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


public class PageResourceAssembler extends ResourceAssemblerSupport<Page, PageResource> {

    public PageResourceAssembler() {
        super(PageController.class, PageResource.class);
    }

    @Override
    public PageResource toResource(Page page) {
        PageResource resource = new PageResource(
                page.getPath().getValue(),
                page.getContent().getValue(),
                createCommitResource(page.getCommit())
        );
        resource.add(selfLink(page));
        resource.add(editLink(page));

        return resource;
    }

    private CommitResource createCommitResource(Optional<Commit> optionalCommit) {
        if (!optionalCommit.isPresent()) {
            return null;
        }

        Commit commit = optionalCommit.get();
        AuthorResource author = createAuthor(commit.getAuthor());
        String date = createDate(commit.getDate());
        return new CommitResource(author, date, commit.getMessage().getValue());
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

    private Link selfLink(Page page) {
        return baseLink(page).withSelfRel();
    }

    private ControllerLinkBuilder baseLink(Page page) {
        WikiId id = page.getWikiId();
        return linkTo(PageController.class, id.getRepositoryID(), id.getBranch())
                .slash(page.getPath().toString());
    }
}