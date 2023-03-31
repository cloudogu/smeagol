package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.cloudogu.smeagol.wiki.usecase.EditWikiCommand;
import com.cloudogu.smeagol.wiki.usecase.InitWikiCommand;
import de.triology.cb.CommandBus;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URL;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
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

    @MockBean
    private CommandBus commandBus;

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
        ).thenReturn(Path.valueOf("docs/arch.txt"));
    }

    @Test
    public void findById() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        String remoteUrl = "https://ecosystem.hitchhiker.com/scm/git/heartOfGold";

        Wiki wiki = new Wiki(
            wikiId,
            new URL(remoteUrl),
            DisplayName.valueOf("Heart Of Gold"),
            RepositoryName.valueOf("namespace/repo"), Path.valueOf("docs"),
            Path.valueOf("Home")
        );

        when(wikiRepository.findById(wikiId)).thenReturn(Optional.of(wiki));

        String self = "http://localhost/api/v1/repositories/4xQfahsId3/branches/master";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.displayName", is("Heart Of Gold")))
            .andExpect(jsonPath("$.repositoryName", is("namespace/repo")))
            .andExpect(jsonPath("$.landingPage", is("docs/Home")))
            .andExpect(jsonPath("$.directory", is("docs")))
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

    @Test
    public void initWiki() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repositories/4xQfahsId3/branches/master")
            .content("{\"landingPage\": \"landing\", \"rootDir\": \"docsroot\"}")
            .contentType("application/json"))
            .andExpect(status().isCreated());
        ArgumentCaptor<InitWikiCommand> commandCaptor = ArgumentCaptor.forClass(InitWikiCommand.class);
        verify(commandBus).execute(commandCaptor.capture());

        assertEquals("master@4xQfahsId3", commandCaptor.getValue().getWikiId().toString());
        assertEquals(null, commandCaptor.getValue().getSettings().getDisplayName());
        assertEquals("docsroot", commandCaptor.getValue().getSettings().getDirectory().getValue());
        assertEquals("landing", commandCaptor.getValue().getSettings().getLandingPage().getValue());
        assertEquals("Initialize wiki (smeagol)", commandCaptor.getValue().getMessage().getValue());
    }

    @Test
    public void initWikiWrongArguments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repositories/4xQfahsId3/branches/master")
            .content("\"rootDir\": \"docsroot\"}")
            .contentType("application/json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void initWikiAlreadyExists() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        when(wikiRepository.findById(wikiId)).thenReturn(Optional.of(mock(Wiki.class)));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repositories/4xQfahsId3/branches/master")
            .content("{\"landingPage\": \"landing\", \"rootDir\": \"docsroot\"}")
            .contentType("application/json"))
            .andExpect(status().isBadRequest());
    }


    @Test
    public void editWiki() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        when(wikiRepository.findById(wikiId)).thenReturn(Optional.of(mock(Wiki.class)));
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/repositories/4xQfahsId3/branches/master")
            .content("{\"landingPage\": \"landing\", \"rootDir\": \"docsroot\"}")
            .contentType("application/json"))
            .andExpect(status().isNoContent());
        ArgumentCaptor<EditWikiCommand> commandCaptor = ArgumentCaptor.forClass(EditWikiCommand.class);
        verify(commandBus).execute(commandCaptor.capture());

        assertEquals("master@4xQfahsId3", commandCaptor.getValue().getWikiId().toString());
        assertEquals(null, commandCaptor.getValue().getSettings().getDisplayName());
        assertEquals("docsroot", commandCaptor.getValue().getSettings().getDirectory().getValue());
        assertEquals("landing", commandCaptor.getValue().getSettings().getLandingPage().getValue());
        assertEquals("Change settings of wiki (smeagol)", commandCaptor.getValue().getMessage().getValue());
    }

    @Test
    public void editWikiWrongArguments() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        when(wikiRepository.findById(wikiId)).thenReturn(Optional.of(mock(Wiki.class)));
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/repositories/4xQfahsId3/branches/master")
            .content("{\"landingPage\": \"landing\", \"rootDir\": \"\"}")
            .contentType("application/json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void editWikiNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/repositories/4xQfahsId3/branches/master")
            .content("{\"landingPage\": \"landing\", \"rootDir\": \"docsroot\"}")
            .contentType("application/json"))
            .andExpect(status().isNotFound());
    }


}
