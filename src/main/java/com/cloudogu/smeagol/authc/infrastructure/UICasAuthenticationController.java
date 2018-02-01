package com.cloudogu.smeagol.authc.infrastructure;

import com.google.common.base.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This endpoint takes an location query parameter and redirects to this location. This behaviour can be used by the ui
 * which run outside of the cas authentication filter, but requires cas authentication. The authentication flow in
 * detail:
 *
 * - user opens ui
 * - ui sends ajax request to api
 * - api responds with 401 ({@link AjaxAwareAuthenticationRedirectStrategy})
 * - ui redirects to /api/v1/authc ({@link UICasAuthenticationController}) with the current location as query parameter
 * - the request gets redirected to cas, by the cas authentication filter
 * - user authenticates himself on the cas login page
 * - cas redirects back to /api/v1/authc ({@link UICasAuthenticationController})
 * - {@link UICasAuthenticationController} extracts the location parameter and redirects back to ui
 */
@RestController
@RequestMapping("/api/v1/authc")
public class UICasAuthenticationController {

    @RequestMapping
    public ResponseEntity<Void> redirect(@RequestParam("location") String location) {
        if (Strings.isNullOrEmpty(location)) {
            return ResponseEntity.badRequest().build();
        }
        return createRedirectResponse(location);
    }

    private ResponseEntity<Void> createRedirectResponse(String location) {
        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, location)
                .build();
    }

}
