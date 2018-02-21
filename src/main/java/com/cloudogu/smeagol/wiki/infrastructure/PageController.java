package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.cloudogu.smeagol.wiki.usecase.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import de.triology.cb.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
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
        Path path = pathExtractor.extractPathFromRequest(request, MAPPING, id);

        Optional<Page> byWikiIdAndPath = repository.findByWikiIdAndPath(id, path);
        if (byWikiIdAndPath.isPresent()) {
            return ResponseEntity.ok(assembler.toResource(byWikiIdAndPath.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "**")
    public ResponseEntity<Void> createOrEdit(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch,
            @RequestBody CreateOrEditRequestPayload payload
    ) throws URISyntaxException {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = pathExtractor.extractPathFromRequest(request, MAPPING, id);

        // TODO return new page? this would safe us one request from the frontend. Is this resty?

        if ( repository.exists(id, path) ) {
            return edit(id, path, payload);
        }
        return create(request, id, path, payload);
    }

    private ResponseEntity<Void> create(HttpServletRequest request, WikiId id, Path path, CreateOrEditRequestPayload payload) throws URISyntaxException {
        CreatePageCommand command = new CreatePageCommand(id, path, payload.getMessage(), payload.getContent());
        commandBus.execute(command);
        return ResponseEntity.created(new URI(request.getRequestURI())).build();
    }

    private ResponseEntity<Void> edit(WikiId id, Path path, CreateOrEditRequestPayload payload) {
        EditPageCommand command = new EditPageCommand(id, path, payload.getMessage(), payload.getContent());
        commandBus.execute(command);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "**")
    public ResponseEntity<Void> delete(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch,
            @RequestBody DeleteRequestPayload payload
    )  {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = pathExtractor.extractPathFromRequest(request, MAPPING, id);

        DeletePageCommand command = new DeletePageCommand(id, path, payload.getMessage());
        commandBus.execute(command);
        return ResponseEntity.noContent().build();
    }

    public abstract static class RequestPayload {
        private String message;
        protected Message getMessage() {
            return Message.valueOf(message);
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class CreateOrEditRequestPayload extends RequestPayload {
        private String content;
        private Content getContent() {
            return Content.valueOf(content);
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class DeleteRequestPayload extends RequestPayload {}

}
