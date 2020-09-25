package com.sum.udemy.util;

import com.sum.udemy.modal.SubscribedCourses;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

public class OperationSupport<T> {

    public T getData(String url, Class<T> tClass) throws Exception {

        Config config = new Config();
        config.setMasterUrl(url);
        config.setOauthToken(SystemPropertyUtil.getOauthToken());

        OkHttpClient client = HttpClientUtils.createHttpClient(config);

        return get(config,tClass, client);
    }

    public T get(Config config, Class<T> tClass, OkHttpClient client) throws Exception{
        try{
            return getRequestHelper(getUrl(config), tClass, client);
        }catch (Exception e){
            throw  new Exception(e.getMessage());
        }
    }

    private T getRequestHelper(URL url, Class<T> tClass, OkHttpClient client) throws Exception {
        try{
            HttpUrl.Builder builder = HttpUrl.get(url).newBuilder();
            Request.Builder requestUrl = new Request.Builder().get().url(builder.build());
            return handleResponse(requestUrl, tClass, client);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    private T handleResponse(Request.Builder requestBuilder, Class<T> tClass, OkHttpClient client) throws IOException {
        return handleResponse(requestBuilder, tClass, Collections.<String, String>emptyMap(), client);
    }

    private T handleResponse(Request.Builder requestBuilder, Class<T> tClass, Map<String, String> emptyMap, OkHttpClient client) throws IOException {
        return handleResponse(client, requestBuilder, tClass, emptyMap);
    }

    private T handleResponse(OkHttpClient client, Request.Builder requestBuilder, Class<T> tClass, Map<String, String> emptyMap) throws IOException {
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        try (ResponseBody body = response.body()) {
            try (InputStream bodyInputStream = body.byteStream()) {
                return Serialization.unmarshal(bodyInputStream, tClass, emptyMap);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if(response != null && response.body() != null) {
                response.body().close();
            }
        }

    }

    private URL getUrl(Config config) throws Exception {
        URL rootUrl = getRootUrl(config);
        return rootUrl;
    }

    private URL getRootUrl(Config config) throws Exception {
        try{
            return new URL(URLUtils.join(config.getMasterUrl().toString()));
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

}
