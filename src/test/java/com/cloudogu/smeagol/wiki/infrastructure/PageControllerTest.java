package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.cloudogu.smeagol.wiki.usecase.CreatePageCommand;
import com.cloudogu.smeagol.wiki.usecase.DeletePageCommand;
import com.cloudogu.smeagol.wiki.usecase.EditPageCommand;
import com.cloudogu.smeagol.wiki.usecase.RestorePageCommand;
import de.triology.cb.CommandBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
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

    @MockBean
    private CommandBus commandBus;

    @Captor
    private ArgumentCaptor<EditPageCommand> editCommandCaptor;

    @Captor
    private ArgumentCaptor<CreatePageCommand> createCommandCaptor;

    @Captor
    private ArgumentCaptor<DeletePageCommand> deleteCommandCaptor;

    @Captor
    private ArgumentCaptor<RestorePageCommand> restoreCommandCaptor;

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
        ).thenReturn(Path.valueOf("docs/Home"));
    }

    private Page createTestPage(WikiId wikiId, Path path) {
        Content content = Content.valueOf("# Markdown rocks");

        CommitId commitId = CommitId.valueOf("05b1c3b1e3d3e3e11cd337dd645fd19afe6086d0");
        Instant instant = Instant.ofEpochSecond(481902134L);
        Author author = new Author(DisplayName.valueOf("Tricia McMillian"), Email.valueOf("trillian@hitchhicker.com"));
        Message message = Message.valueOf("Hitchhiker rocks");

        Commit commit = new Commit(commitId, instant, author, message);
        return new Page(wikiId, path, content, commit);
    }

    @Test
    public void findByWikiIdAndPath() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        Path path = Path.valueOf("docs/Home");

        Page page = createTestPage(wikiId, path);

        when(pageRepository.findByWikiIdAndPath(wikiId, path)).thenReturn(Optional.of(page));

        String self = "http://localhost/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path", is("docs/Home")))
                .andExpect(jsonPath("$._links.self.href", is(self)))
                .andExpect(jsonPath("$._links.delete.href", is(self)))
                .andExpect(jsonPath("$._links.edit.href", is(self)))
                .andExpect(jsonPath("$._links.move.href", is(self)));
    }

    @Test
    public void findByWikiIdAndPathAndCommit() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        Path path = Path.valueOf("docs/Home");
        Page page = createTestPage(wikiId, path);
        String commitId = page.getCommit().get().getId().get().getValue();

        String pagePath = "/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home";
        String requestPath = pagePath + "?commit=" + commitId;
        String self = "http://localhost" + requestPath;


        when(pageRepository.findByWikiIdAndPathAndCommit(wikiId, path, CommitId.valueOf(commitId))).thenReturn(Optional.of(page));
        mockMvc.perform(MockMvcRequestBuilders.get(requestPath)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", is(self)))
                .andExpect(jsonPath("$._links.restore.href", is("http://localhost" + pagePath)));

        verify(pageRepository).findByWikiIdAndPathAndCommit(wikiId, path, CommitId.valueOf(commitId));
    }

    @Test
    public void findByWikiIdAndPathNotFound() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        when(pageRepository.findByWikiIdAndPath(wikiId, Path.valueOf("docs/Home"))).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void edit() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        Path path = Path.valueOf("docs/Home");

        when(pageRepository.exists(wikiId, path)).thenReturn(true);

        String content = "{\"message\": \"Hello\", \"content\": \"i said hello\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home")
                .content(content)
                .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(commandBus).execute(editCommandCaptor.capture());

        EditPageCommand pageCommand = editCommandCaptor.getValue();
        assertEquals(wikiId, pageCommand.getWikiId());
        assertEquals(path, pageCommand.getPath());
        assertEquals("Hello", pageCommand.getMessage().getValue());
        assertEquals("i said hello", pageCommand.getContent().getValue());
    }

    @Test
    public void create() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        Path path = Path.valueOf("docs/Home");

        String content = "{\"message\": \"Hello\", \"content\": \"i said hello\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home")
                .content(content)
                .contentType("application/json"))
                .andExpect(status().isCreated());

        verify(commandBus).execute(createCommandCaptor.capture());

        CreatePageCommand pageCommand = createCommandCaptor.getValue();
        assertEquals(wikiId, pageCommand.getWikiId());
        assertEquals(path, pageCommand.getPath());
        assertEquals("Hello", pageCommand.getMessage().getValue());
        assertEquals("i said hello", pageCommand.getContent().getValue());
    }

    @Test
    public void delete() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        Path path = Path.valueOf("docs/Home");

        when(pageRepository.exists(wikiId, path)).thenReturn(true);

        String content = "{\"message\": \"Hello\"}";
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home")
                .content(content)
                .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(commandBus).execute(deleteCommandCaptor.capture());

        DeletePageCommand pageCommand = deleteCommandCaptor.getValue();
        assertEquals(wikiId, pageCommand.getWikiId());
        assertEquals(path, pageCommand.getPath());
        assertEquals("Hello", pageCommand.getMessage().getValue());
    }

    @Test
    public void restore() throws Exception {
        WikiId wikiId = new WikiId("4xQfahsId3", "master");
        Path path = Path.valueOf("docs/Home");

        String content = "{\"restore\": \"42\", \"message\": \"Hello\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repositories/4xQfahsId3/branches/master/pages/docs/Home")
                .content(content)
                .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(commandBus).execute(restoreCommandCaptor.capture());

        RestorePageCommand pageCommand = restoreCommandCaptor.getValue();
        assertEquals(wikiId, pageCommand.getWikiId());
        assertEquals(path, pageCommand.getPath());
        assertEquals("Hello", pageCommand.getMessage().getValue());
        assertEquals("42", pageCommand.getCommitId().getValue());
    }
}