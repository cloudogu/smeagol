/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 *
 * Copyright notice
 */
package com.cloudogu.wiki;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link WikiServletFactory}.
 * 
 * @author Sebastian Sdorra
 */
@RunWith(MockitoJUnitRunner.class)
public class WikiServletFactoryTest {

    private final WikiServerConfiguration configuration = new WikiServerConfiguration();
    
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletInputStream inputStream;
    
    @Mock
    private Enumeration<String> headerNames;
    
    @Mock
    private Enumeration<String> attributeNames;
    
    private CapturingServletOutputStream outputStream;
    
    @Mock
    private WikiContext context;

    /**
     * Prepare mocks for testing.
     * 
     * @throws IOException 
     */
    @Before
    public void setUpMocks() throws IOException {
        // setup request
        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/test");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getScheme()).thenReturn("http");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getHeaderNames()).thenReturn(headerNames);
        when(request.getAttributeNames()).thenReturn(attributeNames);
        
        // configure output
        outputStream = new CapturingServletOutputStream();
        when(response.getOutputStream()).thenReturn(outputStream);
        
        // configure context
        when(context.getRequest()).thenReturn(request);
        WikiContextFactory.getInstance().begin(context);
    }

    /**
     * Tests {@link WikiServletFactory#create(com.cloudogu.wiki.Wiki, com.cloudogu.wiki.WikiOptions)}.
     * 
     * @throws ServletException
     * @throws IOException 
     */
    @Test
    public void testCreateServlet() throws ServletException, IOException {
        WikiServletFactory servletFactory = new WikiServletFactory(configuration, "test-runner.rb");

        Wiki wiki = new Wiki("repo123", "branch321", "Test", "Test Wiki");
        WikiOptions options = WikiOptions.builder("test").build();
        HttpServlet servlet = servletFactory.create(wiki, options);
        servlet.service(request, response);
                
        assertEquals("Hello, repo123/branch321", outputStream.buffer.toString
                ());
    }

    /**
     * Clean up after tests.
     */
    @After
    public void cleanUp() {
        when(context.getRequest()).thenReturn(request);
        WikiContextFactory.getInstance().end();
    }

    private static class CapturingServletOutputStream extends ServletOutputStream {
        
        private final ByteArrayOutputStream buffer;

        public CapturingServletOutputStream() {
            this.buffer = new ByteArrayOutputStream();
        }
        
        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            
        }

        @Override
        public void write(int b) throws IOException {
            buffer.write(b);
        }
        
    }
}
