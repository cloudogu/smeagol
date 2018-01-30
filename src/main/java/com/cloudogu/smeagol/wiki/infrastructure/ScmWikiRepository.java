package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.wiki.domain.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.net.URL;
import java.util.Optional;

import static com.google.common.base.Strings.emptyToNull;

@Service
public class ScmWikiRepository implements WikiRepository {

    @VisibleForTesting
    static final String SETTINGS_FILE = ".smeagol.yml";

    private final Yaml yaml = new Yaml();

    private final ScmHttpClient scmHttpClient;

    @Autowired
    public ScmWikiRepository(ScmHttpClient scmHttpClient) {
        this.scmHttpClient = scmHttpClient;
    }

    @Override
    public Optional<Wiki> findById(WikiId id) {
        Optional<RepositoryDTO> dto = getRepository(id);
        if (dto.isPresent()) {
            return getSettings(id)
                    .map(s -> createWiki(id, dto.get(), s));
        }
        return Optional.empty();
    }

    private Wiki createWiki(WikiId id, RepositoryDTO repository, WikiSettings settings) {
        DisplayName displayName = displayName(repository, settings);
        Path directory = directory(settings, "docs");
        Path landingPage = landingPage(settings, "Home");

        return new Wiki(id, repository.url, displayName, directory, landingPage);
    }

    private DisplayName displayName(RepositoryDTO repository, WikiSettings settings) {
        String name = MoreObjects.firstNonNull(emptyToNull(settings.getDisplayName()), emptyToNull(repository.name));
        return DisplayName.valueOf(name);
    }

    private Path directory( WikiSettings settings, String defaultDirectory ) {
        String name = MoreObjects.firstNonNull(emptyToNull(settings.getDirectory()), defaultDirectory);
        return Path.valueOf(name);
    }

    private Path landingPage( WikiSettings settings, String defaultLandingPage ) {
        String name = MoreObjects.firstNonNull(emptyToNull(settings.getLandingPage()), defaultLandingPage);
        return Path.valueOf(name);
    }

    private Optional<RepositoryDTO> getRepository(WikiId id) {
        return scmHttpClient.get("/api/rest/repositories/{id}.json", RepositoryDTO.class, id.getRepositoryID());
    }

    private Optional<WikiSettings> getSettings(WikiId id) {
        return scmHttpClient.getEntity(
                "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
                String.class,
                id.getRepositoryID(),
                SETTINGS_FILE,
                id.getBranch()
            )
            .filter(e -> e.getStatusCode().is2xxSuccessful())
            .map(e -> Strings.nullToEmpty(e.getBody()))
            .map(this::readSettings);
    }

    private WikiSettings readSettings(String content) {
        if (Strings.isNullOrEmpty(content)) {
            return new WikiSettings();
        }
        return yaml.loadAs(content, WikiSettings.class);
    }

    @VisibleForTesting
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class RepositoryDTO {
        private String name;
        private URL url;

        public RepositoryDTO() {
        }

        public RepositoryDTO(String name, URL url) {
            this.name = name;
            this.url = url;
        }
    }

    // public because of snakeyaml
    public static class WikiSettings {
        private String displayName;
        private String directory;
        private String landingPage;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public String getLandingPage() {
            return landingPage;
        }

        public void setLandingPage(String landingPage) {
            this.landingPage = landingPage;
        }
    }
}
