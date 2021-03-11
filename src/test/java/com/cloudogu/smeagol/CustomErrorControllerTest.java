package com.cloudogu.smeagol;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class CustomErrorControllerTest {

    @Spy
    private CustomErrorController customErrorController;

    @Mock
    HttpServletRequest requestMock;

    private static final String mockedContextPath = "sample/contextPath/smeagol";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testErrorPageHandling_BAD_REQUEST() {
        HttpStatus expectedError = HttpStatus.BAD_REQUEST;

        doReturn(HttpStatus.BAD_REQUEST.value()).when(requestMock)
                .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Object errorMessage = "SOMETHING THAT PRODUCED A 400 ERROR";
        doReturn(errorMessage).when(requestMock)
                .getAttribute(RequestDispatcher.ERROR_MESSAGE);

        doReturn(mockedContextPath).when(requestMock)
                .getContextPath();

        String responseTemplate = customErrorController.handleError(requestMock);

        //check for HTTP status code (400), reasonPhrase(Bad Gateway) and error message (errorMessage)
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(Integer.toString(expectedError.value())));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(expectedError.getReasonPhrase()));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(errorMessage.toString()));
        //check for correct resource path (should be /static see ProductionDispatcher)
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(mockedContextPath + "/static/clockwork.png"));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(mockedContextPath + "/static/logo-white.png"));
    }

    @Test
    public void testErrorPageHandling_INTERNAL_SERVER_ERROR() {
        HttpStatus expectedError = HttpStatus.INTERNAL_SERVER_ERROR;

        doReturn(HttpStatus.INTERNAL_SERVER_ERROR.value()).when(requestMock)
                .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Object errorMessage = "SOMETHING THAT PRODUCED A 500 ERROR";
        doReturn(errorMessage).when(requestMock)
                .getAttribute(RequestDispatcher.ERROR_MESSAGE);

        doReturn(mockedContextPath).when(requestMock)
                .getContextPath();

        String responseTemplate = customErrorController.handleError(requestMock);

        //check for HTTP status code (500), reasonPhrase(Internal Server Error) and error message (errorMessage)
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(Integer.toString(expectedError.value())));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(expectedError.getReasonPhrase()));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(errorMessage.toString()));
        //check for correct resource path (should be /static see ProductionDispatcher)
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(mockedContextPath + "/static/clockwork.png"));
        Assert.assertThat(responseTemplate, CoreMatchers.containsString(mockedContextPath + "/static/logo-white.png"));
    }

    @Test

    public void testErrorPageHandling_Status_Undefined() {

        doReturn(null).when(requestMock)
                .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String responseTemplate = customErrorController.handleError(requestMock);

        Assert.assertEquals("OK", "", responseTemplate);

    }

}
