package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.ScmHttpClientResponse;
import com.cloudogu.smeagol.wiki.domain.*;
import com.cloudogu.smeagol.wiki.usecase.FailedToInitWikiException;
import com.cloudogu.smeagol.wiki.usecase.FailedToSaveWikiException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.io.Files;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import static com.google.common.base.Strings.emptyToNull;

@Service
public class ScmWikiRepository implements WikiRepository {

    @VisibleForTesting
    static final String SETTINGS_FILE = ".smeagol.yml";

    private final ScmHttpClient scmHttpClient;
    private final GitClientProviderForScmWikiRepository gitClientProvider;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public ScmWikiRepository(ScmHttpClient scmHttpClient, GitClientProviderForScmWikiRepository gitClientProvider, ApplicationEventPublisher publisher) {
        this.scmHttpClient = scmHttpClient;
        this.gitClientProvider = gitClientProvider;
        this.publisher = publisher;
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


    @Override
    public Wiki init(WikiId wikiId, Commit commit, WikiSettings settings) {
        RepositoryDTO repository = getRepository(wikiId)
            .orElseThrow(() -> new FailedToInitWikiException("could not get repository"));
        Wiki newWiki = new Wiki(wikiId, repository.url, settings.getDisplayName(),
            RepositoryName.valueOf(repository.name), settings.getDirectory(), settings.getLandingPage());
        EventDelayer eventDelayer = new EventDelayer(this.publisher);
        GitClient gitClient = this.gitClientProvider.createGitClient(eventDelayer, newWiki);

        try {
            gitClient.createClone();
            File file = gitClient.file(SETTINGS_FILE);
            if (file.exists()) {
                throw new FailedToInitWikiException("settings file already exists");
            }
            writeSettingsToFile(gitClient, commit, settings, file);
        } catch (Exception e) {
            try {
                // delete the local repository if the init failed
                gitClient.deleteClone();
            } catch (Exception ee) {
                throw new FailedToInitWikiException(ee.getMessage());
            }
            throw new FailedToInitWikiException(e.getMessage());
        }

        // invalidate cache since the wiki response changes
        this.scmHttpClient.invalidateCache();
        eventDelayer.forwardEvents();

        return newWiki;
    }

    private void writeSettingsToFile(GitClient gitClient, Commit commit, WikiSettings settings, File file) throws IOException, GitAPIException {
        WikiSettingsFile settingsFile = new WikiSettingsFile();
        settingsFile.setDirectory(settings.getDirectory().getValue());
        settingsFile.setLandingPage(settings.getLandingPage().getValue());
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);

        String fileContent = yaml.dumpAs(settingsFile, Tag.MAP, null);
        Files.asCharSink(file, StandardCharsets.UTF_8).write(fileContent);
        gitClient.commit(
            SETTINGS_FILE,
            commit.getAuthor().getDisplayName().getValue(),
            commit.getAuthor().getEmail().getValue(),
            commit.getMessage().getValue()
        );
    }

    @Override
    public void save(WikiId wikiId, Commit commit, WikiSettings settings) {
        if (getRepository(wikiId).isEmpty()) {
            throw new FailedToSaveWikiException("could not get repository");
        }
        Wiki wiki = findById(wikiId).orElseThrow(() -> new FailedToSaveWikiException("could not find wiki"));
        GitClient gitClient = this.gitClientProvider.createGitClient(this.publisher, wiki);

        try {
            gitClient.refresh();
            File file = gitClient.file(SETTINGS_FILE);
            if (!file.exists()) {
                throw new FailedToSaveWikiException("could not find settings file for wiki");
            }
            writeSettingsToFile(gitClient, commit, settings, file);
        } catch (Exception e) {
            throw new FailedToSaveWikiException(e.getMessage());
        }

        // invalidate cache since the wiki response changes
        this.scmHttpClient.invalidateCache();
    }

    private Wiki createWiki(WikiId id, RepositoryDTO repository, WikiSettingsFile settings) {
        DisplayName displayName = displayName(repository, settings);
        RepositoryName repositoryName = RepositoryName.valueOf(repository.name);
        Path directory = directory(settings, "docs");
        Path landingPage = landingPage(settings, "Home");

        return new Wiki(id, repository.url, displayName, repositoryName, directory, landingPage);
    }

    private DisplayName displayName(RepositoryDTO repository, WikiSettingsFile settings) {
        String name = MoreObjects.firstNonNull(emptyToNull(settings.getDisplayName()), emptyToNull(repository.name));
        return DisplayName.valueOf(name);
    }

    private Path directory(WikiSettingsFile settings, String defaultDirectory) {
        String name = MoreObjects.firstNonNull(emptyToNull(settings.getDirectory()), defaultDirectory);
        return Path.valueOf(name);
    }

    private Path landingPage(WikiSettingsFile settings, String defaultLandingPage) {
        String name = MoreObjects.firstNonNull(emptyToNull(settings.getLandingPage()), defaultLandingPage);
        return Path.valueOf(name);
    }

    private Optional<RepositoryDTO> getRepository(WikiId id) {
        return scmHttpClient.get("/api/rest/repositories/{id}.json", RepositoryDTO.class, id.getRepositoryID());
    }

    private Optional<WikiSettingsFile> getSettings(WikiId id) {
        ScmHttpClientResponse<String> response = scmHttpClient.getEntity(
            "/api/rest/repositories/{id}/content?path={conf}&revision={branch}",
            String.class,
            id.getRepositoryID(),
            SETTINGS_FILE,
            id.getBranch()
        );

        WikiSettingsFile settings = null;
        if (response.isSuccessful()) {
            settings = response.getBody()
                .map(this::readSettings)
                .orElse(new WikiSettingsFile());
        }
        return Optional.ofNullable(settings);
    }

    private WikiSettingsFile readSettings(String content) {
        // create a new object since Yaml is not thread safe
        Yaml yaml = new Yaml();
        return yaml.loadAs(content, WikiSettingsFile.class);
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
    public static class WikiSettingsFile {
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

    private static class EventDelayer implements ApplicationEventPublisher {

        ArrayList<ApplicationEvent> queue = new ArrayList<>();
        ApplicationEventPublisher publisher;

        public EventDelayer(ApplicationEventPublisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public void publishEvent(ApplicationEvent event) {
            queue.add(event);

        }

        @Override
        public void publishEvent(Object o) {
            queue.add((ApplicationEvent) o);
        }

        public void forwardEvents() {
            for (ApplicationEvent event : queue) {
                this.publisher.publishEvent(event);
            }
        }
    }
}
