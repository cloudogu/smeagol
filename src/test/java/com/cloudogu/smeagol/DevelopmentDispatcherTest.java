package com.cloudogu.smeagol;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DevelopmentDispatcherTest {

    private DevelopmentDispatcher dispatcher;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpURLConnection connection;

    @Before
    public void setUp() {
        dispatcher = new MockingDevelopmentDispatcher("http://hitchhiker.com");
    }

    @Test
    public void testNeedsToBeDispatched() {
        assertFalse(dispatcher.needsToBeDispatched("/api/v1/authc"));
        assertTrue(dispatcher.needsToBeDispatched("/locales/de/translations.json"));
        assertTrue(dispatcher.needsToBeDispatched("/static/bundle.js"));
        assertTrue(dispatcher.needsToBeDispatched("/favicon.ico"));
        assertTrue(dispatcher.needsToBeDispatched("/index.html"));
        assertTrue(dispatcher.needsToBeDispatched("/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/master/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/feature%20one/"));
        assertTrue(dispatcher.needsToBeDispatched("/EgQi7FoSe1/feature%20one/docs/Home"));
    }

    @Test
    public void testWithGetRequest() throws IOException {
        // configure request mock
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeaderNames()).thenReturn(toEnum("Content-Type"));
        when(request.getHeaders("Content-Type")).thenReturn(toEnum("application/json"));

        // configure proxy url connection mock
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("hitchhicker".getBytes(Charsets.UTF_8)));
        Map<String, List<String>> headerFields = new HashMap<>();
        headerFields.put("Content-Type", Lists.newArrayList("application/yaml"));
        when(connection.getHeaderFields()).thenReturn(headerFields);
        when(connection.getResponseCode()).thenReturn(200);

        // configure response mock
        DevServletOutputStream output = new DevServletOutputStream();
        when(response.getOutputStream()).thenReturn(output);

        dispatcher.dispatch(request, response, "/people/trillian");

        // verify connection
        verify(connection).setRequestMethod("GET");
        verify(connection).setRequestProperty("Content-Type", "application/json");

        // verify response
        verify(response).setStatus(200);
        verify(response).addHeader("Content-Type", "application/yaml");
        assertEquals("hitchhicker", output.stream.toString());
    }

    @Test
    public void testWithPOSTRequest() throws IOException {
        // configure request mock
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeaderNames()).thenReturn(toEnum());
        when(request.getInputStream()).thenReturn(new DevServletInputStream("hitchhiker"));
        when(request.getContentLengthLong()).thenReturn(1L);

        // configure proxy url connection mock
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        Map<String, List<String>> headerFields = new HashMap<>();
        when(connection.getHeaderFields()).thenReturn(headerFields);
        when(connection.getResponseCode()).thenReturn(204);

        // configure response mock
        when(response.getOutputStream()).thenReturn(new DevServletOutputStream());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(connection.getOutputStream()).thenReturn(output);

        dispatcher.dispatch(request, response, "/people/trillian");

        // verify connection
        verify(connection).setRequestMethod("POST");
        assertEquals("hitchhiker", output.toString());

        // verify response
        verify(response).setStatus(204);
    }

    private Enumeration<String> toEnum(String... values) {
        Set<String> set = ImmutableSet.copyOf(values);
        return toEnum(set);
    }

    private <T> Enumeration<T> toEnum(Collection<T> collection) {
        return new Vector<>(collection).elements();
    }

    private class DevServletInputStream extends ServletInputStream {

        private InputStream inputStream;

        private DevServletInputStream(String content) {
            inputStream = new ByteArrayInputStream(content.getBytes(Charsets.UTF_8));
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }

    private class DevServletOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream stream = new ByteArrayOutputStream();

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int b) throws IOException {
            stream.write(b);
        }
    }

    private class MockingDevelopmentDispatcher extends DevelopmentDispatcher {

        private MockingDevelopmentDispatcher(String target) {
            super(target);
        }

        @Override
        protected HttpURLConnection openConnection(URL url) {
            return connection;
        }
    }

}