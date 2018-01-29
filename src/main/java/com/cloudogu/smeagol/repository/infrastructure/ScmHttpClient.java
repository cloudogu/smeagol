package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.Account;
import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.Stage;
import com.google.common.base.Charsets;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;

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

    public <T> T get(String url, Class<T> type) {
        // TOOD use clear pass credentials
        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = this.restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            type
        );
        return response.getBody();
    }

    private HttpHeaders createHeaders(){
        Account account = accountService.get();
        return new HttpHeaders() {{
            String auth = account.getUsername() + ":" + new String(account.getPassword());
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charsets.US_ASCII));
            String authHeader = "Basic " + new String( encodedAuth );
            set("Authorization", authHeader);
        }};
    }
}
