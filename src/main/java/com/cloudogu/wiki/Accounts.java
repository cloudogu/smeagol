/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * Util methods to get the authenticated {@link Account} from current request.
 * 
 * @author Sebastian Sdorra
 */
public final class Accounts {

    private Accounts() {
    }
    
    /**
     * Retrieves the current account from the request. The method uses the stored cas account to build an 
     * {@link Account} object, uses cas ClearPass to fetch the password from the cas server and caches the result in the
     * current user session.
     * 
     * @param configuration wiki server configuration
     * @param request http request
     * 
     * @return current authenticated account
     */
    public static Account fromRequest(WikiServerConfiguration configuration, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Account account = (Account) session.getAttribute(Account.class.getName());
        if (account == null) {
            AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
            if (principal == null){
                throw new WikiAuthenticationException("could not find principal in request");
            }
            char[] password = getUserPassword(configuration, principal);
            Map<String, Object> attributes = principal.getAttributes();
            account = new Account(
                    Objects.toString(attributes.get("username")),
                    password,
                    Objects.toString(attributes.get("displayName")),
                    Objects.toString(attributes.get("mail"))
            );
            session.setAttribute(Account.class.getName(), account);
        }

        return account;
    }

    private static char[] getUserPassword(WikiServerConfiguration configuration, AttributePrincipal principal) {
        String clearPassUrl = clearPassUrl(configuration);
        String pt = principal.getProxyTicketFor(clearPassUrl);
        if (Strings.isNullOrEmpty(pt)){
            throw new WikiAuthenticationException("could not get proxy ticket for clear pass");
        }
        try {
            return fetchClearPassCredentials(new URL(clearPassUrl.concat("?ticket=").concat(pt)));
        } catch (IOException ex) {
            throw new WikiAuthenticationException("failed to fetch clear pass credentials", ex);
        }
    }
    
    private static String clearPassUrl(WikiServerConfiguration configuration){
        String casUrl = configuration.getCasUrl();
        if (!casUrl.endsWith("/")){
            casUrl = casUrl.concat("/");
        }
        return casUrl.concat("clearPass");
    }

    @VisibleForTesting
    static char[] fetchClearPassCredentials(URL url) throws IOException {
        char[] credentials = JAXB.unmarshal(url, ClearPassResponse.class).getCredentials();
        Preconditions.checkState(credentials != null, "failed to fetch password");
        if (credentials == null || credentials.length == 0){
            throw new WikiAuthenticationException("could not extract password from clear pass response");
        }
        return credentials;
    }

    @XmlRootElement(name = "clearPassResponse", namespace = "http://www.yale.edu/tp/cas")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class ClearPassResponse {

        @XmlElement(name = "clearPassSuccess", namespace = "http://www.yale.edu/tp/cas")
        private ClearPassSuccess clearPassSuccess;
        
        @XmlElement(name = "clearPassFailure", namespace = "http://www.yale.edu/tp/cas")
        private String clearPassFailure;
        
        public char[] getCredentials() {
            if (!Strings.isNullOrEmpty(clearPassFailure)){
                throw new WikiAuthenticationException("could not get user password, cas returned: " + clearPassFailure);
            }
            if ( clearPassSuccess != null && clearPassSuccess.credentials != null ){
                return clearPassSuccess.credentials.toCharArray();
            }
            return null;
        }

    }
    
    private static class ClearPassSuccess {
        @XmlElement(name = "credentials", namespace = "http://www.yale.edu/tp/cas")
        private String credentials;        
    }

}
