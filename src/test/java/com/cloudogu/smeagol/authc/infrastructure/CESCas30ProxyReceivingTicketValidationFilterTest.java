package com.cloudogu.smeagol.authc.infrastructure;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apereo.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;

public class CESCas30ProxyReceivingTicketValidationFilterTest {

    @Test
    public void testInit() {
        CESCas30ProxyReceivingTicketValidationFilter filter = new CESCas30ProxyReceivingTicketValidationFilter();

        FilterConfig mockFilterConfig = Mockito.mock(FilterConfig.class);
        Cas30ProxyReceivingTicketValidationFilter mockValidationFilter = Mockito.mock(Cas30ProxyReceivingTicketValidationFilter.class);

        try {
            Field field = CESCas30ProxyReceivingTicketValidationFilter.class.getDeclaredField("original");
            field.setAccessible(true);
            field.set(filter, mockValidationFilter);

            ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);

            filter.init(mockFilterConfig);

            Mockito.verify(mockValidationFilter).setExceptionOnValidationFailure(captor.capture());

            Assert.assertFalse(captor.getValue());
        } catch (ServletException | NoSuchFieldException | IllegalAccessException e) {
            Assert.fail("Unexcpected ServletException: " + e.getMessage());
        }
    }

    @Test
    public void testDoFilter() {
        CESCas30ProxyReceivingTicketValidationFilter filter = new CESCas30ProxyReceivingTicketValidationFilter();

        Cas30ProxyReceivingTicketValidationFilter mockValidationFilter = Mockito.mock(Cas30ProxyReceivingTicketValidationFilter.class);

        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);

        try {
            Field field = CESCas30ProxyReceivingTicketValidationFilter.class.getDeclaredField("original");
            field.setAccessible(true);
            field.set(filter, mockValidationFilter);
            filter.doFilter(mockReq, mockResp, mockFilterChain);

            // Test if parameters are passed to the super class
            Mockito.verify(mockValidationFilter).doFilter(mockReq, mockResp, mockFilterChain);

            Mockito.doThrow(new TicketEmptyException()).when(mockValidationFilter).doFilter(mockReq, mockResp, mockFilterChain);

            try {
                filter.doFilter(mockReq, mockResp, mockFilterChain);
            } catch (TicketEmptyException e) {
                Assert.fail("This exception should be swallowed by CESCas30ProxyReceivingTicketValidationFilter: " + e.getMessage());
            }
        } catch (ServletException | NoSuchFieldException | IllegalAccessException | IOException e) {
            Assert.fail("Unexcpected ServletException: " + e.getMessage());
        }
    }

    @Test
    public void testDoDestroy() {
        CESCas30ProxyReceivingTicketValidationFilter filter = new CESCas30ProxyReceivingTicketValidationFilter();

        Cas30ProxyReceivingTicketValidationFilter mockValidationFilter = Mockito.mock(Cas30ProxyReceivingTicketValidationFilter.class);

        try {
            Field field = CESCas30ProxyReceivingTicketValidationFilter.class.getDeclaredField("original");
            field.setAccessible(true);
            field.set(filter, mockValidationFilter);

            filter.destroy();

            Mockito.verify(mockValidationFilter).destroy();

        } catch (NoSuchFieldException | IllegalAccessException e) {
            Assert.fail("Unexcpected ServletException: " + e.getMessage());
        }
    }

}
