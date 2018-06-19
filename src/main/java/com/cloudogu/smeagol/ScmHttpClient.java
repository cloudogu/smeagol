package com.cloudogu.smeagol;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class ScmHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(ScmHttpClient.class);

    private final AccountService accountService;
    private final LoadingCache<CacheKey, ScmHttpClientResponse> cache;

    @Autowired
    public ScmHttpClient(AccountService accountService, RestTemplateBuilder restTemplateBuilder, @Value("${scm.url}") String scmUrl, Stage stage) {
        this.accountService = accountService;

        RestTemplate restTemplate = createRestTemplate(restTemplateBuilder, stage, scmUrl);
        // cache request for 10 seconds,
        // because some operations requesting the same resource multiple times (e.g. creating the initial search index)
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build(new ScmRequestCacheLoader(restTemplate));
    }

    private RestTemplate createRestTemplate(RestTemplateBuilder restTemplateBuilder, Stage stage, String scmUrl) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                // disable cookie management to avoid scm-manager sessions
                .disableCookieManagement();

        if (stage == Stage.DEVELOPMENT) {
            httpClientBuilder = disableSSLVerification(httpClientBuilder);
        }

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClientBuilder.build());
        return restTemplateBuilder.requestFactory(requestFactory)
                .rootUri(scmUrl)
                .build();
    }

    private HttpClientBuilder disableSSLVerification(HttpClientBuilder httpClientBuilder) {
        LOG.warn("disable ssl verification for scm-manager communication, because we are in development stage");
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = createSSLContext(acceptingTrustStrategy);

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        return httpClientBuilder.setSSLSocketFactory(csf);
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
        return getEntity(url, type, urlVariables).getBody();
    }

    @SuppressWarnings("unchecked")
    public <T> ScmHttpClientResponse<T> getEntity(String url, Class<T> type, Object... urlVariables) {
        // we need to use the http headers as cache key,
        // because we need the authorization header to avoid a privilege escalation
        HttpHeaders headers = createHeaders();
        CacheKey<T> key = new CacheKey<>(url, type, urlVariables, headers);
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    private HttpHeaders createHeaders(){
        Account account = accountService.get();
        LOG.trace("create headers for account {}", account.getUsername());
        HttpHeaders headers = new HttpHeaders();
        String auth = account.getUsername() + ":" + new String(account.getPassword());
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charsets.US_ASCII));
        String authHeader = "Basic " + new String( encodedAuth );
        headers.set("Authorization", authHeader);
        return headers;
    }

    @VisibleForTesting
    public void invalidateCache() {
        cache.invalidateAll();
    }

    @SuppressWarnings("unchecked")
    private static class ScmRequestCacheLoader extends CacheLoader<CacheKey, ScmHttpClientResponse> {

        private final RestTemplate restTemplate;

        ScmRequestCacheLoader(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        @Override
        public ScmHttpClientResponse load(CacheKey key) {
            LOG.trace("fetch {} from {} with {}", key.type, key.url, key.urlVariables);
            Stopwatch sw = Stopwatch.createStarted();

            HttpEntity<?> entity = new HttpEntity<>(key.headers);
            try {
                ResponseEntity response = this.restTemplate.exchange(
                        key.url,
                        HttpMethod.GET,
                        entity,
                        key.type,
                        key.urlVariables
                );

                return ScmHttpClientResponse.of(response.getStatusCode(), response.getBody());
            } catch (HttpClientErrorException ex) {
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return ScmHttpClientResponse.of(ex.getStatusCode());
                }
                throw ex;
            } finally {
                LOG.trace("scm request {} finished in {}", key.url, sw.stop());
            }
        }
    }

    private static class CacheKey<T> {
        private String url;
        private Class<T> type;
        private Object[] urlVariables;
        private HttpHeaders headers;

        CacheKey(String url, Class<T> type, Object[] urlVariables, HttpHeaders headers) {
            this.url = url;
            this.type = type;
            this.urlVariables = urlVariables;
            this.headers = headers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CacheKey<?> cacheKey = (CacheKey<?>) o;
            return Objects.equals(url, cacheKey.url)
                    && Objects.equals(type, cacheKey.type)
                    && Arrays.equals(urlVariables, cacheKey.urlVariables)
                    && Objects.equals(headers, cacheKey.headers);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(url, type, headers);
            result = 31 * result + Arrays.hashCode(urlVariables);
            return result;
        }
    }
}
