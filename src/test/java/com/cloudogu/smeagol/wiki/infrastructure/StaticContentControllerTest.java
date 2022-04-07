package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StaticContentController.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StaticContentControllerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final WikiId wikiId = new WikiId("4xQfahsId3", "master");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private GitClientProvider gitClientProvider;

    @MockBean
    private WildcardPathExtractor pathExtractor;

    @Mock
    private GitClient gitClient;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        when(gitClientProvider.createGitClient(wikiId)).thenReturn(gitClient);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // we need to mock the WildcardPathExtractor, because request.getServletPath seems to be empty in MockMvc
        when(
                pathExtractor.extractPathFromRequest(
                        any(HttpServletRequest.class),
                        anyString(),
                        any(WikiId.class)
                )
        ).thenReturn(Path.valueOf("docs/arch.txt"));
    }

    @Test
    public void findStaticContent() throws Exception {
        File file = temporaryFolder.newFile();
        Files.write("hitchhiker's guide to the galaxy", file, Charsets.UTF_8);
        when(gitClient.file("docs/arch.txt")).thenReturn(file);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master/static/docs/arch.txt")
                .contentType("text/plain"))
                .andExpect(status().isOk())
                .andExpect(content().string("hitchhiker's guide to the galaxy"));
    }

    @Test
    public void findByIdNotFound() throws Exception {
        File file = temporaryFolder.newFile();
        assertTrue(file.delete());
        when(gitClient.file("docs/arch.txt")).thenReturn(file);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master/static/docs/arch.txt"))
                .andExpect(status().isNotFound());
    }

}
