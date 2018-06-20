package com.cloudogu.smeagol;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    private Dispatcher dispatcher;

    @Mock
    private RequestDispatcher servletDispatcher;

    @Mock
    private FilterChain chain;

    private UiFilter filter;

    @Before
    public void createObjectUnderTest() {
        filter = new UiFilter(dispatcher);
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
    public void testFilterWithFavicon() throws ServletException, IOException {
        assertUriIsNotForwarded("/smeagol/favion.png");
    }

    @Test
    public void testFilterWithServiceWorker() throws ServletException, IOException {
        assertUriIsNotForwarded("/smeagol/service-worker.js");
    }

    @Test
    public void testFilterWithStaticImage() throws ServletException, IOException {
        assertUriIsNotForwarded("/smeagol/static/image.svg");
    }

    private void assertUriIsNotForwarded(String uri) throws ServletException, IOException {
        when(request.getContextPath()).thenReturn("/smeagol");
        when(request.getRequestURI()).thenReturn(uri);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void testFilterWithStaticMediaImage() throws ServletException, IOException {
        assertUriIsNotForwarded("/smeagol/static/media/glyphicons-halflings-regular.89889688.svg");
    }

    @Test
    public void testFilterWithStaticWikiImageRepositoryStaticBranchMedia() throws ServletException, IOException {
        String expected = "/api/v1/repositories/static/branches/media/static/images/myimage.png";

        when(request.getContextPath()).thenReturn("/smeagol");
        when(request.getRequestURI()).thenReturn("/smeagol/static/media/images/myimage.png");
        when(request.getRequestDispatcher(expected)).thenReturn(servletDispatcher);

        filter.doFilter(request, response, chain);

        verify(servletDispatcher).forward(request, response);
    }

    @Test
    public void testFilterWithStaticWikiImage() throws ServletException, IOException {
        String expected = "/api/v1/repositories/EgQi7FoSe1/branches/feature%20one/static/docs/assets/architecture.svg";

        when(request.getContextPath()).thenReturn("/smeagol");
        when(request.getRequestURI()).thenReturn("/smeagol/EgQi7FoSe1/feature%20one/docs/assets/architecture.svg");
        when(request.getRequestDispatcher(expected)).thenReturn(servletDispatcher);

        filter.doFilter(request, response, chain);

        verify(servletDispatcher).forward(request, response);
    }

    @Test
    public void testFilterWhichNeededDispatching() throws ServletException, IOException {
        when(request.getContextPath()).thenReturn("/smeagol");
        when(request.getRequestURI()).thenReturn("/smeagol/api/v1/authc");
        when(dispatcher.needsToBeDispatched("/api/v1/authc")).thenReturn(true);

        filter.doFilter(request, response, chain);

        verify(dispatcher).dispatch(request, response, "/api/v1/authc");
    }

    @Test
    public void testFilter() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/authc");
        when(dispatcher.needsToBeDispatched("/api/v1/authc")).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

}