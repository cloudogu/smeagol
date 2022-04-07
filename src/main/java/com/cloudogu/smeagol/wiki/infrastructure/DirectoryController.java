package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Directory;
import com.cloudogu.smeagol.wiki.domain.DirectoryRepository;
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
@RequestMapping(DirectoryController.MAPPING)
public class DirectoryController {

    static final String MAPPING = "/api/v1/repositories/{repositoryId}/branches/{branch}/directories";

    private final DirectoryResourceAssembler assembler = new DirectoryResourceAssembler();

    private final WildcardPathExtractor pathExtractor;
    private final DirectoryRepository directoryRepository;

    @Autowired
    public DirectoryController(WildcardPathExtractor pathExtractor, DirectoryRepository directoryRepository) {
        this.pathExtractor = pathExtractor;
        this.directoryRepository = directoryRepository;
    }

    @RequestMapping("**")
    public ResponseEntity<DirectoryResource> findDirectory(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch
    ) {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = pathExtractor.extractPathFromRequest(request, MAPPING, id);

        // we must allow requests without ending slash, because of spring hateoas strips always the ending slash
        if (path.isFile()) {
            path = Path.valueOf(path.getValue().concat("/"));
        }

        Optional<Directory> directory = directoryRepository.findByWikiIdAndPath(id, path);
        if (directory.isPresent()) {
            return ResponseEntity.ok(assembler.toModel(directory.get()));
        }

        return ResponseEntity.notFound().build();
    }

}
