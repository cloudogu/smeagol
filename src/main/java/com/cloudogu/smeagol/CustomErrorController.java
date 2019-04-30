package com.cloudogu.smeagol;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;


@Controller
public class CustomErrorController implements ErrorController {

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


    @Override
    public String getErrorPath() {
        return "/error";
    }

    @SuppressWarnings("squid:S1192") // sonar issue not relevant for this template
    private String renderErrorTemplate(int statusCode, String reasonPhrase, String message) {
        return String.format("<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Error</title>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <link rel=\"stylesheet\" href=\"/errors/css/errors.css\" media=\"screen\">\n" +
                "    <!-- favicons -->\n" +
                "    <link rel=\"icon\" type=\"image/png\" href=\"/favicon-64px.png\" sizes=\"64x64\" />\n" +
                "    <link rel=\"icon\" type=\"image/png\" href=\"/favicon-32px.png\" sizes=\"32x32\" />\n" +
                "    <link rel=\"icon\" type=\"image/png\" href=\"/favicon-16px.png\" sizes=\"16x16\" />\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"logo\">\n" +
                "        <img src=\"/logo-white.png\">\n" +
                "    </div>\n" +
                "    <div class=\"message\">\n" +
                "        <div class=\"code\">\n" +
                "            %s\n" +
                "        </div>\n" +
                "        <div class=\"description\">\n" +
                "            %s\n" +
                "        </div>\n" +
                "        <div class=\"error\">\n" +
                "            %s\n " +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"background\">\n" +
                "        <img src=\"/clockwork.png\">\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>", statusCode, reasonPhrase, message);
    }


}
