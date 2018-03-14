package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class PullChangesInjectionFactory {

    @Bean
    @ConditionalOnProperty(name = "git.pull-strategy", havingValue = "always")
    public PullChangesStrategy createAlwaysPullStrategy() {
        return new AlwaysPullChangesStrategy();
    }

    @Bean
    @ConditionalOnProperty(name = "git.pull-strategy", havingValue = "once-a-minute")
    public PullChangesStrategy createOnceAMinutePullStrategy() {
        return new TimeBasedPullChangesStrategy(TimeUnit.MINUTES.toMillis(1L));
    }

    @Bean
    @ConditionalOnProperty(name = "git.pull-strategy", havingValue = "every-ten-seconds", matchIfMissing = true)
    public PullChangesStrategy createEveryTenSecondsPullStrategy() {
        return new TimeBasedPullChangesStrategy(TimeUnit.SECONDS.toMillis(10L));
    }
}
