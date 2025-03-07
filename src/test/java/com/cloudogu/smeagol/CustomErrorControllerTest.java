package com.cloudogu.smeagol;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(MockitoJUnitRunner.class)
public class CustomErrorControllerTest {

    @Mock
    private CustomErrorController.HttpClientHelper httpClientHelper;

    @Spy
    private CustomErrorController customErrorController;

    @Mock
    HttpServletRequest requestMock;

    private static final String mockedContextPath = "sample/contextPath/smeagol";

    @Before
    public void init() {
        openMocks(this);
        customErrorController = new CustomErrorController("http://errors.example.com/", httpClientHelper);
    }

    @Test
    public void testErrorPageHandling_fetchSuccess_BAD_REQUEST() throws IOException, URISyntaxException {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(this.httpClientHelper.createConnection(anyString())).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream("Mocked Response".getBytes()));

        doReturn(HttpStatus.BAD_REQUEST.value()).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Object errorMessage = "SOMETHING THAT PRODUCED A 400 ERROR";
        doReturn(errorMessage).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String responseTemplate = customErrorController.handleError(requestMock);

        Assert.assertThat(responseTemplate, CoreMatchers.containsString("Mocked Response"));
    }

    @Test
    public void testErrorPageHandling_fetchFail_BAD_REQUEST() throws IOException, URISyntaxException {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(this.httpClientHelper.createConnection(anyString())).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(404);

        HttpStatus expectedError = HttpStatus.BAD_REQUEST;

        doReturn(HttpStatus.BAD_REQUEST.value()).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Object errorMessage = "SOMETHING THAT PRODUCED A 400 ERROR";
        doReturn(errorMessage).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String responseTemplate = customErrorController.handleError(requestMock);

        Assert.assertThat(responseTemplate, CoreMatchers.containsString(Integer.toString(expectedError.value())));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(expectedError.getReasonPhrase()));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(errorMessage.toString()));
    }

    @Test
    public void testErrorPageHandling_fetchSuccess_INTERNAL_SERVER_ERROR() throws IOException, URISyntaxException {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(this.httpClientHelper.createConnection(anyString())).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream("Mocked Response".getBytes()));

        HttpStatus expectedError = HttpStatus.INTERNAL_SERVER_ERROR;

        doReturn(HttpStatus.INTERNAL_SERVER_ERROR.value()).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Object errorMessage = "SOMETHING THAT PRODUCED A 500 ERROR";
        doReturn(errorMessage).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String responseTemplate = customErrorController.handleError(requestMock);

        Assert.assertThat(responseTemplate, CoreMatchers.containsString("Mocked Response"));
    }

    @Test
    public void testErrorPageHandling_fetchFail_INTERNAL_SERVER_ERROR() throws IOException, URISyntaxException {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(this.httpClientHelper.createConnection(anyString())).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(404);

        HttpStatus expectedError = HttpStatus.INTERNAL_SERVER_ERROR;

        doReturn(HttpStatus.INTERNAL_SERVER_ERROR.value()).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Object errorMessage = "SOMETHING THAT PRODUCED A 500 ERROR";
        doReturn(errorMessage).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String responseTemplate = customErrorController.handleError(requestMock);

        Assert.assertThat(responseTemplate, CoreMatchers.containsString(Integer.toString(expectedError.value())));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(expectedError.getReasonPhrase()));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(errorMessage.toString()));
    }

    @Test

    public void testErrorPageHandling_Status_Undefined() {

        doReturn(null).when(requestMock)
            .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String responseTemplate = customErrorController.handleError(requestMock);

        Assert.assertEquals("OK", "", responseTemplate);

    }

}
