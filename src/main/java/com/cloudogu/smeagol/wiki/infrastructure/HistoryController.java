package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(HistoryController.MAPPING)
public class HistoryController {

    static final String MAPPING = "/api/v1/repositories/{repositoryId}/branches/{branch}/history/";

    private final HistoryResourceAssembler assembler = new HistoryResourceAssembler();

    private CommitRepository repository;
    private WildcardPathExtractor pathExtractor;

    @Autowired
    public HistoryController(CommitRepository repository, WildcardPathExtractor pathExtractor) {
        this.repository = repository;
        this.pathExtractor = pathExtractor;
    }


    @RequestMapping("**")
    public ResponseEntity<HistoryResource> history(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch
    ) {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = pathExtractor.extractPathFromRequest(request, MAPPING, id);

        History history = repository.findHistoryByWikiIdAndPath(id, path);

        HistoryResource resource = assembler.toResource(history);

        // TODO: should we return ResponseEntity.notFound(), if no commits were found?
        return ResponseEntity.ok(resource);
    }



}
