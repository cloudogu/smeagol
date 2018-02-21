package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * Serves static content, such as images, from a wiki repository.
 *
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
    ) throws IOException
    {
        WikiId id = new WikiId(repositoryId, branch);
        Path path = pathExtractor.extractPathFromRequest(request, MAPPING, id);

        FileSystemResource fileSystemResource = createFileSystemResource(id, path);
        if (fileSystemResource != null) {
            return ResponseEntity.ok(fileSystemResource);
        }

        return ResponseEntity.notFound().build();
    }

    private FileSystemResource createFileSystemResource(WikiId wiki, Path path) throws IOException {
        try(GitClient gitClient = gitClientProvider.createGitClient(wiki)) {
            gitClient.refresh();

            File file = gitClient.file(path.getValue());
            if (file.exists()) {
                return new FileSystemResource(file);
            }
        } catch (GitAPIException e) {
            throw new IOException("could not create filesystem resource", e);
        }

        return null;
    }
}
