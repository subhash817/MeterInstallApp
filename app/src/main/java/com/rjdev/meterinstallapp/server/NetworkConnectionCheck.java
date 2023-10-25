package com.rjdev.meterinstallapp.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by fluper on 22/2/18.
 */

public class NetworkConnectionCheck {
    private Context context;

    public NetworkConnectionCheck(Context context){
        this.context=context;
    }

    public boolean isConnect()
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
//            NetworkInfo[] info = connectivity.getAllNetworkInfo();
//            if (info != null)
//                for (int i = 0; i < info.length; i++)
//                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
//                    {
//                        return true;
//                    }

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnectedOrConnecting())
            {
//                Toast.makeText(context, "Detail State : "+info.getDetailedState()+"\n"+"Extra info : "+info.getExtraInfo(), Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

}
