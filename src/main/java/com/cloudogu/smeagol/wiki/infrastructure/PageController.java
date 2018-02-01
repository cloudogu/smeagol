package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.cloudogu.smeagol.wiki.usecase.EditPageCommand;
import com.cloudogu.smeagol.wiki.usecase.PageNotFoundException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import de.triology.cb.Command;
import de.triology.cb.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping(PageController.MAPPING)
public class PageController {

    static final String MAPPING = "/api/v1/repositories/{repositoryId}/branches/{branch}/pages";

    private final PageResourceAssembler assembler = new PageResourceAssembler();

    private final PageRepository repository;
    private final WildcardPathExtractor pathExtractor;
    private final CommandBus commandBus;

    @Autowired
    public PageController(PageRepository repository, WildcardPathExtractor pathExtractor, CommandBus commandBus) {
        this.repository = repository;
        this.pathExtractor = pathExtractor;
        this.commandBus = commandBus;
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

    @RequestMapping(method = RequestMethod.POST, value = "**")
    public ResponseEntity<Void> edit(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch,
            @RequestBody EditRequestPayload payload
    ){
        WikiId id = new WikiId(repositoryId, branch);
        Path path = createPathFromRequest(request, id);

        try {
            EditPageCommand command = new EditPageCommand(id, path, payload.getMessage(), payload.getContent());
            commandBus.execute(command);
        } catch (PageNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.noContent().build();
    }

    private Path createPathFromRequest(HttpServletRequest request, WikiId id) {
        // we need to extract the path from request, because there is no matcher which allos slashes in spring
        // https://stackoverflow.com/questions/4542489/match-the-rest-of-the-url-using-spring-3-requestmapping-annotation
        // and we must mock the path extractor in our tests, because request.getServletPath is empty in the tests.
        String base = MAPPING.replace("{repositoryId}", id.getRepositoryID())
                .replace("{branch}", id.getBranch());
        return Path.valueOf(pathExtractor.extract(request, base));
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class EditRequestPayload {
        private String message;
        private String content;

        private Message getMessage() {
            return Message.valueOf(message);
        }

        private Content getContent() {
            return Content.valueOf(content);
        }
    }

}
