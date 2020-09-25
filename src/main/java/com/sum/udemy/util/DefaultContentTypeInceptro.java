package com.sum.udemy.util;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class DefaultContentTypeInceptro implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request build = request.newBuilder().header("Content-Type", "application/json").build();
        return chain.proceed(build);
    }
}
