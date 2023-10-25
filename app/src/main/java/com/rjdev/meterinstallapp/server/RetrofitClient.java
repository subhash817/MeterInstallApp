package com.rjdev.meterinstallapp.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rjdev.meterinstallapp.interfaces.ApiInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://1.6.10.121/meter/tpdil_api/webservices/";
    private static Retrofit retrofit;
    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static ApiInterface getService() {
        if (retrofit == null) {
            httpClient.connectTimeout(6000, TimeUnit.SECONDS); // connect timeout
            httpClient.writeTimeout(5, TimeUnit.MINUTES); // write timeout
            httpClient.readTimeout(5, TimeUnit.MINUTES); // read timeout
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(httpLoggingInterceptor);
            OkHttpClient client = httpClient.build();
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }
}