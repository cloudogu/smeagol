package com.cloudogu.wiki;

import com.cloudogu.wiki.scmm.SessionCacheScmWikiListStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The RefreshWikisServlet removes cached wiki list from the session
 *
 * @author Maren Süwer
 */
public class RefreshWikisServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RefreshWikisServlet.class);
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.getSession(true).removeAttribute(SessionCacheScmWikiListStrategy.class.getName());
        LOG.info("cached wiki list is removed");
        response.sendRedirect(request.getContextPath());
    }
}
