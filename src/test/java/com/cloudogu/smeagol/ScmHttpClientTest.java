package com.cloudogu.smeagol;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@RestClientTest(ScmHttpClient.class)
public class ScmHttpClientTest {

    @Autowired
    private MockRestServiceServer server;


    @MockBean
    private AccountService accountService;

    @Autowired
    private ScmHttpClient httpClient;

    @Before
    public void setUp() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);
    }

    @Test
    public void testAuthentication() {
        this.server.expect(requestTo("/hitchhiker/trillian"))
                .andExpect(header("Authorization", "Basic dHJpbGxpYW46dHJpbGxpYW4xMjM="))
                .andRespond(withSuccess());

        httpClient.get("/hitchhiker/trillian", Void.class);
    }

    @Test
    public void testGet() {
        this.server.expect(requestTo("/hitchhiker/trillian"))
                .andRespond(withSuccess("Hello Trillian", MediaType.TEXT_PLAIN));

        String content = httpClient.get("/hitchhiker/trillian", String.class).get();
        assertEquals("Hello Trillian", content);
    }

    @Test
    public void testGetWithUrlVariables() {
        this.server.expect(requestTo("/hitchhiker/trillian/hello"))
                .andRespond(withSuccess());

        httpClient.get("/hitchhiker/{name}/{action}", Void.class, "trillian", "hello");
    }

    @Test
    public void testGetWithNotFound() {
        this.server.expect(requestTo("/hitchhiker/trillian"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Optional<String> result = httpClient.get("/hitchhiker/trillian", String.class);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetEntity() {
        this.server.expect(requestTo("/hitchhiker/trillian"))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        Optional<ResponseEntity<String>> result = httpClient.getEntity("/hitchhiker/trillian", String.class);
        assertTrue(result.get().getStatusCode().is2xxSuccessful());
    }

}