package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.google.common.base.Throwables;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ScmGitCommitRepository implements CommitRepository {

    private final GitClientProvider gitClientProvider;

    @Autowired
    public ScmGitCommitRepository(GitClientProvider gitClientProvider) {
        this.gitClientProvider = gitClientProvider;
    }

    @Override
    public History findHistoryByWikiIdAndPath(WikiId id, Path path) {
        try (GitClient client = gitClientProvider.createGitClient(id)) {
            client.refresh();
            List<Commit> commits = client
                    .findCommits(Pages.filepath(path))
                    .stream()
                    .map(ScmGit::createCommit)
                    .collect(toList());
            return new History(id, path, commits);
        } catch (IOException | GitAPIException ex) {
            throw Throwables.propagate(ex);
        }
    }
}
