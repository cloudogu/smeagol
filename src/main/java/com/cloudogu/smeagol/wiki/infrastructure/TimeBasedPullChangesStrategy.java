package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Pull changes time based.
 */
public class TimeBasedPullChangesStrategy implements PullChangesStrategy {

    private static final Long ZERO = 0L;

    private final ConcurrentHashMap<WikiId, Long> timestamps = new ConcurrentHashMap<>();


    private final long duration;

    public TimeBasedPullChangesStrategy(long duration) {
        this.duration = duration;
    }

    @Override
    public boolean shouldPull(WikiId wikiId) {
        long timestamp = timestamps.getOrDefault(wikiId, ZERO) + duration;

        long now = System.currentTimeMillis();
        boolean shouldPull = timestamp < now;
        if (shouldPull) {
            timestamps.put(wikiId, now);
        }
        return shouldPull;
    }
}
