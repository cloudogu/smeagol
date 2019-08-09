package com.cloudogu.smeagol.wiki.infrastructure;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class GitConfigTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldSetOriginUrl() throws IOException, GitAPIException, URISyntaxException {
        File directory = temporaryFolder.newFolder();

        Git repository = createRepository(directory, "https://www.scm-manager.org");
        GitConfig config = GitConfig.from(repository);

        config.ensureOriginMatchesUrl("https://scm.scm-manager.org");

        String url = repository.getRepository().getConfig().getString("remote", "origin", "url");
        assertThat(url).isEqualTo("https://scm.scm-manager.org");
    }

    private Git createRepository(File directory, String remoteUrl) throws GitAPIException, URISyntaxException {
        Git git = Git.init()
                .setDirectory(directory)
                .call();

        RemoteAddCommand command = git.remoteAdd();
        command.setName("origin");
        command.setUri(new URIish(remoteUrl));
        command.call();

        return git;
    }

}