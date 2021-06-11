package com.cloudogu.smeagol;

import com.google.common.base.Strings;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private final String scmUrl;
    private final RestTemplate scmRestTemplate;

    @Autowired
    public AccountService(ObjectFactory<HttpServletRequest> requestFactory, @Value("${scm.url}") String scmUrl,
                          RestTemplateFactory restTemplateFactory) {
        this.scmRestTemplate = restTemplateFactory.createRestTemplate();
        this.requestFactory = requestFactory;
        this.scmUrl = scmUrl;
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
            if (principal == null) {
                throw new AuthenticationException("could not find principal in request");
            }
            String accessToken = getAccessToken(principal);
            Map<String, Object> attributes = principal.getAttributes();
            account = new Account(
                Objects.toString(attributes.get("username")),
                accessToken,
                Objects.toString(attributes.get("displayName")),
                Objects.toString(attributes.get("mail"))
            );
            session.setAttribute(Account.class.getName(), account);
        }

        return account;
    }

    private synchronized String getAccessToken(AttributePrincipal principal) {
        String accessTokenEndpointURL = getAccessTokenEndpoint();
        String pt = principal.getProxyTicketFor(accessTokenEndpointURL);
        if (Strings.isNullOrEmpty(pt)) {
            throw new AuthenticationException("could not get proxy ticket for scm access token endpoint");
        }
        return fetchSCMAccessToken(accessTokenEndpointURL, pt);
    }

    private String getAccessTokenEndpoint() {
        String accessEndpointURL = scmUrl;
        if (!accessEndpointURL.endsWith("/")) {
            accessEndpointURL = accessEndpointURL.concat("/");
        }
        return accessEndpointURL.concat("api/v2/cas/auth/");
    }

    private String fetchSCMAccessToken(String url, String proxyTicket) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("ticket=" + proxyTicket, headers);

        String accessToken = this.scmRestTemplate.postForObject(url, request, String.class);
        if (Strings.isNullOrEmpty(accessToken)) {
            throw new AuthenticationException("could not get accessToken from scm endpoint");
        }
        return accessToken;
    }
}
