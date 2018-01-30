package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Service
public class ScmGitPageRepository implements PageRepository {

    private static final String EXTENSION = ".md";

    private final GitClientProvider gitClientProvider;

    @Autowired
    public ScmGitPageRepository(GitClientProvider gitClientProvider) {
        this.gitClientProvider = gitClientProvider;
    }

    @Override
    public Optional<Page> findByWikiIdAndPath(WikiId id, Path path) {
        try (GitClient client = gitClientProvider.createGitClient(id)) {
            client.refresh();
            return createPageFromFile(client, id, path);
        } catch (IOException | GitAPIException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private Optional<Page> createPageFromFile(GitClient client, WikiId id, Path path) throws IOException, GitAPIException {
        String pagePath = path.getValue().concat(EXTENSION);
        File file = client.file(pagePath);

        if (file.exists()) {
            Optional<RevCommit> optCommit = client.lastCommit(pagePath);
            if (optCommit.isPresent()) {
                RevCommit commit = optCommit.get();

                String content = Files.toString(file, Charsets.UTF_8);

                PersonIdent ident = commit.getAuthorIdent();
                Author author = createAuthor(ident);
                Instant lastModified = Instant.ofEpochSecond(commit.getCommitTime());

                Page page = new Page(id, path, author, Content.valueOf(content), lastModified);
                return Optional.of(page);
            }
        }

        return Optional.empty();
    }

    private Author createAuthor(PersonIdent ident) {
        return new Author(DisplayName.valueOf(ident.getName()), Email.valueOf(ident.getEmailAddress()));
    }

    @Override
    public void save(Page page) {

    }
}
