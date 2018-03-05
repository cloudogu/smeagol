package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.SearchResult;
import com.cloudogu.smeagol.wiki.domain.SearchResultRepository;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repositories/{repositoryId}/branches/{branch}/search")
public class SearchController {

    private final SearchResultResourceAssembler assembler = new SearchResultResourceAssembler();

    private SearchResultRepository repository;

    @Autowired
    public SearchController(SearchResultRepository repository) {
        this.repository = repository;
    }

    @RequestMapping
    public List<SearchResultResource> searchByWikiIdAndQuery(
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch,
            @RequestParam("query") String query
    ) {
        WikiId id = new WikiId(repositoryId, branch);

        Iterable<SearchResult> searchResults = repository.search(id, query);
        return assembler.toResources(searchResults);
    }
}
