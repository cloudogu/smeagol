/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.smeagol.authc.infrastructure;

import com.google.common.base.Throwables;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Util methods for certificate handling.
 *
 * @author Sebastian Sdorra
 */
public final class SSL {

    private SSL() {
    }

    /**
     * Disables certificate checks for {@link java.net.URL} based http clients. <strong>Warning:</strong> After the
     * execution of this method, all certificates are accepted. Use this method only for development and never in
     * production.
     */
    public static void disableCertificateCheck() {
        SSLContext sc = createSSLContext();
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    private static SSLContext createSSLContext() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, UNSECURE_TRUSTMANAGER, new SecureRandom());
            return sc;
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
