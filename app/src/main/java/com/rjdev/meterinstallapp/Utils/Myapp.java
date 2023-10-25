package com.rjdev.meterinstallapp.Utils;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;


public class Myapp extends Application {
    public   static Context context;
    public Gson gson;
    @Override
    public void onCreate() {
        super.onCreate();
        gson = new Gson();
        context=getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
