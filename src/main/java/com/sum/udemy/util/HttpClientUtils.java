package com.sum.udemy.util;


import io.fabric8.kubernetes.client.utils.ImpersonatorInterceptor;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static okhttp3.ConnectionSpec.CLEARTEXT;

public class HttpClientUtils {

    private static Pattern VALID_IPV4_PATTERN = null;
    public static final String ipv4Pattern = "(http:\\/\\/|https:\\/\\/)?(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])(\\/[0-9]\\d|1[0-9]\\d|2[0-9]\\d|3[0-2]\\d)?";
    private static final Logger logger = LoggerFactory.getLogger(io.fabric8.kubernetes.client.utils.HttpClientUtils.class);

    static {
        try {
            VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            throw UdemyClientException.launderThrowable("Unable to compile ipv4address pattern.", e);
        }
    }

    public static OkHttpClient createHttpClient(final Config config) {
        return createHttpClient(config, (b) -> {});
    }

    public static OkHttpClient createHttpClientForMockServer(final Config config) {
        return createHttpClient(config, b -> b.protocols(Collections.singletonList(Protocol.HTTP_1_1)));
    }


    private static OkHttpClient createHttpClient(final Config config, final Consumer<OkHttpClient.Builder> additionalConfig) {
        try {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

            // Follow any redirects
            httpClientBuilder.followRedirects(true);
            httpClientBuilder.followSslRedirects(true);
            httpClientBuilder.hostnameVerifier((s, sslSession) -> true);


            httpClientBuilder.addInterceptor(chain -> {
                Request request = chain.request();
                if (Utils.isNotNullOrEmpty(config.getUsername()) && Utils.isNotNullOrEmpty(config.getPassword())) {
                    Request authReq = chain.request().newBuilder().addHeader("Authorization", Credentials.basic(config.getUsername(), config.getPassword())).build();
                    return chain.proceed(authReq);
                } else if (Utils.isNotNullOrEmpty(config.getOauthToken())) {
                    Request authReq = chain.request().newBuilder().addHeader("Authorization", "Bearer " + config.getOauthToken()).build();
                    return chain.proceed(authReq);
                }
                return chain.proceed(request);
            }).addInterceptor(new DefaultContentTypeInceptro());

            Logger reqLogger = LoggerFactory.getLogger(HttpLoggingInterceptor.class);
            if (reqLogger.isTraceEnabled()) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClientBuilder.addNetworkInterceptor(loggingInterceptor);
            }

            return httpClientBuilder.build();
        } catch (Exception e) {
            throw UdemyClientException.launderThrowable(e);
        }
    }


    private static boolean isIpAddress(String ipAddress) {
        Matcher ipMatcher = VALID_IPV4_PATTERN.matcher(ipAddress);
        return ipMatcher.matches();
    }

    private static boolean shouldDisableHttp2() {
        return System.getProperty("java.version", "").startsWith("1.8");
    }
}
