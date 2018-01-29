/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.smeagol;

import com.cloudogu.wiki.WikiAuthenticationException;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link com.cloudogu.smeagol.AccountService}.
 *
 * @author Sebastian Sdorra
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private ObjectFactory<HttpServletRequest> requestFactory;

    @Mock
    private AttributePrincipal principal;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
    public void testGetWithoutPrincipal(){
        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("principal");

        new AccountService(requestFactory, "com/cloudogu/smeagol").get();
    }

    /**
     * Tests {@link AccountService#get()} without proxy ticket.
     */
    @Test
    public void testGetWithoutProxyTicket(){
        when(request.getUserPrincipal()).thenReturn(principal);

        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("proxy");
        expectedException.expectMessage("ticket");

        new AccountService(requestFactory, "com/cloudogu/smeagol").get();
    }

    /**
     * Tests {@link AccountService#get()} with an error during clear pass
     * fetch.
     */
    @Test
    public void testGetFailedToFetchClearPass(){
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getProxyTicketFor("com/cloudogu/smeagol/clearPass")).thenReturn("pt-123");

        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("fetch");
        expectedException.expectMessage("clear pass");

        new AccountService(requestFactory, "com/cloudogu/smeagol").get();
    }

    /**
     * Tests {@link AccountService#get()} without password in clear pass
     * response.
     * @throws IOException
     */
    @Test
    public void testGetWithoutPassword() throws IOException {
        File directory = folder.newFolder();

        URL testResource = Resources.getResource("com/cloudogu/smeagol/clearpass-wo.xml");
        try (FileOutputStream fos = new FileOutputStream(new File(directory, "clearPass")) ) {
            Resources.copy(testResource, fos);
        }

        String url = directory.toURI().toURL().toExternalForm();

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getProxyTicketFor(url + "clearPass")).thenReturn("pt-123");

        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("extract");
        expectedException.expectMessage("clear pass");
        expectedException.expectMessage("password");

        new AccountService(requestFactory, url).get();
    }

    /**
     * Tests {@link AccountService#get()} with clear pass failure.
     * @throws IOException
     */
    @Test
    public void testGetWithClearPassFailure() throws IOException {
        File directory = folder.newFolder();

        URL testResource = Resources.getResource("com/cloudogu/smeagol/clearpass-failure.xml");
        try (FileOutputStream fos = new FileOutputStream(new File(directory, "clearPass")) ) {
            Resources.copy(testResource, fos);
        }

        String url = directory.toURI().toURL().toExternalForm();

        when(request.getUserPrincipal()).thenReturn(principal);
        // when(configuration.getCasUrl()).thenReturn(url);
        when(principal.getProxyTicketFor(url + "clearPass")).thenReturn("pt-123");

        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("strange error");
        expectedException.expectMessage("cas");

        new AccountService(requestFactory, url).get();
    }

    /**
     * Tests {@link AccountService#get()}.
     * @throws IOException
     */
    @Test
    public void testGet() throws IOException {
        File directory = folder.newFolder();
        
        URL testResource = Resources.getResource("com/cloudogu/smeagol/clearpass.xml");
        try (FileOutputStream fos = new FileOutputStream(new File(directory, "clearPass")) ) {
            Resources.copy(testResource, fos);
        }
        
        String url = directory.toURI().toURL().toExternalForm();
        
        when(request.getUserPrincipal()).thenReturn(principal);
        // when(configuration.getCasUrl()).thenReturn(url);
        when(principal.getProxyTicketFor(url + "clearPass")).thenReturn("pt-123");
        Map<String,Object> attributes = ImmutableMap.of(
            "username", "admin", "displayName", "Administrator", "mail", "super@admin.org"
        );
        when(principal.getAttributes()).thenReturn(attributes);


        Account account = new AccountService(requestFactory, url).get();
        assertEquals("admin", account.getUsername());
        assertEquals("admin123", new String(account.getPassword()));
        assertEquals("Administrator", account.getDisplayName());
        assertEquals("super@admin.org", account.getMail());
        
        verify(session).setAttribute(Account.class.getName(), account);
    }
    
    /**
     * Tests {@link AccountService#get()} from session cache.
     */
    @Test
    public void testGetGetFromSession(){
        Account account = new Account("hans", "schalter", "hansamschalter@light.de");
        when(session.getAttribute(Account.class.getName())).thenReturn(account);
        Account returnedAccount = new AccountService(requestFactory, "com/cloudogu/smeagol").get();
        assertSame(account, returnedAccount);
    }
    
    /**
     * Tests {@link AccountService#fetchClearPassCredentials(URL)}.
     */
    @Test
    public void testFetchClearPassCredentials() throws IOException {
        URL url = Resources.getResource("com/cloudogu/smeagol/clearpass.xml");
        assertArrayEquals("admin123".toCharArray(), AccountService.fetchClearPassCredentials(url));
    }

}