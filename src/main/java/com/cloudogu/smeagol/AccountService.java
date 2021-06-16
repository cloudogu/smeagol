package com.cloudogu.smeagol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import static com.cloudogu.smeagol.ScmHttpClient.createRestTemplate;

/**
 * Util methods to get the authenticated {@link Account} from current request.
 *
 * @author Sebastian Sdorra
 */
@Service
public class AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final ObjectFactory<HttpServletRequest> requestFactory;
    private final String scmUrl;
    private final RestTemplate scmRestTemplate;

    private static final String ACCESS_TOKEN_ENDPOINT = "/api/v2/cas/auth/";

    @Autowired
    public AccountService(ObjectFactory<HttpServletRequest> requestFactory, RestTemplateBuilder restTemplateBuilder,
                          @Value("${scm.url}") String scmUrl, Stage stage) {
        this.scmRestTemplate = createRestTemplate(restTemplateBuilder, stage, scmUrl);
        this.requestFactory = requestFactory;
        this.scmUrl = scmUrl;
    }

    /**
     * Retrieves the current account from the request. The method uses the stored cas account to build an
     * {@link Account} object, uses cas proxy ticket to fetch an access token from the scm server and caches the result in the
     * current user session.
     *
     * @return current authenticated account
     */
    public Account get() {
        HttpServletRequest request = requestFactory.getObject();

        HttpSession session = request.getSession(true);
        Account account = (Account) session.getAttribute(Account.class.getName());

        if (account != null) {
            boolean shouldRefetch = true;
            try {
                shouldRefetch = shouldRefetchToken(account.getAccessToken());
            } catch (Exception e) {
                LOG.warn("could not determine whether access token should be refreshed: ", e);
            }
            if (!shouldRefetch) {
                return account;
            }
        }

        account = getNewAccount(request);
        session.setAttribute(Account.class.getName(), account);
        return account;
    }

    private Account getNewAccount(HttpServletRequest request) {
        Account account;
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

        return account;
    }

    private String getAccessToken(AttributePrincipal principal) {
        String accessTokenEndpointURL = getAccessTokenEndpoint();
        String pt = principal.getProxyTicketFor(accessTokenEndpointURL);
        if (Strings.isNullOrEmpty(pt)) {
            throw new AuthenticationException("could not get proxy ticket for scm access token endpoint");
        }
        return fetchSCMAccessToken(ACCESS_TOKEN_ENDPOINT, pt);
    }

    private String getAccessTokenEndpoint() {
        String accessEndpointURL = scmUrl;
        if (accessEndpointURL.endsWith("/")) {
            accessEndpointURL = accessEndpointURL.substring(0, accessEndpointURL.length() - 1);
        }
        return accessEndpointURL.concat(ACCESS_TOKEN_ENDPOINT);
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

    protected static boolean shouldRefetchToken(String jwt) throws IOException {
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper mapper = new ObjectMapper();
        SCMJwt scmJwt = mapper.readValue(payload, SCMJwt.class);
        long currentUnixTime = System.currentTimeMillis() / 1000L;

        return scmJwt.exp - currentUnixTime < 60;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SCMJwt {

        private long exp;

        public void setExp(long exp) {
            this.exp = exp;
        }
    }

}
