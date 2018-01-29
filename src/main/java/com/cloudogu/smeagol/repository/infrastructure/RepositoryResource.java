package com.cloudogu.smeagol.repository.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.util.HashMap;
import java.util.Map;

public class RepositoryResource extends ResourceSupport {

    private String name;
    private String description;
    private String lastModified;
    private Map<String, Object> embedded;

    public RepositoryResource(String name, String description, String lastModified) {
        this.name = name;
        this.description = description;
        this.lastModified = lastModified;
        this.embedded = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLastModified() {
        return lastModified;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("_embedded")
    public Map<String, Object> getEmbedded() {
        return embedded;
    }

    public void embed(String relationShip, Object resource) {
        embedded.put(relationShip, resource);
    }
}
