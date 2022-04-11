package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class DirectoryResourceAssembler extends RepresentationModelAssemblerSupport<Directory, DirectoryResource> {

    public DirectoryResourceAssembler() {
        super(DirectoryController.class, DirectoryResource.class);
    }

    @Override
    public @NotNull DirectoryResource toModel(Directory directory) {
        List<DirectoryEntryResource> entries = Lists.newArrayList();
        for (Path path : directory.getChildDirectories()) {
            DirectoryEntryResource entry = new DirectoryEntryResource(path.getName(), "directory");
            entry.add(selfDirectory(directory.getWikiId(), path));
            entries.add(entry);
        }
        for (Path path : directory.getChildPages()) {
            DirectoryEntryResource entry = new DirectoryEntryResource(path.getName(), "page");
            entry.add(selfPage(directory.getWikiId(), path));
            entries.add(entry);
        }
        DirectoryResource resource = new DirectoryResource(directory.getPath().getValue(), entries);
        resource.add(selfDirectory(directory.getWikiId(), directory.getPath()));
        return resource;
    }

    private Link selfDirectory(WikiId id, Path path) {
        return linkTo(DirectoryController.class, id.getRepositoryID(), id.getBranch())
                .slash(path.getValue())
                .withRel("self");
    }

    private Link selfPage(WikiId id, Path path) {
        return linkTo(PageController.class, id.getRepositoryID(), id.getBranch())
                .slash(path.getValue())
                .withRel("self");
    }
}
