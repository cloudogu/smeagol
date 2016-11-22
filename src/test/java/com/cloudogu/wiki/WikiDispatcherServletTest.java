/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link WikiDispatcherServlet}.
 * 
 * @author Sebastian Sdorra
 */
@RunWith(MockitoJUnitRunner.class)
public class WikiDispatcherServletTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private WikiProvider provider;

    @InjectMocks
    private WikiDispatcherServlet servlet;

    /**
     * Tests {@link WikiDispatcherServlet#getRepositoryId(HttpServletRequest) }.
     */
    @Test
    public void testGetRepositoryId() {
        when(request.getPathInfo()).thenReturn("/test/sorbot");
        assertEquals("test", servlet.getRepositoryId(request));
        when(request.getPathInfo()).thenReturn("/test");
        assertEquals("test", servlet.getRepositoryId(request));
        when(request.getPathInfo()).thenReturn("/test/sorbot/123");
        assertEquals("test", servlet.getRepositoryId(request));
        when(request.getPathInfo()).thenReturn("test/sorbot");
        assertEquals("test", servlet.getRepositoryId(request));
    }

    /**
     * Tests {@link WikiDispatcherServlet#getBranchName(HttpServletRequest) }.
     */
    @Test
    public void testGetBranchName() {
        when(request.getPathInfo()).thenReturn("/test/sorbot");
        assertEquals("sorbot", servlet.getBranchName(request));
        when(request.getPathInfo()).thenReturn("/test");
        assertEquals("", servlet.getBranchName(request));
        when(request.getPathInfo()).thenReturn("/test/sorbot/123");
        assertEquals("sorbot", servlet.getBranchName(request));
        when(request.getPathInfo()).thenReturn("test/sorbot");
        assertEquals("sorbot", servlet.getBranchName(request));
    }

    /**
     * Test {@link WikiDispatcherServlet#service(HttpServletRequest, HttpServletResponse)} with overview requested.
     * 
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Test
    public void testServiceOnOverview() throws ServletException, IOException {
        StringWriter buffer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(buffer));
        when(provider.getAll()).thenReturn(Lists.newArrayList(new Wiki("xyz",
                "zxy", "XYZ Wiki", "Fresch XYZ WIKI")));
        
        servlet.service(request, response);
        
        verify(response).setContentType("text/html");
        assertThat(buffer.toString(), allOf(
            containsString("<html"),
            containsString("</html>"),
            containsString("xyz"),
            containsString("XYZ Wiki")//,
            // containsString("Fresch XYZ WIKI")
        ));
    }
    
    /**
     * Test {@link WikiDispatcherServlet#service(HttpServletRequest, HttpServletResponse)} with non existing wiki 
     * requested.
     * 
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Test
    public void testServiceOnNonExistingWiki() throws ServletException, IOException {
        StringWriter buffer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(buffer));
        
        when(request.getPathInfo()).thenReturn("/test/");
        
        servlet.service(request, response);
        assertNotFoundPage(buffer);
    }
    
    /**
     * Test {@link WikiDispatcherServlet#service(HttpServletRequest, HttpServletResponse)} with servlet that throws
     * a {@link WikiNotFoundException}.
     * 
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Test
    public void testServiceWithWikiNotFoundException() throws ServletException, IOException {
        StringWriter buffer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(buffer));
        when(request.getPathInfo()).thenReturn("/test/");
        
        HttpServlet wikiServlet = mock(HttpServlet.class);
        when(provider.getServlet("test")).thenReturn(wikiServlet);
        doThrow(WikiNotFoundException.class).when(wikiServlet).service(
            Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class)
        );
        
        servlet.service(request, response);
        assertNotFoundPage(buffer);
    }
    
    private void assertNotFoundPage(StringWriter buffer){
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        
        verify(response).setContentType("text/html");
        assertThat(buffer.toString(), allOf(
            containsString("<html"),
            containsString("</html>"),
            containsString("Not Found")
        ));
    }
    
    /**
     * Test {@link WikiDispatcherServlet#service(HttpServletRequest, HttpServletResponse)} with non existing wiki 
     * requested.
     * 
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Test
    public void testServiceOnWiki() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/test/");
        when(request.getContextPath()).thenReturn("/wiki");
        when(request.getServletPath()).thenReturn("/test");
        
        HttpServlet wikiServlet = mock(HttpServlet.class);
        when(provider.getServlet("test")).thenReturn(wikiServlet);
        
        servlet.service(request, response);
        ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
        ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
        
        verify(wikiServlet).service(requestCaptor.capture(), responseCaptor.capture());
        
        assertSame(response, responseCaptor.getValue());
        HttpServletRequest capturedRequest = requestCaptor.getValue();
        assertEquals(
            WikiDispatcherServlet.class.getName() + "$DispatchHttpServletRequestWrapper", 
            capturedRequest.getClass().getName()
        );
        
        // test wrapped getServletPath method
        assertEquals("/wiki/test", capturedRequest.getServletPath());
    }
}