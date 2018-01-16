package com.cloudogu.wiki;

import com.cloudogu.wiki.scmm.SessionCacheScmWikiListStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Maren Süwer
 */
public class RefreshWikisServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RefreshWikisServlet.class);

    public RefreshWikisServlet() {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        request.getSession(true).removeAttribute(SessionCacheScmWikiListStrategy.class.getName());
        LOG.info("wikis are reloaded");
        try {
            response.sendRedirect(request.getContextPath());
        } catch (IOException ex) {
            LOG.warn("Failed to send redirect.", ex);
        }
    }
}
