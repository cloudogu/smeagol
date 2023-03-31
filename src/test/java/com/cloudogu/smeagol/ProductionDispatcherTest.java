package com.cloudogu.smeagol;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductionDispatcherTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher servletDispatcher;

    private final ProductionDispatcher dispatcher = new ProductionDispatcher();

    @Test
    public void testNeedsToBeDispatched() {
        assertFalse(dispatcher.needsToBeDispatched("/api/v1/authc"));
        assertFalse(dispatcher.needsToBeDispatched("/api/v1/logout"));
        assertFalse(dispatcher.needsToBeDispatched("/locales/de/translations.json"));
        assertFalse(dispatcher.needsToBeDispatched("/static/bundle.js"));
        assertFalse(dispatcher.needsToBeDispatched("/favicon.ico"));
        assertFalse(dispatcher.needsToBeDispatched("/index.html"));
        assertFalse(dispatcher.needsToBeDispatched("/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/master/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/feature%20one/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/feature%20one/docs/Home"));
    }

    @Test
    public void testDispatch() throws ServletException, IOException {
        when(request.getRequestDispatcher("/index.html")).thenReturn(servletDispatcher);
        dispatcher.dispatch(request, response, "/EgQi7FoSe1/master/");
        verify(servletDispatcher).forward(request, response);
    }
}
