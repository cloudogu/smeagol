/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link Accounts}.
 *
 * @author Sebastian Sdorra
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountsTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpSession session;

    @Mock
    private WikiServerConfiguration configuration;
    
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
    public void setUpMocks(){
        when(request.getSession(true)).thenReturn(session);
    }
    
    /**
     * Tests {@link Accounts#fromRequest(WikiServerConfiguration, HttpServletRequest)} without principal.
     */
    @Test
    public void testFromRequestWithoutPrincipal(){
        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("principal");
        
        Accounts.fromRequest(configuration, request);
    }

    /**
     * Tests {@link Accounts#fromRequest(WikiServerConfiguration, HttpServletRequest)} without proxy ticket.
     */    
    @Test
    public void testFromRequestWithoutProxyTicket(){
        when(request.getUserPrincipal()).thenReturn(principal);
        when(configuration.getCasUrl()).thenReturn("com/cloudogu/wiki");
        
        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("proxy");
        expectedException.expectMessage("ticket");
        
        Accounts.fromRequest(configuration, request);
    }
    
    /**
     * Tests {@link Accounts#fromRequest(WikiServerConfiguration, HttpServletRequest)} with an error during clear pass
     * fetch.
     */
    @Test
    public void testFromRequestFailedToFetchClearPass(){
        when(request.getUserPrincipal()).thenReturn(principal);
        when(configuration.getCasUrl()).thenReturn("com/cloudogu/wiki");
        when(principal.getProxyTicketFor("com/cloudogu/wiki/clearPass")).thenReturn("pt-123");
        
        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("fetch");
        expectedException.expectMessage("clear pass");
        
        Accounts.fromRequest(configuration, request);
    }
    
    /**
     * Tests {@link Accounts#fromRequest(WikiServerConfiguration, HttpServletRequest)} without password in clear pass
     * response.
     * @throws java.io.IOException
     */
    @Test
    public void testFromRequestWithoutPassword() throws IOException {
        File directory = folder.newFolder();
        
        URL testResource = Resources.getResource("com/cloudogu/wiki/clearpass-wo.xml");
        try (FileOutputStream fos = new FileOutputStream(new File(directory, "clearPass")) ) {
            Resources.copy(testResource, fos);
        }
        
        String url = directory.toURI().toURL().toExternalForm();
        
        when(request.getUserPrincipal()).thenReturn(principal);
        when(configuration.getCasUrl()).thenReturn(url);
        when(principal.getProxyTicketFor(url + "clearPass")).thenReturn("pt-123");

        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("extract");
        expectedException.expectMessage("clear pass");
        expectedException.expectMessage("password");

        Accounts.fromRequest(configuration, request);
    }
    
    /**
     * Tests {@link Accounts#fromRequest(WikiServerConfiguration, HttpServletRequest)} with clear pass failure.
     * @throws java.io.IOException
     */
    @Test
    public void testFromRequestWithClearPassFailure() throws IOException {
        File directory = folder.newFolder();
        
        URL testResource = Resources.getResource("com/cloudogu/wiki/clearpass-failure.xml");
        try (FileOutputStream fos = new FileOutputStream(new File(directory, "clearPass")) ) {
            Resources.copy(testResource, fos);
        }
        
        String url = directory.toURI().toURL().toExternalForm();
        
        when(request.getUserPrincipal()).thenReturn(principal);
        when(configuration.getCasUrl()).thenReturn(url);
        when(principal.getProxyTicketFor(url + "clearPass")).thenReturn("pt-123");

        expectedException.expect(WikiAuthenticationException.class);
        expectedException.expectMessage("strange error");
        expectedException.expectMessage("cas");

        Accounts.fromRequest(configuration, request);
    }
    
    /**
     * Tests {@link Accounts#fromRequest(WikiServerConfiguration, HttpServletRequest)}.
     * @throws java.io.IOException
     */
    @Test
    public void testFromRequest() throws IOException {
        File directory = folder.newFolder();
        
        URL testResource = Resources.getResource("com/cloudogu/wiki/clearpass.xml");
        try (FileOutputStream fos = new FileOutputStream(new File(directory, "clearPass")) ) {
            Resources.copy(testResource, fos);
        }
        
        String url = directory.toURI().toURL().toExternalForm();
        
        when(request.getUserPrincipal()).thenReturn(principal);
        when(configuration.getCasUrl()).thenReturn(url);
        when(principal.getProxyTicketFor(url + "clearPass")).thenReturn("pt-123");
        Map<String,Object> attributes = ImmutableMap.of(
            "username", "admin", "displayName", "Administrator", "mail", "super@admin.org"
        );
        when(principal.getAttributes()).thenReturn(attributes);

        Account account = Accounts.fromRequest(configuration, request);
        assertEquals("admin", account.getUsername());
        assertEquals("admin123", new String(account.getPassword()));
        assertEquals("Administrator", account.getDisplayName());
        assertEquals("super@admin.org", account.getMail());
        
        verify(session).setAttribute(Account.class.getName(), account);
    }
    
    /**
     * Tests {@link Accounts#fromRequest(WikiServerConfiguration, HttpServletRequest)} from session cache.
     */
    @Test
    public void testFromRequestGetFromSession(){
        Account account = new Account("hans", "schalter", "hansamschalter@light.de");
        when(session.getAttribute(Account.class.getName())).thenReturn(account);
        Account returnedAccount = Accounts.fromRequest(configuration, request);
        assertSame(account, returnedAccount);
    }
    
    /**
     * Tests {@link Accounts#fetchClearPassCredentials(URL)}.
     */
    @Test
    public void testFetchClearPassCredentials() throws IOException {
        URL url = Resources.getResource("com/cloudogu/wiki/clearpass.xml");
        assertArrayEquals("admin123".toCharArray(), Accounts.fetchClearPassCredentials(url)); 
    }

}