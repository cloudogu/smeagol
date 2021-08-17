package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.AccountTestData;
import com.cloudogu.smeagol.ScmHttpClient;
import com.cloudogu.smeagol.repository.domain.Branch;
import com.cloudogu.smeagol.repository.domain.RepositoryId;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest({ScmBranchRepository.class, ScmHttpClient.class})
public class ScmBranchRepositoryTest {

    @Autowired
    private MockRestServiceServer server;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ScmBranchRepository repository;

    @Before
    public void setUp() throws IOException {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        URL url = Resources.getResource("com/cloudogu/smeagol/repository/infrastructure/branches.json");
        String content = Resources.toString(url, Charsets.UTF_8);

        this.server.expect(requestTo("/api/rest/repositories/30QQIOlg42/branches.json"))
                .andExpect(header("Authorization", "Basic X19iZWFyZXJfdG9rZW46and0dHJpbGxpYW4="))
                .andRespond(withSuccess(content, MediaType.APPLICATION_JSON));
    }

    @Test
    public void testFindByRepositoryId() {
        RepositoryId repositoryId = RepositoryId.valueOf("30QQIOlg42");
        Iterator<Branch> branches = repository.findByRepositoryId(repositoryId).iterator();

        Branch develop = branches.next();
        assertEquals("develop", develop.getName().getValue());
        assertEquals("30QQIOlg42", develop.getRepositoryId().getValue());
        Branch featureJunit5 = branches.next();
        assertEquals("feature/junit5", featureJunit5.getName().getValue());
        Branch master = branches.next();
        assertEquals("master", master.getName().getValue());

        assertFalse(branches.hasNext());
    }

}
