package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Resolves directories for wiki.
 */
@Component
public class DirectoryResolver {

    private static final String DOT_GIT = ".git";
    private static final String SEARCH_INDEX = "search-index";

    private final String homeDirectory;

    @Autowired
    public DirectoryResolver(@Value("${homeDirectory}") String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    /**
     * Resolve wiki directory.
     *
     * @param id id of wiki
     *
     * @return wiki direcotry
     */
    public File resolve(WikiId id) {
        File wikiDirectory = new File(homeDirectory, id.getRepositoryID());
        return new File(wikiDirectory, id.getBranch());
    }

    /**
     * Resolve search index directory.
     *
     * @param id id of wiki
     *
     * @return search index directory
     */
    public File resolveSearchIndex(WikiId id) {
        File repository = resolve(id);
        File dotGit = new File(repository, DOT_GIT);
        return new File(dotGit, SEARCH_INDEX);
    }
}
