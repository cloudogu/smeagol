package com.cloudogu.smeagol.authc.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

public class CasFilterWithRedirectTest {

    @Test
    public void testOnFailedValidation() throws IOException {
        CasFilterWithRedirect filter = new CasFilterWithRedirect();

        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(mockReq.getRequestURL()).thenReturn(new StringBuffer("https://invalidurl/validation/"));
        Mockito.when(mockReq.getQueryString()).thenReturn("hallo=welt&ticket=12345&lorem=ipsum");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        try {
            filter.onFailedValidation(mockReq, mockResp);
            Assert.fail("Should have thrown exception");
        } catch (TicketEmptyException e) {
            Mockito.verify(mockResp).sendRedirect(captor.capture());
            Assert.assertEquals(captor.getValue(), "https://invalidurl/validation/?hallo=welt&lorem=ipsum");
        }

        Mockito.doThrow((new IOException())).when(mockResp).sendRedirect(Mockito.any());
        try {
            filter.onFailedValidation(mockReq, mockResp);
            Assert.fail("Should have thrown exception");
        } catch (RuntimeException e) {
            // this exception should be normal
        }


    }


}
