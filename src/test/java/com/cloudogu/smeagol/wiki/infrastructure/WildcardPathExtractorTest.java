package com.cloudogu.smeagol.wiki.infrastructure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WildcardPathExtractorTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WildcardPathExtractor pathExtractor;

    @Test
    public void testExtractPath() {
        when(request.getServletPath()).thenReturn("/api/v1/endpoint");
        assertEquals("endpoint", pathExtractor.extract(request, "/api/v1"));
        assertEquals("endpoint", pathExtractor.extract(request, "/api/v1/"));
    }

}