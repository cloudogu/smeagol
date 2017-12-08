/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.google.common.base.Throwables;
import com.mashape.unirest.http.Unirest;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

/**
 * Util methods for certificate handling.
 *
 * @author Sebastian Sdorra
 */
public final class SSL {

    private SSL() {
    }

    /**
     * Disables certificate checks. <strong>Warning:</strong> After the execution of this method, all certificates are
     * accepted. Use this method only for development and never in production.
     */
    public static void disableCertificateCheck(HttpClientBuilder httpClientBuilder) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, UNSECURE_TRUSTMANAGER, new SecureRandom());
            
            // disable check for java.net.URL
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            // disable check for Unirest
            httpClientBuilder.setSSLContext(sc);
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private static final TrustManager[] UNSECURE_TRUSTMANAGER = new TrustManager[]{
        new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

        }
    };
}
