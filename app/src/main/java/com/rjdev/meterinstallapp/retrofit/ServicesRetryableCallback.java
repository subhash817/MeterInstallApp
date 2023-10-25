package com.rjdev.meterinstallapp.retrofit;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mobulous2 on 19/4/17.
 */
public abstract class ServicesRetryableCallback<T> implements Callback<T> {

    private int totalRetries = 3;
    private static final String TAG = ServicesRetryableCallback.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;

    public ServicesRetryableCallback(Call<T> call, int totalRetries) {
        this.call = call;
        this.totalRetries = totalRetries;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!isCallSuccess(response))
            if (retryCount++ < totalRetries)
            {
                Log.v(TAG, "Retrying success API Call -  (" + retryCount + " / " + totalRetries + ")");
                retry();
            }
            else
                onFinalResponse(call, response);
        else
            onFinalResponse(call,response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
//        Log.e(TAG, t.getMessage());
        try {

            if (retryCount++ < totalRetries) {
                Log.v(TAG, "Retrying failure API Call -  (" + retryCount + " / " + totalRetries + ")");
                retry();
            } else
                onFinalFailure(call, t);
        }catch (NullPointerException e){
            Log.e("Exception",e.getMessage());
        }catch (Exception e){
            Log.e("Exception",e.getMessage());
        }
    }

    public void onFinalResponse(Call<T> call, Response<T> response) {

    }

    public void onFinalFailure(Call<T> call, Throwable t) {
    }

    private void retry() {
        call.clone().enqueue(this);
    }
    public static boolean isCallSuccess(Response response) {
        int code = response.code();
        return (code >= 200 && code < 400);
    }
}