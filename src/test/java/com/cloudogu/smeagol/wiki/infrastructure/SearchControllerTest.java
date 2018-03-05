package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.SearchResult;
import com.cloudogu.smeagol.wiki.domain.SearchResultRepository;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private SearchResultRepository repository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void searchByWikiIdAndQuery() throws Exception {
        List<SearchResult> searchResults = Lists.newArrayList(
                new SearchResult(WIKI_ID_42, Path.valueOf("docs/Home")),
                new SearchResult(WIKI_ID_42, Path.valueOf("docs/Galaxy"))
        );
        when(repository.search(WIKI_ID_42, "guide")).thenReturn(searchResults);

        String base = "http://localhost/api/v1/repositories/42/branches/galaxy/pages/";
        String selfHome = base + "docs/Home";
        String selfGalaxy = base + "docs/Galaxy";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/42/branches/galaxy/search?query=guide")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].path", is("docs/Home")))
                .andExpect(jsonPath("$.[0]._links.self.href", is(selfHome)))
                .andExpect(jsonPath("$.[1].path", is("docs/Galaxy")))
                .andExpect(jsonPath("$.[1]._links.self.href", is(selfGalaxy)));
    }
}