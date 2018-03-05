package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.SearchResult;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class SearchResultResourceAssembler extends ResourceAssemblerSupport<SearchResult, SearchResultResource> {

    public SearchResultResourceAssembler() {
        super(SearchController.class, SearchResultResource.class);
    }

    @Override
    public SearchResultResource toResource(SearchResult searchResult) {
        SearchResultResource resource = new SearchResultResource(searchResult.getPath().getValue());
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
