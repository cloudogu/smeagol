package com.cloudogu.smeagol.wiki.infrastructure;

import com.google.common.base.Joiner;
import org.junit.Test;

import java.io.File;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.assertj.core.api.Assertions.assertThat;


public class DirectoryResolverTest {

    @Test
    public void testResolve() {
        DirectoryResolver resolver = new DirectoryResolver("home");
        assertPath(resolver.resolve(WIKI_ID_42), "home", "42", "galaxy");
    }

    @Test
    public void testResolveSearchIndex() {
        DirectoryResolver resolver = new DirectoryResolver("home");
        assertPath(resolver.resolveSearchIndex(WIKI_ID_42), "home", "42", "galaxy", ".git", "search-index");
    }

    private void assertPath(File actual, String... expected) {
        assertThat(actual.getPath()).isEqualTo(Joiner.on(File.separator).join(expected));
    }
}