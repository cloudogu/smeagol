package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.AccountTestData;
import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.repository.domain.Repository;
import com.cloudogu.smeagol.repository.domain.RepositoryId;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest({ScmRepositoryRepository.class, ScmHttpClient.class})
public class ScmRepositoryRepositoryTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ScmRepositoryRepository repository;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ScmHttpClient httpClient;

    @Before
    public void setUp() {
        // clear cache to avoid side effects
        httpClient.invalidateCache();
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);
    }

    @Test
    public void testFindAll() throws IOException {
        URL url = Resources.getResource("com/cloudogu/smeagol/repository/infrastructure/repositories.json");
        String content = Resources.toString(url, Charsets.UTF_8);

        server.expect(requestTo("/api/rest/repositories.json"))
                .andExpect(header("Authorization", "Basic dHJpbGxpYW46dHJpbGxpYW4xMjM="))
                .andRespond(withSuccess(content, MediaType.APPLICATION_JSON));

        Iterator<Repository> repositories = repository.findAll(Optional.empty()).iterator();

        Repository restaurant = repositories.next();
        assertEquals("30QQIOlg42", restaurant.getId().getValue());
        assertEquals("hitchhiker/restaurantAtTheEndOfTheUniverse", restaurant.getName().getValue());
        assertEquals(1513941908, restaurant.getLastModified().getEpochSecond());

        Repository heartOfGold = repositories.next();
        assertEquals("4xQfahsId3", heartOfGold.getId().getValue());
        assertEquals("Heart Of Gold", heartOfGold.getDescription().getValue());

        assertFalse(repositories.hasNext());
    }

    @Test
    public void testFindById() throws IOException {
        URL url = Resources.getResource("com/cloudogu/smeagol/repository/infrastructure/repository.json");
        String content = Resources.toString(url, Charsets.UTF_8);

        server.expect(requestTo("/api/rest/repositories/4xQfahsId3.json"))
                .andExpect(header("Authorization", "Basic dHJpbGxpYW46dHJpbGxpYW4xMjM="))
                .andRespond(withSuccess(content, MediaType.APPLICATION_JSON));

        RepositoryId id = RepositoryId.valueOf("4xQfahsId3");
        Repository heartOfGold = repository.findById(id).get();

        assertEquals(id, heartOfGold.getId());
        assertEquals("Heart Of Gold", heartOfGold.getDescription().getValue());
    }

    @Test
    public void testFindByIdNotFound() {
        server.expect(requestTo("/api/rest/repositories/4xQfahsId3.json"))
                .andExpect(header("Authorization", "Basic dHJpbGxpYW46dHJpbGxpYW4xMjM="))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        RepositoryId id = RepositoryId.valueOf("4xQfahsId3");
        Optional<Repository> heartOfGold = repository.findById(id);
        assertFalse(heartOfGold.isPresent());
    }

}
