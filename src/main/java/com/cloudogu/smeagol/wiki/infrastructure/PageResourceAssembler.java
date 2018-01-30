package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Page;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.time.format.DateTimeFormatter;

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
                page.getAuthor().map(a -> new PageResource.AuthorResource(a.getDisplayName().getValue(), a.getEmail().getValue())).orElse(null),
                page.getLastModified().map(l ->  DateTimeFormatter.ISO_INSTANT.format(l)).orElse(null)
        );
        resource.add(selfLink(page));
        return resource;
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
