package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Serves static content, such as images, from a wiki repository.
 *
 * TODO:
 * - is it okay, that we bypass the domain?
 * - do we need to refresh the repository?
 * - integration test
 * - does FileSystemResource set a valid content-type?
 * - createPathFromRequest is copied from {@link PageController}, we need a util
 */
@RestController
@RequestMapping(StaticContentController.MAPPING)
public class StaticContentController {

    static final String MAPPING = "/api/v1/repositories/{repositoryId}/branches/{branch}/static";

    private final WildcardPathExtractor pathExtractor;
    private final GitClientProvider gitClientProvider;

    @Autowired
    public StaticContentController(WildcardPathExtractor pathExtractor, GitClientProvider gitClientProvider) {
        this.pathExtractor = pathExtractor;
        this.gitClientProvider = gitClientProvider;
    }

    @RequestMapping("**")
    public ResponseEntity<FileSystemResource> findStaticContent(
            HttpServletRequest request,
            @PathVariable("repositoryId") String repositoryId,
            @PathVariable("branch") String branch
    ) {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = createPathFromRequest(request, id);

        try( GitClient gitClient = gitClientProvider.createGitClient(id)) {
            File file = gitClient.file(path.getValue());
            if (file.exists()) {
                return ResponseEntity.ok(new FileSystemResource(file));
            }
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
