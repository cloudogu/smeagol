/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.smeagol;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * Util methods to get the authenticated {@link Account} from current request.
 * 
 * @author Sebastian Sdorra
 */
@Service
public class AccountService {

    private final ObjectFactory<HttpServletRequest> requestFactory;
    private final String casUrl;

    @Autowired
    public AccountService(ObjectFactory<HttpServletRequest> requestFactory, @Value("${cas.url}") String casUrl) {
        this.requestFactory = requestFactory;
        this.casUrl = casUrl;
    }

    /**
     * Retrieves the current account from the request. The method uses the stored cas account to build an 
     * {@link Account} object, uses cas ClearPass to fetch the password from the cas server and caches the result in the
     * current user session.
     * 
     * @return current authenticated account
     */
    public Account get() {
        HttpServletRequest request = requestFactory.getObject();

        HttpSession session = request.getSession(true);
        Account account = (Account) session.getAttribute(Account.class.getName());
        if (account == null) {
            AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
            if (principal == null){
                throw new AuthenticationException("could not find principal in request");
            }
            char[] password = getUserPassword(principal);
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

    private char[] getUserPassword(AttributePrincipal principal) {
        String clearPassUrl = clearPassUrl();
        String pt = principal.getProxyTicketFor(clearPassUrl);
        if (Strings.isNullOrEmpty(pt)){
            throw new AuthenticationException("could not get proxy ticket for clear pass");
        }
        try {
            return fetchClearPassCredentials(new URL(clearPassUrl.concat("?ticket=").concat(pt)));
        } catch (IOException ex) {
            throw new AuthenticationException("failed to fetch clear pass credentials", ex);
        }
    }
    
    private String clearPassUrl(){
        String clearPassUrl = casUrl;
        if (!clearPassUrl.endsWith("/")){
            clearPassUrl = clearPassUrl.concat("/");
        }
        return clearPassUrl.concat("clearPass");
    }

    @VisibleForTesting
    static char[] fetchClearPassCredentials(URL url) {
        char[] credentials = JAXB.unmarshal(url, ClearPassResponse.class).getCredentials();
        Preconditions.checkState(credentials != null, "failed to fetch password");
        if (credentials.length == 0){
            throw new AuthenticationException("could not extract password from clear pass response");
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
                throw new AuthenticationException("could not get user password, cas returned: " + clearPassFailure);
            }
            if ( clearPassSuccess != null && clearPassSuccess.credentials != null ){
                return clearPassSuccess.credentials.toCharArray();
            }
            return new char[0];
        }

    }
    
    private static class ClearPassSuccess {
        @XmlElement(name = "credentials", namespace = "http://www.yale.edu/tp/cas")
        private String credentials;        
    }

}
