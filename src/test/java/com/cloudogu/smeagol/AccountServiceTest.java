package com.cloudogu.smeagol;

import com.google.common.collect.ImmutableMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apereo.cas.client.authentication.AttributePrincipal;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.util.Map;

import static com.cloudogu.smeagol.AccountService.shouldRefetchToken;
import static com.cloudogu.smeagol.AccountTestData.LONG_LASTING_JWT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests for {@link com.cloudogu.smeagol.AccountService}.
 *
 * @author Sebastian Sdorra
 */
@RunWith(SpringRunner.class)
@RestClientTest(AccountService.class)
public class AccountServiceTest {

    @MockBean
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @MockBean
    private ObjectFactory<HttpServletRequest> requestFactory;

    @Mock
    private AttributePrincipal principal;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private AccountService accountService;

    /**
     * Prepare mocks.
     */
    @Before
    public void setUpMocks() {
        when(request.getSession(true)).thenReturn(session);
        when(requestFactory.getObject()).thenReturn(request);
    }

    /**
     * Tests {@link AccountService#get()} without principal.
     */
    @Test
    public void testGetWithoutPrincipal() {
        expectedException.expect(AuthenticationException.class);
        expectedException.expectMessage("principal");

        accountService.get();
    }

    /**
     * Tests {@link AccountService#get()} without proxy ticket.
     */
    @Test
    public void testGetWithoutProxyTicket() {
        when(request.getUserPrincipal()).thenReturn(principal);

        expectedException.expect(AuthenticationException.class);
        expectedException.expectMessage("proxy");
        expectedException.expectMessage("ticket");

        accountService.get();
    }

    /**
     * Tests {@link AccountService#get()} with an error during access token fetch.
     */
    @Test
    public void testGetFailedToFetchAccessToken() {
        when(request.getUserPrincipal()).thenReturn(principal);
        String accessTokenEndpoint = "/api/v2/cas/auth/";
        when(principal.getProxyTicketFor("https://192.168.56.2/scm" + accessTokenEndpoint)).thenReturn("pt-123");

        server.expect(requestTo(accessTokenEndpoint)).andRespond(withNoContent());

        expectedException.expect(AuthenticationException.class);
        expectedException.expectMessage("could not get accessToken from scm endpoint");

        accountService.get();
    }


    /**
     * Tests {@link AccountService#get()}.
     *
     * @throws IOException
     */
    @Test
    public void testGet() {

        when(request.getUserPrincipal()).thenReturn(principal);
        String accessTokenEndpoint = "/api/v2/cas/auth/";
        when(principal.getProxyTicketFor("https://192.168.56.2/scm" + accessTokenEndpoint)).thenReturn("pt-123");
        Map<String, Object> attributes = ImmutableMap.of(
            "username", "admin", "displayName", "Administrator", "mail", "super@admin.org"
        );
        when(principal.getAttributes()).thenReturn(attributes);

        server.expect(requestTo(accessTokenEndpoint))
            .andExpect(header("Content-Type", "application/x-www-form-urlencoded"))
            .andExpect(content().string("ticket=pt-123"))
            .andRespond(withSuccess("jwtadmin", MediaType.TEXT_PLAIN));

        Account account = accountService.get();

        assertEquals("admin", account.getUsername());
        assertEquals("jwtadmin", account.getAccessToken());
        // calling two time to check if value is stored in member
        assertEquals("jwtadmin", account.getAccessToken());
        assertEquals("Administrator", account.getDisplayName());
        assertEquals("super@admin.org", account.getMail());

        verify(session).setAttribute(Account.class.getName(), account);

    }

    /**
     * Tests {@link AccountService#get()} from session cache.
     */
    @Test
    public void testGetGetFromSession() {
        Account account = new Account("hans", LONG_LASTING_JWT, "schalter", "hansamschalter@light.de");
        when(session.getAttribute(Account.class.getName())).thenReturn(account);
        Account returnedAccount = accountService.get();
        assertSame(account, returnedAccount);
    }

    @Test
    public void testShouldRefetchToken_expired() throws IOException {
        assertEquals(true, shouldRefetchToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImp0aSI6IjZSU2FKVmdWdkgiLCJpYXQiOjE1MjM2NzU2ODAsImV4cCI6MTUyMzY3OTI4MCwic2NtLW1hbmFnZXIucmVmcmVzaEV4cGlyYXRpb24iOjE2MjM3MTg4ODAyODQsInNjbS1tYW5hZ2VyLnBhcmVudFRva2VuSWQiOiI2UlNhSlZnVnZIIn0.ignored"));
    }

    @Test
    public void testShouldRefetchToken_valid() throws IOException {
        // Token expires in year 2271
        assertEquals(false, shouldRefetchToken(LONG_LASTING_JWT));
    }
}
