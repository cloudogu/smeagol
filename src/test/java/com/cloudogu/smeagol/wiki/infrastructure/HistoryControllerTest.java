package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.CommitRepository;
import com.cloudogu.smeagol.wiki.domain.History;
import com.cloudogu.smeagol.wiki.domain.WikiId;
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

import java.util.Arrays;

import static com.cloudogu.smeagol.wiki.DomainTestData.COMMIT;
import static com.cloudogu.smeagol.wiki.DomainTestData.PATH_HOME;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoryController.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class HistoryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private CommitRepository commitRepository;

    @MockBean
    private WildcardPathExtractor pathExtractor;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // we need to mock the WildcardPathExtractor, because request.getServletPath seems to be empty in MockMvc
        when(
                pathExtractor.extractPathFromRequest(
                        any(HttpServletRequest.class),
                        anyString(),
                        any(WikiId.class)
                )
        ).thenReturn(PATH_HOME);
    }

    @Test
    public void findForWikiIdAndPath() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        String remoteUrl = "https://ecosystem.hitchhiker.com/scm/git/heartOfGold";

        when(commitRepository.findHistoryByWikiIdAndPath(wikiId, PATH_HOME)).thenReturn(new History(wikiId, PATH_HOME, Arrays.asList(COMMIT)));

        String host = "http://localhost";
        String repositoryPath = "/api/v1/repositories/4xQfahsId3/branches/master";
        String self =  host + repositoryPath + "/history/docs/Home";
        String pagePath = host + repositoryPath + "/pages/docs/Home";

        mockMvc.perform(MockMvcRequestBuilders.get(repositoryPath + "/history/docs/Home")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path", is("docs/Home")))
                .andExpect(jsonPath("$._links.self.href", is(self)))
                .andExpect(jsonPath("$.commits[0].commitId", is(COMMIT.getId().get().getValue())))
                .andExpect(jsonPath("$.commits[0]._links.page.href", is(pagePath + "?commit=" + COMMIT.getId().get().getValue())))
                .andExpect(jsonPath("$.commits[0]._links.restore.href", is(pagePath)))
                .andExpect(jsonPath("$.commits[0].message", is(COMMIT.getMessage().getValue())));
    }

}
