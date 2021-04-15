package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.ScmHttpClientResponse;
import com.cloudogu.smeagol.wiki.domain.*;
import com.cloudogu.smeagol.wiki.usecase.FailedToInitWikiException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.io.Files;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

import static com.google.common.base.Strings.emptyToNull;

@Service
public class ScmWikiRepository implements WikiRepository {

    @VisibleForTesting
    static final String SETTINGS_FILE = ".smeagol.yml";

    private final ScmHttpClient scmHttpClient;
    private final DirectoryResolver directoryResolver;
    private final AccountService accountService;
    private final ApplicationEventPublisher publisher;
    private final PullChangesStrategy strategy;

    @Autowired
    public ScmWikiRepository(ScmHttpClient scmHttpClient, ApplicationEventPublisher publisher, PullChangesStrategy strategy, DirectoryResolver directoryResolver, AccountService accountService) {
        this.scmHttpClient = scmHttpClient;
        this.directoryResolver = directoryResolver;
        this.accountService = accountService;
        this.publisher = publisher;
        this.strategy = strategy;
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
    public Wiki init(WikiId wikiId, Commit commit, WikiSettings settings) throws IOException, GitAPIException {
        Optional<RepositoryDTO> repository = getRepository(wikiId);
        if (repository.isEmpty()) {
            throw new FailedToInitWikiException(wikiId, settings);
        }
        Account account = accountService.get();
        Wiki newWiki = new Wiki(wikiId, repository.get().url, settings.getDisplayName(),
            RepositoryName.valueOf(repository.get().name), settings.getDirectory(), settings.getLandingPage());
        EventDelayer eventDelayer = new EventDelayer(this.publisher);
        GitClient gitClient = new GitClient(eventDelayer, this.directoryResolver, this.strategy, account, newWiki);
        gitClient.refresh();
        try {
            File file = gitClient.file(SETTINGS_FILE);
            // TODO: set correct file content
            Files.asCharSink(file, Charsets.UTF_8).write("");
            gitClient.commit(
                SETTINGS_FILE,
                commit.getAuthor().getDisplayName().getValue(),
                commit.getAuthor().getEmail().getValue(),
                commit.getMessage().getValue()
            );
        } catch (Exception e) {
            e.printStackTrace();
            // delete the local repository if the init failed
            gitClient.deleteClone();
            throw new FailedToInitWikiException(wikiId, settings);
        }

        eventDelayer.forwardEvents();

        return newWiki;
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

        public void forwardEvents() {
            for (ApplicationEvent event : queue) {
                this.publisher.publishEvent(event);
            }
        }

        @Override
        public void publishEvent(Object o) {
        }
    }
}
