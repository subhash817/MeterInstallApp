package com.rjdev.meterinstallapp.activity;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.Socket;


/**
 * Created by fluper on 8/2/18.
 */


/**
 * Created by mobua01 on 5/5/17.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
   // NetworkConnectionCheck connectionCheck;

    private RequestQueue mRequestQueue;


    private static AppController mInstance;

    private Socket socket1;

    {

    }
    public Socket getSocket1() {
        return socket1;
    }


    public static boolean networkConnectionCheck() {
       // if (mInstance.connectionCheck == null) {
         //   mInstance.connectionCheck = new NetworkConnectionCheck(mInstance);
      //  }
        //return mInstance.connectionCheck.isConnect();
        return false;
   }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  ACRA.init(this);
        mInstance = this;

//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
//                .name(Realm.DEFAULT_REALM_NAME)
//                .schemaVersion(0)
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(realmConfiguration);


        //connectionCheck = new NetworkConnectionCheck(this);

    }

    public static synchronized AppController getInstance() {

        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

}

