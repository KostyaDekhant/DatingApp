package com.example.datingappclient.retrofit;

import android.content.Context;

import com.example.datingappclient.R;
import com.example.datingappclient.TokenManager;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.retrofit.api.AuthAPI;
import com.example.datingappclient.retrofit.repository.AuthRepository;
import com.example.datingappclient.utils.gson.LocalDateDeserializer;
import com.example.datingappclient.utils.gson.LocalDateSerializer;
import com.example.datingappclient.utils.gson.TimestampDeserializer;
import com.example.datingappclient.utils.gson.TimestampSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;
import java.time.LocalDate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT;

    private static Retrofit retrofit;
    private static Retrofit authRetrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampDeserializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampSerializer())
                    .setLenient()
                    .create();

            Retrofit authRetrofit = RetrofitClient.getAuthOnlyClient(context);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getHTTPClient(context, new AuthRepository(authRetrofit.create(AuthAPI.class)), new TokenManager(context)))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getAuthOnlyClient(Context context) {
        if (authRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampDeserializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampSerializer())
                    .setLenient()
                    .create();

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            try {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                SSLSocketFactory sslSocketFactory = getSSLSocketFactory(context, tmf);
                clientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) tmf.getTrustManagers()[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clientBuilder.addInterceptor(new RetryInterceptor(3, 5000));

            authRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return authRetrofit;
    }

    private static OkHttpClient getHTTPClient(Context context, AuthRepository authRepository, TokenManager tokenManager) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // для HTTPS
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            SSLSocketFactory sslSocketFactory = getSSLSocketFactory(context, tmf);
            httpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) tmf.getTrustManagers()[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            String token = tokenManager.getAccessToken();

            Request.Builder requestBuilder = original.newBuilder();

            if (token != null) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        })
                .authenticator(new TokenAuthenticator(authRepository, tokenManager))
                .addInterceptor(new RetryInterceptor(3, 5000));

        return httpClient.build();
    }

    private static SSLSocketFactory getSSLSocketFactory(Context context, TrustManagerFactory tmf) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(R.raw.selfsigned);
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }
}
