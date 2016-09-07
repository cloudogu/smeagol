/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.github.sdorra.milieu.Configuration;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * Main configuration of the application.
 * 
 * @author Sebastian Sdorra
 */
public class WikiServerConfiguration {

    @Configuration("SMEAGOL_PORT")
    private int port = 8080;
    
    @Configuration("SMEAGOL_CONTEXT_PATH")
    private String contextPath = "/smeagol";
    
    @Configuration("SMEAGOL_SERVICE_URL")
    private String serviceUrl;
    
    @Configuration("SMEAGOL_CAS_URL")
    private String casUrl;
    
    @Configuration("SMEAGOL_HOME")
    private String homeDirectory;
    
    @Configuration("SMEAGOL_STAGE")
    private Stage stage = Stage.PRODUCTION;
    
    @Configuration("SMEAGOL_STATIC_PATH")
    private String staticPath = "src/main/webapp";

    @Configuration("SMEAGOL_GEM_PATH")
    private String gemPath = "target/rubygems";
    
    public int getPort() {
        return port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getCasUrl() {
        return casUrl;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public Stage getStage() {
        return stage;
    }

    public String getStaticPath() {
        return staticPath;
    }

    public String getGemPath() {
        return gemPath;
    }
    
    public Map<String,String> getCasSettings(){
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
