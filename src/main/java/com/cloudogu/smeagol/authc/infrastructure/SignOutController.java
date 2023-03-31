package com.cloudogu.smeagol.authc.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Destroy the user session and redirects to the cas server.
 */
@RestController
@RequestMapping("/api/v1/logout")
public class SignOutController {

    private CasConfiguration casConfiguration;

    @Autowired
    public SignOutController(CasConfiguration casConfiguration) {
        this.casConfiguration = casConfiguration;
    }

    @RequestMapping
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, casConfiguration.getLogoutUrl())
                .build();
    }
}
