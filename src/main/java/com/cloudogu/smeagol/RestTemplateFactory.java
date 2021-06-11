package com.cloudogu.smeagol;


import com.google.common.base.Throwables;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Component
public class RestTemplateFactory {

    private final String scmUrl;
    private final RestTemplateBuilder restTemplateBuilder;
    private final Stage stage;

    @Autowired
    public RestTemplateFactory(@Value("${scm.url}") String scmUrl, RestTemplateBuilder restTemplateBuilder, Stage stage) {
        this.scmUrl = scmUrl;
        this.restTemplateBuilder = restTemplateBuilder;
        this.stage = stage;
    }

    public RestTemplate createRestTemplate() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
            // disable cookie management to avoid scm-manager sessions
            .disableCookieManagement();

        if (stage == Stage.DEVELOPMENT) {
            httpClientBuilder = disableSSLVerification(httpClientBuilder);
        }

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClientBuilder.build());
        return restTemplateBuilder.requestFactory(() -> requestFactory)
            .rootUri(scmUrl)
            .build();
    }

    public HttpClientBuilder disableSSLVerification(HttpClientBuilder httpClientBuilder) {
        // TODO:
        //LOG.warn("disable ssl verification for scm-manager communication, because we are in development stage");
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = createSSLContext(acceptingTrustStrategy);

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        return httpClientBuilder.setSSLSocketFactory(csf);
    }

    public SSLContext createSSLContext(TrustStrategy acceptingTrustStrategy) {
        try {
            return SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex) {
            throw Throwables.propagate(ex);
        }
    }
}
