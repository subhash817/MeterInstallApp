package com.rjdev.meterinstallapp.retrofit;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.rjdev.meterinstallapp.activity.AppController;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import com.example.redemo.RetrofitService.AppController;

/**
 * Created by mobulous2 on 15/3/17.
 */
public class ServicesConnection
{
    ServiceProgressDialog serviceProgressDialog;
    private static ServicesConnection connect;
    private ServicesInterface clientService;

    //private static final String BASE_URL = "http://18.219.101.127:3000/user/";

    //private static final String BASE_URL = "http://34.243.152.208/aesthetic/api/";
    public static final String BASE_URL ="http://1.6.10.113/approve_api2/webservices/index.php?apicall=";
    public static final int DEFAULT_RETRIES = 0;

    public static synchronized ServicesConnection getInstance()
    {
        if (connect == null) {
            connect = new ServicesConnection();
        }
        return connect;
    }

//    service interface instance to call api
    public ServicesInterface createService() throws Exception
    {
        if (clientService == null)
        {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();//    logs HTTP request and response data.
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);//  set your desired log level

            OkHttpClient client =    new OkHttpClient.Builder().
                    addInterceptor(logging).
                    connectTimeout(90, TimeUnit.SECONDS).
                    readTimeout(90, TimeUnit.SECONDS).
                    writeTimeout(90, TimeUnit.SECONDS).
                    addInterceptor(chain -> {
                        Request.Builder requestBuilder = chain.request().newBuilder();
                        requestBuilder.header("Content-Type", "application/json");
//            todo for future            requestBuilder.header("Authorizaton", "serverkey");
                      return  chain.proceed(requestBuilder.build()); //  add logging as last interceptor
                    }).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            clientService = retrofit.create(ServicesInterface.class);
        }
        return clientService;
    }


    //    service interface instance to call api
    public ServicesInterface createVideoService() throws Exception
    {
        if (clientService == null)
        {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();//    logs HTTP request and response data.
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);//  set your desired log level
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//            httpClient.readTimeout(1, TimeUnit.SECONDS)
//                    .connectTimeout(1, TimeUnit.SECONDS);
            httpClient.readTimeout(20, TimeUnit.MINUTES);
            httpClient.readTimeout(20, TimeUnit.MINUTES);
            // add your other interceptors …
            httpClient.addInterceptor(logging); //  add logging as last interceptor

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            clientService = retrofit.create(ServicesInterface.class);
        }
        return clientService;
    }
//    enqueue
    public <T> boolean enqueueWithRetry(Call<T> call, final Activity activity, boolean isLoader, final int retryCount, final Callback<T> callback)
    {
        boolean returntype=false;
        try {
            if (AppController.networkConnectionCheck()) {
                if (isLoader) {
                    serviceProgressDialog = new ServiceProgressDialog(activity);
                    serviceProgressDialog.showCustomProgressDialog();
                }
                call.enqueue(new ServicesRetryableCallback<T>(call, retryCount) {
                    @Override
                    public void onFinalResponse(Call<T> call, Response<T> response) {
                        if (serviceProgressDialog != null) {
                            serviceProgressDialog.hideProgressDialog();
                        }
//                        if (response.body() instanceof ServicesResponseBean) {
//                            if (((ServicesResponseBean) response.body()).getMessage().equals("Please login.")) {
//                                Toast.makeText(activity, "This account already logged in from other device, Please logged in!", Toast.LENGTH_SHORT).show();
//                                //CustomToast.showCustomToast(activity, "Invalid User.");
//                            }
//                        }
                        callback.onResponse(call, response);
                    }

                    @Override
                    public void onFinalFailure(Call<T> call, Throwable t) {
                        if (serviceProgressDialog != null) {
                            serviceProgressDialog.hideProgressDialog();
                        }
                        if (t instanceof SocketTimeoutException) {
                            Toast.makeText(activity,"No internet connection", Toast.LENGTH_SHORT).show();
                            Log.i("onFFMPEGFailure", "Exception");
                        }
                        callback.onFailure(call, t);
                    }
                });
                returntype= true;
            } else {
                serviceProgressDialog.hideProgressDialog();
                Toast.makeText(activity,"No internet connection", Toast.LENGTH_SHORT).show();
                // CustomToast.showCustomToast(activity, activity.getString(R.string.interdis));
                returntype= false;
            }
        }catch (NullPointerException e){
            Log.e("Exception",e.getMessage());
        }catch (Exception e){
            Log.e("Exception",e.getMessage());
        }
        return returntype;
    }

    public  <T> boolean enqueueWithoutRetry(Call<T> call, Activity activity, boolean isLoader, final Callback<T> callback) {
        if (AppController.networkConnectionCheck()) {
            return enqueueWithRetry(call,  activity,isLoader, DEFAULT_RETRIES, callback);
        }
        else {
            Toast.makeText(activity,"No internet connection", Toast.LENGTH_SHORT).show();
        }
        return false;

    }
}
