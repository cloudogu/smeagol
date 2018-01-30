package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Wiki;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.cloudogu.smeagol.wiki.domain.WikiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/repositories/{repositoryId}/branches/{branch}")
public class WikiController {

    private final WikiResourceAssembler assembler = new WikiResourceAssembler();

    private WikiRepository repository;

    @Autowired
    public WikiController(WikiRepository repository) {
        this.repository = repository;
    }

    @RequestMapping
    public ResponseEntity<WikiResource> wiki(
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch
    ) {
        WikiId id = new WikiId(repositoryId, branch);

        Optional<Wiki> wiki = repository.findById(id);
        if (wiki.isPresent()) {
            return ResponseEntity.ok(assembler.toResource(wiki.get()));
        }
        return ResponseEntity.notFound().build();
    }

}
