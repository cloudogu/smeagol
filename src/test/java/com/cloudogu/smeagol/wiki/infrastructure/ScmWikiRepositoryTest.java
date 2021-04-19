package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.ScmHttpClientResponse;
import com.cloudogu.smeagol.wiki.domain.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

import static com.cloudogu.smeagol.wiki.DomainTestData.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScmWikiRepositoryTest {

    @Mock
    private ScmHttpClient httpClient;

    @InjectMocks
    private ScmWikiRepository repository;

    @Mock
    private GitClientProviderForScmWikiRepository gitClientProvider;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void findByIdWithNonExistingRepository() {
        when(
            httpClient.get(
                "/api/rest/repositories/{id}.json",
                ScmWikiRepository.RepositoryDTO.class,
                "123"
            )
        ).thenReturn(Optional.empty());

        Optional<Wiki> wiki = repository.findById(new WikiId("123", "master"));
        assertFalse(wiki.isPresent());
    }

    @Test
    public void findByIdWithoutSettingsFile() {
        ScmWikiRepository.RepositoryDTO dto = new ScmWikiRepository.RepositoryDTO("heartOfGold", null);
        when(
            httpClient.get(
                "/api/rest/repositories/{id}.json",
                ScmWikiRepository.RepositoryDTO.class,
                "123"
            )
        ).thenReturn(Optional.of(dto));

        when(
            httpClient.getEntity(
                "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
                String.class,
                "123", ScmWikiRepository.SETTINGS_FILE, "master"
            )
        ).thenReturn(ScmHttpClientResponse.of(HttpStatus.NOT_FOUND));

        Optional<Wiki> wiki = repository.findById(new WikiId("123", "master"));
        assertFalse(wiki.isPresent());
    }

    @Test
    public void findByIdWithEmptySettingsFile() {
        WikiId id = new WikiId("123", "master");

        ScmWikiRepository.RepositoryDTO dto = new ScmWikiRepository.RepositoryDTO("heartOfGold", null);
        when(
            httpClient.get(
                "/api/rest/repositories/{id}.json",
                ScmWikiRepository.RepositoryDTO.class,
                id.getRepositoryID()
            )
        ).thenReturn(Optional.of(dto));

        when(
            httpClient.getEntity(
                "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
                String.class,
                id.getRepositoryID(),
                ScmWikiRepository.SETTINGS_FILE,
                id.getBranch()
            )
        ).thenReturn(ScmHttpClientResponse.of(HttpStatus.OK, ""));

        Wiki wiki = repository.findById(id).get();
        assertEquals("heartOfGold", wiki.getDisplayName().getValue());
        assertEquals("docs", wiki.getDirectory().getValue());
        assertEquals("Home", wiki.getLandingPage().getValue());
    }

    @Test
    public void findByIdWithSettingsFile() throws MalformedURLException {
        WikiId id = new WikiId("123", "master");
        URL url = new URL("https://github.com/cloudogu/smeagol");
        ScmWikiRepository.RepositoryDTO dto = new ScmWikiRepository.RepositoryDTO("heartOfGold", url);
        when(
            httpClient.get(
                "/api/rest/repositories/{id}.json",
                ScmWikiRepository.RepositoryDTO.class,
                id.getRepositoryID()
            )
        ).thenReturn(Optional.of(dto));

        when(
            httpClient.getEntity(
                "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
                String.class,
                id.getRepositoryID(),
                ScmWikiRepository.SETTINGS_FILE,
                id.getBranch()
            )
        ).thenReturn(ScmHttpClientResponse.of(HttpStatus.OK, "displayName: Heart of Gold\ndirectory: pages\nlandingPage: Index"));

        Wiki wiki = repository.findById(id).get();
        assertEquals("Heart of Gold", wiki.getDisplayName().getValue());
        assertSame(url, wiki.getRepositoryUrl());
        assertEquals("pages", wiki.getDirectory().getValue());
        assertEquals("Index", wiki.getLandingPage().getValue());
    }

    @Test
    public void init() throws IOException, GitAPIException {
        WikiId id = new WikiId("123", "master");
        URL url = new URL("https://github.com/cloudogu/smeagol");
        ScmWikiRepository.RepositoryDTO dto = new ScmWikiRepository.RepositoryDTO("heartOfGold", url);
        when(
            httpClient.get(
                "/api/rest/repositories/{id}.json",
                ScmWikiRepository.RepositoryDTO.class,
                id.getRepositoryID()
            )
        ).thenReturn(Optional.of(dto));
        GitClient gitClient = mock(GitClient.class);
        when(gitClientProvider.createGitClient(any(), any())).thenReturn(gitClient);
        File settingsFile = new File(temporaryFolder.newFolder(), ".smeagol.yml");
        when(gitClient.file(".smeagol.yml")).thenReturn(settingsFile);
        WikiSettings settings = new WikiSettings(DisplayName.valueOf("heartOfGold"),
            Path.valueOf("docs"), Path.valueOf("home"));

        Wiki newWiki = repository.init(id, COMMIT, settings);

        verify(this.httpClient).invalidateCache();
        verify(gitClient).commit(".smeagol.yml", DISPLAY_NAME_TRILLIAN.getValue(),
            EMAIL_TRILLIAN.getValue(), MESSAGE_PANIC.getValue());
        String settingsContent = Files.readString(settingsFile.toPath());
        assertEquals("directory: docs\n" +
            "displayName: null\n" +
            "landingPage: home\n", settingsContent);
        assertEquals(id, newWiki.getId());
        assertEquals(url, newWiki.getRepositoryUrl());
        assertEquals(settings.getDisplayName(), newWiki.getDisplayName());
        assertEquals("heartOfGold", newWiki.getRepositoryName().getValue());
        assertEquals(settings.getDirectory(), newWiki.getDirectory());
        assertEquals(settings.getLandingPage(), newWiki.getLandingPage());
    }

    @Test(expected = RuntimeException.class)
    public void initFailedToWrite() throws IOException {
        WikiId id = new WikiId("123", "master");
        URL url = new URL("https://github.com/cloudogu/smeagol");
        ScmWikiRepository.RepositoryDTO dto = new ScmWikiRepository.RepositoryDTO("heartOfGold", url);
        when(
            httpClient.get(
                "/api/rest/repositories/{id}.json",
                ScmWikiRepository.RepositoryDTO.class,
                id.getRepositoryID()
            )
        ).thenReturn(Optional.of(dto));
        GitClient gitClient = mock(GitClient.class);
        when(gitClientProvider.createGitClient(any(), any())).thenReturn(gitClient);
        File settingsFile = new File(temporaryFolder.newFolder().getAbsolutePath() + "/doesNotExist", ".smeagol.yml");
        when(gitClient.file(".smeagol.yml")).thenReturn(settingsFile);
        WikiSettings settings = new WikiSettings(DisplayName.valueOf("heartOfGold"),
            Path.valueOf("docs"), Path.valueOf("home"));

        repository.init(id, COMMIT, settings);

        verify(gitClient).deleteClone();
    }

    @Test
    public void save() throws IOException, GitAPIException {
        WikiId id = new WikiId("123", "master");
        URL url = new URL("https://github.com/cloudogu/smeagol");
        ScmWikiRepository.RepositoryDTO dto = new ScmWikiRepository.RepositoryDTO("heartOfGold", url);
        when(
            httpClient.get(
                "/api/rest/repositories/{id}.json",
                ScmWikiRepository.RepositoryDTO.class,
                id.getRepositoryID()
            )
        ).thenReturn(Optional.of(dto));
        when(
            httpClient.getEntity(
                "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
                String.class,
                id.getRepositoryID(),
                ScmWikiRepository.SETTINGS_FILE,
                id.getBranch()
            )
        ).thenReturn(ScmHttpClientResponse.of(HttpStatus.OK, "displayName: Heart of Gold\ndirectory: pages\nlandingPage: Index"));
        GitClient gitClient = mock(GitClient.class);
        when(gitClientProvider.createGitClient(any(), any())).thenReturn(gitClient);
        File settingsFile = temporaryFolder.newFile(".smeagol.yml");
        when(gitClient.file(".smeagol.yml")).thenReturn(settingsFile);
        WikiSettings settings = new WikiSettings(DisplayName.valueOf("heartOfGold"),
            Path.valueOf("docs"), Path.valueOf("home"));

        repository.save(id, COMMIT, settings);

        verify(this.httpClient).invalidateCache();
        verify(gitClient).commit(".smeagol.yml", DISPLAY_NAME_TRILLIAN.getValue(),
            EMAIL_TRILLIAN.getValue(), MESSAGE_PANIC.getValue());
        String settingsContent = Files.readString(settingsFile.toPath());
        assertEquals("directory: docs\n" +
            "displayName: null\n" +
            "landingPage: home\n", settingsContent);
    }
}
