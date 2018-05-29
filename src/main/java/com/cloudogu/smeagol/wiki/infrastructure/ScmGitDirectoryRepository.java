package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Directory;
import com.cloudogu.smeagol.wiki.domain.DirectoryRepository;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@Service
public class ScmGitDirectoryRepository implements DirectoryRepository {

    private final GitClientProvider gitClientProvider;

    @Autowired
    public ScmGitDirectoryRepository(GitClientProvider gitClientProvider) {
        this.gitClientProvider = gitClientProvider;
    }

    @Override
    public Optional<Directory> findByWikiIdAndPath(WikiId wikiId, Path path) {
        checkArgument(path.isDirectory(), "path %s is not a directory", path);

        try (GitClient client = gitClientProvider.createGitClient(wikiId)) {
            client.refresh();

            File file = client.file(path.getValue());
            if (file.exists()) {
                checkState(file.isDirectory(), "file %s is not a directory", path);

                return Optional.of(createDirectory(wikiId, path, file));
            }

        } catch (IOException | GitAPIException ex) {
            throw Throwables.propagate(ex);
        }
        return Optional.empty();
    }

    private Directory createDirectory(WikiId wikiId, Path path, File file) {
        List<Path> childDirectories = Lists.newArrayList();
        List<Path> childPages = Lists.newArrayList();

        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                childDirectories.add(path.childDirectory(child.getName()));
            }

            if (Pages.isPage(child)) {
                childPages.add(Pages.path(path, child));
            }
        }
        return new Directory(wikiId, path, childDirectories, childPages);
    }
}
