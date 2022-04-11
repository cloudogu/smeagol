package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.CommitId;
import com.cloudogu.smeagol.wiki.domain.Content;
import com.cloudogu.smeagol.wiki.domain.Message;
import com.cloudogu.smeagol.wiki.domain.Page;
import com.cloudogu.smeagol.wiki.domain.PageRepository;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.cloudogu.smeagol.wiki.usecase.CreatePageCommand;
import com.cloudogu.smeagol.wiki.usecase.DeletePageCommand;
import com.cloudogu.smeagol.wiki.usecase.EditPageCommand;
import com.cloudogu.smeagol.wiki.usecase.MovePageCommand;
import com.cloudogu.smeagol.wiki.usecase.RestorePageCommand;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Strings;
import de.triology.cb.CommandBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping(PageController.MAPPING)
public class PageController {

    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

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
    public ResponseEntity findByWikiIdAndPath(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch,
            @RequestParam(value = "commit", required = false) String commitId
    ) {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = pathExtractor.extractPathFromRequest(request, MAPPING, id);
        Optional<ResponseEntity<PageResource>> responseFound;

        try {
            if (Strings.isNullOrEmpty(commitId)) {
                responseFound = createResponse(id, path);
            } else {
                responseFound = createResponse(id, path, CommitId.valueOf(commitId));
            }
        } catch (MalformedCommitIdException ex) {
            LOG.debug("MalformedCommitIdException: respond with 400 Bad Request", ex);
            return ResponseEntity.badRequest().body("The passed commit is malformed.");
        }

        return responseFound.orElse(ResponseEntity.notFound().build());
    }

    private Optional<ResponseEntity<PageResource>> createResponse(WikiId id, Path path, CommitId commitId) {
        Optional<Page> byWikiIdAndPath = repository.findByWikiIdAndPathAndCommit(id, path, commitId);
        return byWikiIdAndPath.map(page -> ResponseEntity.ok(assembler.toCommitFixedResource(byWikiIdAndPath.get())));
    }

    private Optional<ResponseEntity<PageResource>> createResponse(WikiId id, Path path) {
        Optional<Page> byWikiIdAndPath = repository.findByWikiIdAndPath(id, path);
        return byWikiIdAndPath.map(page -> ResponseEntity.ok(assembler.toModel(page)));
    }


    @RequestMapping(method = RequestMethod.POST, value = "**")
    public ResponseEntity<Void> createOrEdit(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch,
            @RequestBody PostRequestPayload payload
    ) throws URISyntaxException {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = pathExtractor.extractPathFromRequest(request, MAPPING, id);

        if (payload.getMoveTo() != null) {
            return move(id, path, payload);
        }

        if (payload.getRestore() != null) {
            return restore(id, path, payload);
        }

        if ( repository.exists(id, path) ) {
            return edit(id, path, payload);
        }
        return create(request, id, path, payload);
    }

    private ResponseEntity<Void> move(WikiId id, Path source, PostRequestPayload payload) {
        MovePageCommand command = new MovePageCommand(id, source, payload.getMoveTo(), payload.getMessage());
        commandBus.execute(command);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Void> restore(WikiId id, Path source, PostRequestPayload payload) {
        RestorePageCommand command = new RestorePageCommand(id, source, payload.getRestore(), payload.getMessage());
        commandBus.execute(command);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Void> create(HttpServletRequest request, WikiId id, Path path, PostRequestPayload payload) throws URISyntaxException {
        CreatePageCommand command = new CreatePageCommand(id, path, payload.getMessage(), payload.getContent());
        commandBus.execute(command);
        return ResponseEntity.created(new URI(request.getRequestURI())).build();
    }

    private ResponseEntity<Void> edit(WikiId id, Path path, PostRequestPayload payload) {
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
    public static class PostRequestPayload extends RequestPayload {
        private String content;
        private String moveTo;
        private String restore;
        private Content getContent() {
            return Content.valueOf(content);
        }
        private Path getMoveTo() {
            if (Strings.isNullOrEmpty(moveTo)) {
                return null;
            }
            return Path.valueOf(moveTo);
        }
        private CommitId getRestore() {
            if (Strings.isNullOrEmpty(restore)) {
                return null;
            }
            return CommitId.valueOf(restore);
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class DeleteRequestPayload extends RequestPayload {}

}
