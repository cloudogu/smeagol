package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


/**
 * Extracts the wildcard part as {@link Path} from the request.
 */
@Component
public class WildcardPathExtractor {

    /**
     * Extracts the wildcard part of the request as {@link Path}, by using the servlet path to get the url of the
     * endpoint.
     *
     * @param request http servlet request
     * @param pathMapping mapping of the rest endpoint with placeholders e.g.: /v1/{repositoryId}/{branch}/mypath
     * @param wikiId id of wiki
     *
     * @return wildcard part of the request as {@link Path}
     */
    public Path extractPathFromRequest(HttpServletRequest request, String pathMapping, WikiId wikiId) {
        // we need to extract the path from request, because there is no matcher which allows slashes in spring
        // https://stackoverflow.com/questions/4542489/match-the-rest-of-the-url-using-spring-3-requestmapping-annotation
        // and we must mock the path extractor in our tests, because request.getServletPath is empty in the tests.
        String base = pathMapping.replace("{repositoryId}", wikiId.getRepositoryID())
                                 .replace("{branch}", wikiId.getBranch());

        return Path.valueOf(extract(request, base));
    }

    private String extract(HttpServletRequest request, String base) {
        String servletPath = request.getServletPath();

        String path = servletPath.substring(base.length());
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }
}
