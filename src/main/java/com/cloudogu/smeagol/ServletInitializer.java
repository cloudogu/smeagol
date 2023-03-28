package com.cloudogu.smeagol;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Allows for being deployed as a war into an external container
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Smeagol.class);
    }

    @Override
    public void onStartup(jakarta.servlet.ServletContext servletContext){
    }
}
