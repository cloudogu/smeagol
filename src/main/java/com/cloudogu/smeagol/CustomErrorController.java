package com.cloudogu.smeagol;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


@Controller
public class CustomErrorController implements ErrorController {

    private final String errorsUrl;

    @Autowired
    public CustomErrorController(
        @Value("${errors.url}") String errorsUrl
    ) {
        this.errorsUrl = errorsUrl;
    }

    /**
     * Mapping for errors which are not properly handled with in the application
     */
    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "";
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
            String reasonPhrase = "";
            if (httpStatus != null) {
                reasonPhrase = httpStatus.getReasonPhrase();
            }
            Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            if (message != null) {
                errorMessage = message.toString();
            }
            return renderErrorTemplate(statusCode, reasonPhrase, errorMessage);
        }
        return "";
    }

    @SuppressWarnings("squid:S1192") // sonar issue not relevant for this template
    private String renderErrorTemplate(int statusCode, String reasonPhrase, String message) {
        final String urlString = errorsUrl + statusCode + ".html";

        try {
            final URL url = new URL(urlString);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            final int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    final StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    return response.toString();
                }
            } else {
                return "<html><body><h1>Error " + statusCode + "</h1><p>" + reasonPhrase + "</p><p>" + message + "</p></body></html>";
            }
        } catch (IOException e) {
            return "<html><body><h1>Error " + statusCode + "</h1><p>" + reasonPhrase + "</p><p>" + message + "</p></body></html>";
        }
    }
}
