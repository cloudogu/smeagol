package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PageController.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class PageControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private PageRepository pageRepository;

    @MockBean
    private WildcardPathExtractor pathExtractor;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // we need to mockthe WildcardPathExtractor, because request.getSerlvetPath seems to be empty in MockMvc
        when(pathExtractor.extract(any(HttpServletRequest.class), anyString())).thenReturn("docs/Home");
    }

    @Test
    public void findByWikiIdAndPath() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        Path path = Path.valueOf("docs/Home");
        Author author = new Author(DisplayName.valueOf("Tricia McMillian"), Email.valueOf("trillian@hitchhicker.com"));
        Content content = Content.valueOf("# Markdown rocks");
        Instant instant = Instant.ofEpochSecond(481902134L);

        Page page = new Page(wikiId, path, author, content, instant);
        when(pageRepository.findByWikiIdAndPath(wikiId, path)).thenReturn(Optional.of(page));

        String self = "http://localhost/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path", is("docs/Home")))
                .andExpect(jsonPath("$._links.self.href", is(self)));
    }

    @Test
    public void findByWikiIdAndPathNotFound() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        when(pageRepository.findByWikiIdAndPath(wikiId, Path.valueOf("docs/Home"))).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }
}