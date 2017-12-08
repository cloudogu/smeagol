package com.cloudogu.wiki;

import com.mashape.unirest.http.Unirest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WikiServerTest {

    @Mock
    private WikiServerConfiguration serverConfiguration;

    @Test
    public void ensureCookiesAreDisabled() throws Exception {
        WikiServer.configureRestClient(serverConfiguration);

        Server server = new Server(0);
        server.setHandler(new SimpleHandler() {

            @Override
            protected void handle(HttpServletRequest request, HttpServletResponse response) {
                if (request.getCookies() == null) {
                    response.addCookie(new Cookie("test", "value"));
                }
            }
        });

        server.start();
        try {
            String uri = server.getURI().toString();

            List<String> cookies = Unirest.get(uri).asString().getHeaders().get("Set-Cookie");
            assertEquals(1, cookies.size());
            cookies = Unirest.get(uri).asString().getHeaders().get("Set-Cookie");
            assertEquals(1, cookies.size());

        } finally {
            server.stop();
        }
    }

    private static abstract class SimpleHandler extends AbstractHandler {

        @Override
        public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            handle(httpServletRequest, httpServletResponse);
        }

        protected abstract void handle(HttpServletRequest request, HttpServletResponse response);
    }

}