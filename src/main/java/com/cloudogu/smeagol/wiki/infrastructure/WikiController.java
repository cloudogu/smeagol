package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.cloudogu.smeagol.wiki.usecase.EditWikiCommand;
import com.cloudogu.smeagol.wiki.usecase.InitWikiCommand;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Strings;
import de.triology.cb.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/repositories/{repositoryId}/branches/{branch}")
public class WikiController {

    private final WikiResourceAssembler assembler = new WikiResourceAssembler();

    private WikiRepository repository;
    private final CommandBus commandBus;

    @Autowired
    public WikiController(WikiRepository repository, CommandBus commandBus) {
        this.repository = repository;
        this.commandBus = commandBus;
    }

    @GetMapping
    public ResponseEntity<WikiResource> wiki(
        @PathVariable("repositoryId") String repositoryId,
        @PathVariable("branch") String branch
    ) {
        WikiId id = new WikiId(repositoryId, branch);

        Optional<Wiki> wiki = repository.findById(id);
        return wiki.map(value -> ResponseEntity.ok(assembler.toResource(value))).
            orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WikiResource> initWiki(
        HttpServletRequest request,
        @PathVariable("repositoryId") String repositoryId,
        @PathVariable("branch") String branch,
        @RequestBody WikiRequestPayload payload
    ) throws URISyntaxException {
        WikiId id = new WikiId(repositoryId, branch);
        Optional<Wiki> wiki = repository.findById(id);
        if (wiki.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        WikiSettings settings = new WikiSettings(null, payload.getRootDir(), payload.getLandingPage());
        InitWikiCommand command = new InitWikiCommand(id, Message.valueOf("Initialize wiki (smeagol)"), settings);
        commandBus.execute(command);
        return ResponseEntity.created(new URI(request.getRequestURI())).build();
    }

    @PatchMapping
    public ResponseEntity<WikiResource> editWiki(
        @PathVariable("repositoryId") String repositoryId,
        @PathVariable("branch") String branch,
        @RequestBody WikiRequestPayload payload
    ) {
        WikiId id = new WikiId(repositoryId, branch);
        Optional<Wiki> wiki = repository.findById(id);
        if (wiki.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        WikiSettings settings = new WikiSettings(null, payload.getRootDir(), payload.getLandingPage());
        EditWikiCommand command = new EditWikiCommand(id, Message.valueOf("Change settings of wiki (smeagol)"), settings);
        commandBus.execute(command);
        return ResponseEntity.noContent().build();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WikiRequestPayload {
        private String landingPage;
        private String rootDir;

        private Path getLandingPage() {
            if (Strings.isNullOrEmpty(landingPage)) {
                return null;
            }
            return Path.valueOf(landingPage);
        }

        private Path getRootDir() {
            if (Strings.isNullOrEmpty(rootDir)) {
                return null;
            }
            return Path.valueOf(rootDir);
        }
    }
}
