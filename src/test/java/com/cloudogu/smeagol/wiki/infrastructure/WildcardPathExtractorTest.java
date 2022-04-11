package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.DomainTestData;
import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WildcardPathExtractorTest {

    @Mock
    private HttpServletRequest request;
    private final WildcardPathExtractor pathExtractor = new WildcardPathExtractor();

    @Test
    public void testExtractPathFromRequest() {
        when(request.getServletPath()).thenReturn("/v1/42/galaxy/my/path");

        WikiId id = DomainTestData.WIKI_ID_42;

        String mapping = "/v1/{repositoryId}/{branch}";
        assertEquals(Path.valueOf("my/path"), pathExtractor.extractPathFromRequest(request, mapping, id));

        mapping = "/v1/{repositoryId}/{branch}/";
        assertEquals(Path.valueOf("my/path"), pathExtractor.extractPathFromRequest(request, mapping, id));
    }

}
