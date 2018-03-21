package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.google.common.base.Throwables;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cloudogu.smeagol.wiki.infrastructure.ScmGit.createCommit;

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
            List<RevCommit> revCommits = client.findCommits(Pages.filepath(path));
            List<Commit> commits = new ArrayList<>(revCommits.size());
            for (RevCommit rc: revCommits) {
                commits.add(createCommit(rc));
            }
            return new History(id, path, commits);
        } catch (IOException | GitAPIException ex) {
            throw Throwables.propagate(ex);
        }
    }
}
