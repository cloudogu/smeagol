package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.junit.jupiter.api.Test;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeBasedPullChangesStrategyTest {

    @Test
    @SuppressWarnings("squid:S2925") // ignore thread sleep warning
    public void shouldPull() throws InterruptedException {
        TimeBasedPullChangesStrategy strategy = new TimeBasedPullChangesStrategy(1000L);
        assertThat(strategy.shouldPull(WIKI_ID_42)).isTrue();
        assertThat(strategy.shouldPull(WIKI_ID_42)).isFalse();
        assertThat(strategy.shouldPull(new WikiId("42", "develop"))).isTrue();
        Thread.sleep(1001L);
        assertThat(strategy.shouldPull(WIKI_ID_42)).isTrue();
    }
}
