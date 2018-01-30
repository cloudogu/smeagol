package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Page;
import com.cloudogu.smeagol.wiki.domain.PageRepository;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping(PageController.MAPPING)
public class PageController {

    static final String MAPPING = "/api/v1/repositories/{repositoryId}/branches/{branch}/pages";

    private final PageResourceAssembler assembler = new PageResourceAssembler();

    private final PageRepository repository;
    private final WildcardPathExtractor pathExtractor;

    @Autowired
    public PageController(PageRepository repository, WildcardPathExtractor pathExtractor) {
        this.repository = repository;
        this.pathExtractor = pathExtractor;
    }

    @RequestMapping("**")
    public ResponseEntity<PageResource> findByWikiIdAndPath(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch
    ) {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = createPathFromRequest(request, id);

        Optional<Page> byWikiIdAndPath = repository.findByWikiIdAndPath(id, path);
        if (byWikiIdAndPath.isPresent()) {
            return ResponseEntity.ok(assembler.toResource(byWikiIdAndPath.get()));
        }
        return ResponseEntity.notFound().build();
    }

    private Path createPathFromRequest(HttpServletRequest request, WikiId id) {
        // we need to extract the path from request, because there is no matcher which allos slashes in spring
        // https://stackoverflow.com/questions/4542489/match-the-rest-of-the-url-using-spring-3-requestmapping-annotation
        // and we must mock the path extractor in our tests, because request.getServletPath is empty in the tests.
        String base = MAPPING.replace("{repositoryId}", id.getRepositoryID())
                .replace("{branch}", id.getBranch());
        return Path.valueOf(pathExtractor.extract(request, base));
    }


}
