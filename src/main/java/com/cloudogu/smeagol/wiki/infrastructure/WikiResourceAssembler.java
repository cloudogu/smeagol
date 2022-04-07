package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Wiki;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class WikiResourceAssembler extends RepresentationModelAssemblerSupport<Wiki, WikiResource> {

    public WikiResourceAssembler() {
        super(WikiController.class, WikiResource.class);
    }

    @Override
    public @NotNull WikiResource toModel(Wiki wiki) {
        String displayName = wiki.getDisplayName().getValue();
        String repositoryName = wiki.getRepositoryName().getValue();
        String directory = wiki.getDirectory().getValue();
        String path = wiki.getDirectory().concat(wiki.getLandingPage()).getValue();
        
        WikiResource resource = new WikiResource(displayName, repositoryName, directory, path);
        resource.add(selfLink(wiki));
        resource.add(gitLink(wiki));
        resource.add(landingPageLink(wiki, path));
        return resource;
    }

    private Link landingPageLink(Wiki wiki, String path) {
        return baseLink(wiki.getId()).slash("pages").slash(path).withRel("landingPage");
    }

    private Link gitLink(Wiki wiki) {
        return Link.of(wiki.getRepositoryUrl().toExternalForm(), "repository");
    }

    private Link selfLink(Wiki wiki) {
        return baseLink(wiki.getId()).withSelfRel();
    }

    private WebMvcLinkBuilder baseLink(WikiId id) {
        return linkTo(
                methodOn(WikiController.class).wiki(id.getRepositoryID(), id.getBranch())
        );
    }
}
