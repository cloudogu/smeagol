package com.cloudogu.smeagol;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * The development dispatcher proxies every non api request to configured {@code ui.url}.
 */
public class DevelopmentDispatcher implements Dispatcher {

    private String target;

    @Autowired
    public DevelopmentDispatcher(@Value("${ui.url}") String target) {
        this.target = target;
    }

    @Override
    public boolean needsToBeDispatched(String uri) {
        return !(uri.startsWith("/api") || uri.startsWith("api"));
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException {
        URL url = createProxyUrl(uri);

        HttpURLConnection connection = openConnection(url);
        connection.setRequestMethod(request.getMethod());
        copyRequestHeaders(request, connection);

        if (request.getContentLengthLong() > 0) {
            copyRequestBody(request, connection);
        }

        int responseCode = connection.getResponseCode();
        response.setStatus(responseCode);
        copyResponseHeaders(response, connection);
        copyResponseBody(response, connection);
    }

    private void copyResponseBody(HttpServletResponse response, HttpURLConnection connection) throws IOException {
        try (InputStream input = connection.getInputStream(); OutputStream output = response.getOutputStream()) {
            ByteStreams.copy(input, output);
        }
    }

    private void copyResponseHeaders(HttpServletResponse response, HttpURLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        for (Map.Entry<String,List<String>> entry : headerFields.entrySet()) {
            if (entry.getKey() != null && ! entry.getKey().equalsIgnoreCase("Transfer-Encoding")) {
                for (String value : entry.getValue()) {
                    response.addHeader(entry.getKey(), value);
                }
            }
        }
    }

    private void copyRequestBody(HttpServletRequest request, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        try (InputStream input = request.getInputStream(); OutputStream output = connection.getOutputStream()) {
            ByteStreams.copy(input, output);
        }
    }

    private void copyRequestHeaders(HttpServletRequest request, HttpURLConnection connection) {
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            Enumeration<String> values = request.getHeaders(header);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                connection.setRequestProperty(header, value);
            }
        }
    }

    private URL createProxyUrl(String uri) throws MalformedURLException {
        return new URL(target + uri);
    }

    @VisibleForTesting
    protected HttpURLConnection openConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}
