package com.cloudogu.smeagol;

import de.triology.cb.CommandBus;
import de.triology.cb.decorator.LoggingCommandBus;
import de.triology.cb.spring.Registry;
import de.triology.cb.spring.SpringCommandBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import java.util.Collections;

/**
 * Main entry point for the whole application.
 */
@SpringBootApplication
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class Smeagol {

    private static final Logger LOG = LoggerFactory.getLogger(Smeagol.class);

    private final ApplicationContext applicationContext;
    private final Stage stage;

    /**
     * Creates a new Smeagol application in the requested stage.
     *
     * @param stageName name of stage
     */
    public Smeagol(ApplicationContext applicationContext, @Value("${stage}") String stageName) {
        this.applicationContext = applicationContext;
        stage = Stage.fromString(stageName);
        if (stage == Stage.DEVELOPMENT) {
            LOG.warn("smeagol is running in development stage, never use this stage for production deployments");
        }
    }

    @Bean
    public Stage stage() {
        return stage;
    }

    @Bean
    public CommandBus commandBus() {
        return new LoggingCommandBus(
            new SpringCommandBus(new Registry(applicationContext))
        );
    }

    @Bean
    public FilterRegistrationBean uiFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new UiFilter());
        registration.setUrlPatterns(Collections.singleton("/*"));
        return registration;
    }

    public static void main(String[] args) {
        // disable dev tools restart, because we are using spring loaded
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(Smeagol.class, args);
    }
}
