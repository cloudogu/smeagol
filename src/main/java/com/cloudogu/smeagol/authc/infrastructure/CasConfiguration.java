package com.cloudogu.smeagol.authc.infrastructure;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Provides configuration details for cas authentication.
 */
@Component
public class CasConfiguration {

    private final String contextPath;
    private final String serviceUrl;
    private final String casUrl;

    @Autowired
    public CasConfiguration(
            @Value("${server.contextPath}") String contextPath,
            @Value("${cas.serviceUrl}") String serviceUrl,
            @Value("${cas.url}") String casUrl
    ) {
        this.contextPath = contextPath;
        this.serviceUrl = serviceUrl;
        this.casUrl = casUrl;
    }

    /**
     * Creates a map of cas settings for cas infrastructure components.
     *
     * @return cas settings as map
     */
    Map<String,String> createCasSettings(){
        return ImmutableMap.<String,String>builder()
                .put("casServerLoginUrl", casUrl + "/login")
                .put("serverName", serviceUrl)
                .put("casServerUrlPrefix", casUrl)
                .put("proxyCallbackUrl", serviceUrl + "/proxyCallbackUrl")
                .put("proxyReceptorUrl", contextPath + "/proxyCallbackUrl")
                .put("acceptAnyProxy", "true")
                .build();
    }
}
