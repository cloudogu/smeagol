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
        String pagePath = pagePath(path);
        File file = client.file(pagePath);

        if (file.exists()) {
            Optional<RevCommit> optCommit = client.lastCommit(pagePath);
            if (optCommit.isPresent()) {
                Content content = createContent(file);
                Commit commit = createCommit(optCommit.get());

                Page page = new Page(id, path, content, commit);
                return Optional.of(page);
            }
        }

        return Optional.empty();
    }

    public void delete(Page page, Commit commit) {
        WikiId id = page.getWikiId();
        Path path = page.getPath();
        try (GitClient client = gitClientProvider.createGitClient(id)) {
            client.refresh();

            String pagePath = pagePath(path);
            File file = client.file(pagePath);
            if (!file.delete()) {
                throw new IOException("could not delete file: " + file.getPath());
            }

            Author author = commit.getAuthor();

            client.commit(
                    pagePath,
                    author.getDisplayName().getValue(),
                    author.getEmail().getValue(),
                    commit.getMessage().getValue()
            );

        } catch (IOException | GitAPIException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Override
    public Page move(Page page, Path target, Commit commit) {
        WikiId id = page.getWikiId();
        Path path = page.getPath();
        try (GitClient client = gitClientProvider.createGitClient(id)) {
            client.refresh();

            String sourcePath = pagePath(path);
            String targetPath = pagePath(target);
            File oldFile = client.file(sourcePath);
            File newFile = client.file(targetPath);
            File newFileParentDir = newFile.getParentFile();
            if(!newFileParentDir.mkdirs() && !newFileParentDir.exists()) {
                throw new IOException("could not create directory for page: " + newFile.getPath());
            }
            Files.move(oldFile, newFile);

            Author author = commit.getAuthor();

            String[] paths = {sourcePath, targetPath};
            RevCommit revCommit = client.commit(
                    paths,
                    author.getDisplayName().getValue(),
                    author.getEmail().getValue(),
                    commit.getMessage().getValue()
            );

            return new Page(id, Path.valueOf(targetPath), page.getContent(), createCommit(revCommit));
        } catch (IOException | GitAPIException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private String pagePath(Path path) {
        return path.getValue().concat(EXTENSION);
    }

    private Content createContent(File file) throws IOException {
        return Content.valueOf(Files.toString(file, Charsets.UTF_8));
    }

    private Commit createCommit(RevCommit revCommit) {
        CommitId id = CommitId.valueOf(revCommit.getId().getName());
        Author author = createAuthor(revCommit.getAuthorIdent());
        Instant lastModified = Instant.ofEpochSecond(revCommit.getCommitTime());
        Message message = Message.valueOf(revCommit.getFullMessage());
        return new Commit(id, lastModified, author, message);
    }

    private Author createAuthor(PersonIdent ident) {
        return new Author(DisplayName.valueOf(ident.getName()), Email.valueOf(ident.getEmailAddress()));
    }

    @Override
    public Page save(Page page) {
        WikiId id = page.getWikiId();
        Path path = page.getPath();
        try (GitClient client = gitClientProvider.createGitClient(id)) {
            client.refresh();

            String pagePath = pagePath(path);
            File file = client.file(pagePath);
            file.getParentFile().mkdirs();

            Content content = page.getContent();
            Files.write(content.getValue(), file, Charsets.UTF_8);

            Commit commit = page.getCommit().get();
            Author author = commit.getAuthor();

            RevCommit revCommit = client.commit(
                    pagePath,
                    author.getDisplayName().getValue(),
                    author.getEmail().getValue(),
                    commit.getMessage().getValue()
            );

            return new Page(page.getWikiId(), path, content, createCommit(revCommit));
        } catch (IOException | GitAPIException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Override
    public boolean exists(WikiId id, Path path) {
        try (GitClient client = gitClientProvider.createGitClient(id)) {
            String pagePath = pagePath(path);
            return client.file(pagePath).exists();
        }
    }
}
