/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 *
 * Copyright notice
 */
package com.cloudogu.wiki;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.View;
import java.io.IOException;

/**
 * The dispatcher servlet takes the current requested name of the wiki and
 * forwards the request to the {@link Servlet}, which was created with
 * {@link WikiProvider}. If the root application was requested an overview of
 * all available wikis is created from the {@link WikiProvider}.
 *
 * @author Sebastian Sdorra
 */
public class WikiDispatcherServlet extends HttpServlet {

    private static final String SEPARATOR = "/";

    private static final Logger LOG = LoggerFactory.getLogger(WikiDispatcherServlet.class);

    private final MustacheFactory factory = new DefaultMustacheFactory();

    private static final long serialVersionUID = 7511937785395456331L;

    private final WikiProvider provider;
    private final WikiServerConfiguration configuration;

    public WikiDispatcherServlet(WikiProvider provider, WikiServerConfiguration configuration) {
        this.provider = provider;
        this.configuration = configuration;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String repositoryId = getRepositoryId(req);
        String branchName = getBranchName(req);


        if (Strings.isNullOrEmpty(repositoryId)) {
            try {
                renderOverview(req, resp);
            }
            catch(RuntimeException ex){
                renderNoConnectionToSCM(req, resp);
            }
        } else if (Strings.isNullOrEmpty(branchName)) {
            renderBranchOverview(req, resp);
        } else {
            String wikiName = repositoryId;
            if(!Strings.isNullOrEmpty(branchName)){
                wikiName += SEPARATOR + branchName;
            }
            try {
                Servlet servlet = provider.getServlet(wikiName);
                if (servlet != null) {
                    servlet.service(wrap(req, wikiName), resp);
                } else {
                    renderNotFound(req, resp);
                }
            } catch (WikiNotFoundException ex) {
                LOG.trace("could not find wiki", ex);
                renderNotFound(req, resp);
            }
        }
    }

    private void renderNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(404);
        renderTemplate(response, "notfound", new NotFound(request));
    }

    private void renderNoConnectionToSCM(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(500);
        renderTemplate(response, "noSCMConnection", new NotFound(request));
    }

    private void renderOverview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        renderTemplate(response, "overviewRepos", new Overview(request, provider.getAll(), getCasLogoutUrl(configuration)));
    }

    private void renderBranchOverview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        renderTemplate(response, "overviewBranches", new Overview(request, provider.getAllBranches(getRepositoryId(request)), getCasLogoutUrl(configuration)));
    }

    private void renderTemplate(HttpServletResponse response, String templateName, ViewModel viewModel) throws IOException {
        String tpl = createTemplatePath(templateName, viewModel);
        response.setContentType("text/html");
        Mustache mustache = factory.compile(WikiResources.path(tpl));
        mustache.execute(response.getWriter(), viewModel);
    }

    private String createTemplatePath(String templateName, ViewModel viewModel) {
        return templateName + "_" + viewModel.getLocale() + ".html";
    }

    private HttpServletRequest wrap(HttpServletRequest request, String path) {
        return new DispatchHttpServletRequestWrapper(request, path);
    }

    @VisibleForTesting
    String getRepositoryId(HttpServletRequest request) {
        String path = "";
        String requestURI = request.getRequestURI();

        if (Strings.isNullOrEmpty(requestURI)) {
            return path;
        }

        requestURI = requestURI.replaceAll("/+$", "");
        String contextPath = request.getContextPath();

        if (requestURI.startsWith(contextPath)) {
            path = requestURI.substring(contextPath.length()).replaceAll("^/+", "");
        }

        int indexOfMatrixParameter = path.indexOf(";");
        if(indexOfMatrixParameter != -1){
             if(indexOfMatrixParameter > 1){
                path = path.substring(0,indexOfMatrixParameter-1);
            } else {
                return "";
            }
        }

        int indexOfSeparator = path.indexOf(SEPARATOR);
        if(indexOfSeparator == -1){
            return path;
        } else {
            return path.substring(0, indexOfSeparator);
        }

    }

    @VisibleForTesting
    String getBranchName(HttpServletRequest request) {
        String path = "";
        String requestURI = request.getRequestURI();

        if (Strings.isNullOrEmpty(requestURI)) {
            return path;
        }

        requestURI = requestURI.replaceAll("/+$", "");
        String contextPath = request.getContextPath();

        if (requestURI.startsWith(contextPath)) {
            path = requestURI.substring(contextPath.length()).replaceAll("^/+", "");
        }

        int indexOfMatrixParameter = path.indexOf(";");
        if(indexOfMatrixParameter != -1){
            if(indexOfMatrixParameter > 1){
                path = path.substring(0,indexOfMatrixParameter-1);
            } else {
                return "";
            }
        }

        int indexOfSeparator = path.indexOf(SEPARATOR);
        if(indexOfSeparator == -1){
            return "";
        } else {
            path = path.substring(indexOfSeparator + 1);
        }

        indexOfSeparator = path.indexOf(SEPARATOR);
        if(indexOfSeparator != -1){
            path = path.substring(0, indexOfSeparator);
        }
        return path;
    }

    private static class DispatchHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private String path;

        public DispatchHttpServletRequestWrapper(HttpServletRequest request, String path) {
            super(request);
            this.path = path;
        }

        @Override
        public String getServletPath() {
            String contextPath = super.getContextPath();
            String servletPath = super.getServletPath();
            return contextPath + servletPath;// + SEPARATOR + path;
        }

    }

    private static abstract class ViewModel {

        private final HttpServletRequest request;

        protected ViewModel(HttpServletRequest request) {
            this.request = request;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public String getLocale() {
            return LocaleChoosingStrategy.getLocale(request.getLocales()).getLanguage();
        }

    }

    private static class NotFound extends ViewModel {

        public NotFound(HttpServletRequest request) {
            super(request);
        }

    }

    private static class Overview extends ViewModel {

        private final Iterable<Wiki> wikis;
        private final String casLogoutUrl;

        public Overview(HttpServletRequest request, Iterable<Wiki> wikis, String casUrl) {
            super(request);
            this.wikis = wikis;
            this.casLogoutUrl = casUrl;
        }

        public Iterable<Wiki> getWikis() {
            return wikis;
        }

        public String getCasLogoutUrl() {
            return casLogoutUrl;
        }
    }

    private String getCasLogoutUrl(WikiServerConfiguration config){
        String url = config.getCasUrl();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "logout";
        return url;
    }

}
