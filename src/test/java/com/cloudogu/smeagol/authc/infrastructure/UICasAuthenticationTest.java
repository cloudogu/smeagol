package com.cloudogu.smeagol.authc.infrastructure;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class UICasAuthenticationTest {

    @Test
    public void testRedirectWithoutLocation() {
        UICasAuthentication authentication = new UICasAuthentication();
        ResponseEntity<Void> entity = authentication.redirect(null);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testRedirect() {
        UICasAuthentication authentication = new UICasAuthentication();
        ResponseEntity<Void> entity = authentication.redirect("/hitchhikers");

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT);
        assertThat(entity.getHeaders().get("location")).contains("/hitchhikers");
    }

}