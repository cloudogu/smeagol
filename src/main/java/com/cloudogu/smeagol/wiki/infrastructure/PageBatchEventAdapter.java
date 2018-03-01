package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Page;
import com.cloudogu.smeagol.wiki.domain.PageRepository;
import com.cloudogu.smeagol.wiki.domain.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The PageBatchEventAdapter is an adapter between {@link RepositoryChangedEvent} and {@link PageBatchEvent}.
 */
@Component
public class PageBatchEventAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(PageBatchEventAdapter.class);

    private final ApplicationEventPublisher publisher;
    private final PageRepository pageRepository;

    @Autowired
    public PageBatchEventAdapter(ApplicationEventPublisher publisher, PageRepository pageRepository) {
        this.publisher = publisher;
        this.pageRepository = pageRepository;
    }

    @EventListener
    public void handle(RepositoryChangedEvent event) {
        PageBatchEvent batchEvent = new PageBatchEvent(event.getWikiId());
        for (RepositoryChangedEvent.Change change : event) {
            addChangeToEvent(batchEvent, change);
        }

        publisher.publishEvent(batchEvent);
    }

    private void addChangeToEvent(PageBatchEvent batchEvent, RepositoryChangedEvent.Change change) {
        Path path = Pages.pagepath(change.getPath());
        if (change.getType() == ChangeType.DELETED) {
            batchEvent.deleted(path);
        } else {
            Optional<Page> optionalPage = pageRepository.findByWikiIdAndPath(batchEvent.getWikiId(), path);
            if (optionalPage.isPresent()) {
                if (change.getType() == ChangeType.ADDED) {
                    batchEvent.added(optionalPage.get());
                } else {
                    batchEvent.modified(optionalPage.get());
                }
            } else {
                LOG.warn("could not find page {} of wiki {}", path, batchEvent.getWikiId());
            }
        }

    }


}
