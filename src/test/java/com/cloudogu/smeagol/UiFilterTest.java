package com.cloudogu.smeagol;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UiFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private FilterChain chain;

    private final UiFilter filter = new UiFilter();

    @Before
    public void setUp() {
        when(request.getContextPath()).thenReturn("/smeagol");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test(expected = ServletException.class)
    public void testFilterWithNonHttpRequest() throws IOException, ServletException {
        ServletRequest servletRequest = mock(ServletRequest.class);
        filter.doFilter(servletRequest, response, chain);
    }

    @Test(expected = ServletException.class)
    public void testFilterWithNonHttpResponse() throws IOException, ServletException {
        ServletResponse servletResponse = mock(ServletResponse.class);
        filter.doFilter(request, servletResponse, chain);
    }

    @Test
    public void testFilterWithApiRequest() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/smeagol/api/v1/authc");

        filter.doFilter(request, response, chain);
        verifyFilter();
    }

    private void verifyFilter() throws IOException, ServletException {
        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    public void testFilterWithLocalesRequest() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/smeagol/locales/de/translations.json");

        filter.doFilter(request, response, chain);
        verifyFilter();
    }

    @Test
    public void testFilterWithStaticRequest() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/smeagol/static/bundle.js");

        filter.doFilter(request, response, chain);
        verifyFilter();
    }

    @Test
    public void testFilterWithRootFile() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/smeagol/favicon.ico");

        filter.doFilter(request, response, chain);
        verifyFilter();
    }

    @Test
    public void testFilterWithIndexHtml() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/smeagol/index.html");

        filter.doFilter(request, response, chain);
        verifyFilter();
    }

    @Test
    public void testFilterOnRoot() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/smeagol/");

        filter.doFilter(request, response, chain);
        verifyFilter();
    }

    @Test
    public void testFileWithUiPageRequest() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/smeagol/EgQi7FoSe1/master/docs/Home");

        filter.doFilter(request, response, chain);
        verifyForwarded();
    }

    @Test
    public void testFilterWithUiRepositoryRequest() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/smeagol/EgQi7FoSe1");

        filter.doFilter(request, response, chain);
        verifyForwarded();
    }

    @Test
    public void testFilterWithUiBranchRequest() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/smeagol/EgQi7FoSe1/master/");

        filter.doFilter(request, response, chain);
        verifyForwarded();
    }

    private void verifyForwarded() throws IOException, ServletException {
        verify(chain, never()).doFilter(request, response);
        verify(request).getRequestDispatcher("/index.html");
        verify(dispatcher).forward(request, response);
    }


}