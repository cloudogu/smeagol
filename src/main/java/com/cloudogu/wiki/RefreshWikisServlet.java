package com.cloudogu.wiki;

import com.cloudogu.wiki.scmm.SessionCacheScmWikiListStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by masuewer on 15.01.18.
 */

public class RefreshWikisServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RefreshWikisServlet.class);
    private final String refreshUrl;

    public RefreshWikisServlet() {
        refreshUrl = "/smeagol";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        request.getSession(true).removeAttribute(SessionCacheScmWikiListStrategy.class.getName());
        LOG.info("wikis are reloaded");
        request.getSession().invalidate();
        try {
            response.sendRedirect(refreshUrl);
        } catch (IOException ex) {
            LOG.warn("Failed to send redirect.", ex);
        }
    }
}
