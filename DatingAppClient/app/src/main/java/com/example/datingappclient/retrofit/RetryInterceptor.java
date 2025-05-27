package com.example.datingappclient.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.datingappclient.constants.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {

    private final int maxRetries;
    private final long retryDelayMillis;

    public RetryInterceptor(int maxRetries, long retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        IOException exception = null;

        String logTag = Constants.GLOBAL_LOG_TAG + "TRY CONNECT";
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                Response response = chain.proceed(request);

                if (!response.isSuccessful() && response.code() >= 500) {
                    Log.e(logTag,response.code() + " " + response.message() + " retry attempt - " + attempt + "/" + maxRetries);
                    response.close();
                    try {
                        Thread.sleep(retryDelayMillis);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Retry interrupted", e);
                    }
                    continue;
                }

                return response;
            } catch (IOException e) {
                exception = e;
                try {
                    Thread.sleep(retryDelayMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // прерываем поток корректно
                    throw new IOException("Retry interrupted", ie);
                }
            }
        }

        throw exception != null ? exception : new IOException("Unknown network error after " + maxRetries + " retries");
    }
}
