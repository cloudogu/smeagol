package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.*;
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
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RepositoryController.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RepositoryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private RepositoryRepository repositoryRepository;

    @MockBean
    private BranchRepository branchRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void findAll() throws Exception {
        List<Repository> repositories = Lists.newArrayList(
                RepositoryTestData.createHeartOfGold(),
                RepositoryTestData.createRestaurantAtTheEndOfTheUniverse()
        );
        when(repositoryRepository.findAll(false)).thenReturn(repositories);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.repositories.[0].id", is("4xQfahsId3")))
                .andExpect(jsonPath("$._embedded.repositories.[0].name", is("hitchhiker/heartOfGold")))
                .andExpect(jsonPath("$._embedded.repositories.[0]._links.self.href", is("http://localhost/api/v1/repositories/4xQfahsId3")))
                .andExpect(jsonPath("$._embedded.repositories.[0].lastModified", is("1985-04-09T10:15:30Z")))
                .andExpect(jsonPath("$._embedded.repositories.[1].id", is("30QQIOlg42")))
                .andExpect(jsonPath("$._embedded.repositories.[1].name", is("hitchhiker/restaurantAtTheEndOfTheUniverse")))
                .andExpect(jsonPath("$._embedded.repositories.[1]._links.self.href", is("http://localhost/api/v1/repositories/30QQIOlg42")))
                .andExpect(jsonPath("$._embedded.repositories.[1].lastModified", is("2018-01-28T16:58:42Z")));
    }

    @Test
    public void findAllMissingSmeagolPlugin() throws Exception {
        when(repositoryRepository.findAll(false)).thenThrow(new MissingSmeagolPluginException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories")
            .contentType("application/json"))
            .andExpect(status().is5xxServerError())
            .andExpect(content().string("SCM is missing Smeagol plugin"));
    }

    @Test
    public void findById() throws Exception {
        RepositoryId id = RepositoryId.valueOf("4xQfahsId3");
        Optional<Repository> heartOfGold = Optional.of(RepositoryTestData.createHeartOfGold());
        when(repositoryRepository.findById(id)).thenReturn(heartOfGold);

        List<Branch> branches = Lists.newArrayList(
                new Branch(id, Name.valueOf("develop")),
                new Branch(id, Name.valueOf("master"))
        );
        when(branchRepository.findByRepositoryId(id)).thenReturn(branches);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("4xQfahsId3")))
                .andExpect(jsonPath("$.name", is("hitchhiker/heartOfGold")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/repositories/4xQfahsId3")))
                .andExpect(jsonPath("$._embedded.branches._embedded.branchResourceList[0].name", is("develop")))
                .andExpect(jsonPath("$._embedded.branches._embedded.branchResourceList[0]._links.self.href", is("http://localhost/api/v1/repositories/4xQfahsId3/branches/develop")))
                .andExpect(jsonPath("$._embedded.branches._embedded.branchResourceList[1].name", is("master")))
                .andExpect(jsonPath("$._embedded.branches._embedded.branchResourceList[1]._links.self.href", is("http://localhost/api/v1/repositories/4xQfahsId3/branches/master")));
    }

    @Test
    public void findByIdNotFound() throws Exception {
        RepositoryId id = RepositoryId.valueOf("4xQfahsId3");
        when(repositoryRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/repositories/4xQfahsId3")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }
}
