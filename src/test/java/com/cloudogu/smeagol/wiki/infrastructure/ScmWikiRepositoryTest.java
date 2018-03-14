package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.wiki.domain.Wiki;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScmWikiRepositoryTest {

    @Mock
    private ScmHttpClient httpClient;

    @Mock
    private ResponseEntity<String> entity;

    @InjectMocks
    private ScmWikiRepository repository;

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
            httpClient.get(
                "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
                    String.class,
                "123", ScmWikiRepository.SETTINGS_FILE, "master"
            )
        ).thenReturn(Optional.empty());

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
            httpClient.get(
                "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
                String.class,
                id.getRepositoryID(),
                ScmWikiRepository.SETTINGS_FILE,
                id.getBranch()
            )
        ).thenReturn(Optional.of(""));

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
                httpClient.get(
                        "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
                        String.class,
                        id.getRepositoryID(),
                        ScmWikiRepository.SETTINGS_FILE,
                        id.getBranch()
                )
        ).thenReturn(Optional.of("displayName: Heart of Gold\ndirectory: pages\nlandingPage: Index"));

        Wiki wiki = repository.findById(id).get();
        assertEquals("Heart of Gold", wiki.getDisplayName().getValue());
        assertSame(url, wiki.getRepositoryUrl());
        assertEquals("pages", wiki.getDirectory().getValue());
        assertEquals("Index", wiki.getLandingPage().getValue());
    }

}