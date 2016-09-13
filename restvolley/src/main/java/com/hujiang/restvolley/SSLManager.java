/*
 * SSLHelper      2016-01-20
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley;

import android.util.Log;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * process host name verify and ssl socket factory.
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-01-20
 */
public class SSLManager {

    private static SSLManager sInstance;

    private SSLContext mSSLContext;
    private HostnameVerifier mHostnameVerifier = ALLOW_ALL_HOSTNAME_VERIFIER;

    private TrustManager mTrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    /**
     * singleton method.
     * @return {@link SSLManager}
     */
    public static SSLManager instance() {
        if (sInstance == null) {
            synchronized (SSLManager.class) {
                if (sInstance == null) {
                    sInstance = new SSLManager();
                }
            }
        }

        return sInstance;
    }

    private SSLManager() {
        try {
            mSSLContext = SSLContext.getInstance("TLS");
            mSSLContext.init(null, new TrustManager[]{mTrustManager}, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        Log.i("liuxiaoming", mSSLContext.getProtocol());
        Log.i("liuxiaoming", String.format("%s, %s, %s", mSSLContext.getProvider().getInfo(), mSSLContext.getProvider().getName(), mSSLContext.getProvider().getVersion()));

        Log.i("liuxiaoming", "***********SSLContext provider*************");
        Set<Map.Entry<Object, Object>> ss = mSSLContext.getProvider().entrySet();
        for (Map.Entry<Object, Object> e : ss) {
            Log.i("liuxiaoming", String.format("%s:%s", e.getKey().toString(), e.getValue().toString()));
        }

        String[] serverDefaultCipherSuites = mSSLContext.getServerSocketFactory().getDefaultCipherSuites();
        String[] serverSupportCipherSuites = mSSLContext.getServerSocketFactory().getSupportedCipherSuites();
        String[] defaultCipherSuites = mSSLContext.getSocketFactory().getDefaultCipherSuites();
        String[] supportCipherSuites = mSSLContext.getSocketFactory().getSupportedCipherSuites();

        SSLEngine sslEngine = mSSLContext.createSSLEngine();
        String[] enabledProtocols = sslEngine.getEnabledProtocols();
        String[] enabledCipherSuites = sslEngine.getEnabledCipherSuites();
        String[] sslEngineSupportedCipherSuites = sslEngine.getSupportedCipherSuites();

        Log.i("liuxiaoming", "***********server default cipher suites*************");
        for (String s : serverDefaultCipherSuites) {
            Log.i("liuxiaoming", s);
        }
        Log.i("liuxiaoming", "***********server supported cipher suites*************");
        for (String s : serverSupportCipherSuites) {
            Log.i("liuxiaoming", s);
        }

        Log.i("liuxiaoming", "***********default cipher suites*************");
        for (String s : defaultCipherSuites) {
            Log.i("liuxiaoming", s);
        }

        Log.i("liuxiaoming", "***********supported cipher suites*************");
        for (String s : supportCipherSuites) {
            Log.i("liuxiaoming", s);
        }

        Log.i("liuxiaoming", "***********enabled protocols*************");
        for (String s : enabledProtocols) {
            Log.i("liuxiaoming", s);
        }

        Log.i("liuxiaoming", "***********enabled cipher suites*************");
        for (String s : enabledCipherSuites) {
            Log.i("liuxiaoming", s);
        }

        Log.i("liuxiaoming", "***********ssl engine supported cipher suites*************");
        for (String s : sslEngineSupportedCipherSuites) {
            Log.i("liuxiaoming", s);
        }
    }

    /**
     * get {@link SSLSocketFactory}.
     * @return {@link SSLSocketFactory}
     */
    public SSLSocketFactory getSSLSocketFactory() {
        return mSSLContext.getSocketFactory();
    }

    /**
     * get {@link HostnameVerifier}.
     * @return {@link HostnameVerifier}
     */
    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    /**
     * allow all hostname verifier.
     */
    public static final HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}