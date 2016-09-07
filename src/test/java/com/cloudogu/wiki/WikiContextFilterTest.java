/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link WikiContextFilter}.
 * 
 * @author Sebastian Sdorra
 */
@RunWith(MockitoJUnitRunner.class)
public class WikiContextFilterTest {

    @Mock
    private WikiServerConfiguration configuration;
    
    @Mock
    private WikiProvider provider;

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain chain;
    
    @InjectMocks
    private WikiContextFilter filter; 
    
    /**
     * Tests {@link WikiContextFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}.
     * 
     * @throws IOException
     * @throws ServletException 
     */
    @Test
    public void testDoFilter() throws IOException, ServletException {
        doAnswer((InvocationOnMock iom) -> {
            WikiContext context = WikiContextFactory.getInstance().get();
            
            assertNotNull(context);
            assertSame(request, context.getRequest());
            assertSame(response, context.getResponse());
            assertSame(provider, context.getProvider());
            
            return null;
        }).when(chain).doFilter(request, response);
        
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

}