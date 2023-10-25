package com.rjdev.meterinstallapp.server;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by anupamchugh on 05/01/17.
 */

public class APIClient {

    private static Retrofit retrofit = null;
    public static final String mainUrl="https://reqres.in";

    public static Retrofit getClient() {

        /*HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();*/
        //new Code
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = null;
                          /*      if (authorizationValue != null) {*/
                                    Log.d("--accessToken-- ", "");

                                    Request original = chain.request();
                                    // Request customization: add request headers
                                    Request.Builder requestBuilder = original.newBuilder()
                                            .addHeader("accessToken", "1cc48aa8bf03c3e57358c8a74f9224e4");

                                    request = requestBuilder.build();
                                //}
                                return chain.proceed(request);
                            }
                        })
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl("http://18.221.126.139/foodTruck/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .build();

        return retrofit;
    }

}
