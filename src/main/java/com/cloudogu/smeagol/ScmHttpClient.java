package com.cloudogu.smeagol;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Optional;

@Service
public class ScmHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(ScmHttpClient.class);

    private RestTemplate restTemplate;
    private AccountService accountService;

    @Autowired
    public ScmHttpClient(AccountService accountService, RestTemplateBuilder restTemplateBuilder, @Value("${scm.url}") String scmUrl, Stage stage) {
        this.accountService = accountService;

        RestTemplateBuilder builder = restTemplateBuilder;
        if (stage == Stage.DEVELOPMENT) {
            builder = disableSSLVerification(restTemplateBuilder);
        }
        this.restTemplate = builder.rootUri(scmUrl).build();
    }

    private RestTemplateBuilder disableSSLVerification(RestTemplateBuilder restTemplateBuilder) {
        LOG.warn("disable ssl verification for scm-manager communication, because we are in development stage");
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = createSSLContext(acceptingTrustStrategy);

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        return restTemplateBuilder.requestFactory(requestFactory);
    }

    private SSLContext createSSLContext(TrustStrategy acceptingTrustStrategy) {
        try {
            return SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex) {
            throw Throwables.propagate(ex);
        }
    }

    public <T> Optional<T> get(String url, Class<T> type, Object... urlVariables) {
        return getEntity(url, type, urlVariables).map(HttpEntity::getBody);
    }

    public <T> Optional<ResponseEntity<T>> getEntity(String url, Class<T> type, Object... urlVariables) {
        LOG.trace("fetch {} from {} with {}", type, url, urlVariables);
        Stopwatch sw = Stopwatch.createStarted();

        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<T> response = this.restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    type,
                    urlVariables
            );
            return Optional.of(response);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw ex;
            }
        } finally {
            LOG.trace("scm request {} finished in {}", url, sw.stop());
        }
        return Optional.empty();
    }

    private HttpHeaders createHeaders(){
        Account account = accountService.get();
        HttpHeaders headers = new HttpHeaders();
        String auth = account.getUsername() + ":" + new String(account.getPassword());
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charsets.US_ASCII));
        String authHeader = "Basic " + new String( encodedAuth );
        headers.set("Authorization", authHeader);
        return headers;
    }
}
