package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Directory;
import com.cloudogu.smeagol.wiki.domain.DirectoryRepository;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.cloudogu.smeagol.wiki.DomainTestData.WIKI_ID_42;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DirectoryController.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DirectoryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private DirectoryRepository directoryRepository;

    @MockBean
    private WildcardPathExtractor pathExtractor;

    private MockMvc mockMvc;

    @Before
    public void setUpWebApplicationContext() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();


    }

    private Path mockRequestedPath(String value) {
        Path path = Path.valueOf(value);
        // we need to mock the WildcardPathExtractor, because request.getServletPath seems to be empty in MockMvc
        when(
                pathExtractor.extractPathFromRequest(
                        any(HttpServletRequest.class),
                        anyString(),
                        any(WikiId.class)
                )
        ).thenReturn(path);

        return path;
    }

    @Test
    public void findDirectoryNotFound() throws Exception {
        Path path = mockRequestedPath("docs/");

        when(directoryRepository.findByWikiIdAndPath(WIKI_ID_42, path)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/42/branches/galaxy/directories/docs/")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findDirectory() throws Exception {
        Path path = mockRequestedPath("docs/");

        List<Path> childDirectories = Lists.newArrayList(Path.valueOf("docs/sub/"));
        List<Path> childPages = Lists.newArrayList(Path.valueOf("docs/Home"));

        Directory directory = new Directory(WIKI_ID_42, path, childDirectories, childPages);
        when(directoryRepository.findByWikiIdAndPath(WIKI_ID_42, path)).thenReturn(Optional.of(directory));

        String base = "http://localhost/api/v1/repositories/42/branches/galaxy";
        String self = base + "/directories/docs";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/42/branches/galaxy/directories/docs/")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path", is("docs/")))
                .andExpect(jsonPath("$._links.self.href", is(self)))
                .andExpect(jsonPath("$.children.[0].name", is("sub")))
                .andExpect(jsonPath("$.children.[0].type", is("directory")))
                .andExpect(jsonPath("$.children.[0]._links.self.href", is(self + "/sub")))
                .andExpect(jsonPath("$.children.[1].name", is("Home")))
                .andExpect(jsonPath("$.children.[1].type", is("page")))
                .andExpect(jsonPath("$.children.[1]._links.self.href", is(base + "/pages/docs/Home")));
    }

    @Test
    public void findDirectoryWithoutEndingSlash() throws Exception {
        mockRequestedPath("docs");
        Path path = Path.valueOf("docs/");

        Directory directory = new Directory(WIKI_ID_42, path, Collections.emptyList(), Collections.emptyList());
        when(directoryRepository.findByWikiIdAndPath(WIKI_ID_42, path)).thenReturn(Optional.of(directory));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/42/branches/galaxy/directories/docs")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }
}
