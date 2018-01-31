package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
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

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WikiController.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class WikiControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private WikiRepository wikiRepository;

    @MockBean
    private WildcardPathExtractor pathExtractor;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(pathExtractor.extract(any(HttpServletRequest.class), anyString())).thenReturn("docs/Home");
    }

    @Test
    public void findById() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        String remoteUrl = "https://ecosystem.hitchhiker.com/scm/git/heartOfGold";

        Wiki wiki = new Wiki(
                wikiId,
                new URL(remoteUrl),
                DisplayName.valueOf("Heart Of Gold"),
                Path.valueOf("docs"),
                Path.valueOf("Home")
        );

        when(wikiRepository.findById(wikiId)).thenReturn(Optional.of(wiki));

        String self = "http://localhost/api/v1/repositories/4xQfahsId3/branches/master";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName", is("Heart Of Gold")))
                .andExpect(jsonPath("$.landingPage", is("docs/Home")))
                .andExpect(jsonPath("$._links.self.href", is(self)))
                .andExpect(jsonPath("$._links.repository.href", is(remoteUrl)))
                .andExpect(jsonPath("$._links.landingPage.href", is(self + "/pages/docs/Home")));
    }

    @Test
    public void findByIdNotFound() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        when(wikiRepository.findById(wikiId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

}