package com.cloudogu.smeagol.repository.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RepositoryResource extends ResourceSupport {

    @JsonProperty("id")
    private String id;
    private String name;
    private String description;
    private String lastModified;
    private Map<String, Object> embedded;

    public RepositoryResource(String id, String name, String description, String lastModified) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RepositoryResource resource = (RepositoryResource) o;
        return Objects.equals(id, resource.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id);
    }
}
