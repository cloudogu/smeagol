package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.SearchResult;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class SearchResultResourceAssembler extends RepresentationModelAssemblerSupport<SearchResult, SearchResultResource> {

    public SearchResultResourceAssembler() {
        super(SearchController.class, SearchResultResource.class);
    }

    @Override
    public @NotNull SearchResultResource toModel(SearchResult searchResult) {
        SearchResultResource resource = new SearchResultResource(
                searchResult.getPath().getValue(),
                searchResult.getScore().getValue(),
                searchResult.getContentFragment().getValue()
        );

        resource.add(pageLink(searchResult));
        return resource;
    }

    private Link pageLink(SearchResult searchResult) {
        WikiId id = searchResult.getWikiId();
        return linkTo(PageController.class, id.getRepositoryID(), id.getBranch())
                .slash(searchResult.getPath().toString())
                .withRel("self");
    }

}
