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
    @ConditionalOnProperty(name = "git.pull-strategy", havingValue = "once-a-minute", matchIfMissing = true)
    public PullChangesStrategy createOnceAMinutePullStrategy() {
        return new TimeBasedPullChangesStrategy(TimeUnit.MINUTES.toMillis(1L));
    }

}
