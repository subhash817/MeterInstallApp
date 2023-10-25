package com.rjdev.meterinstallapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.Utils.SessionManager;
import com.rjdev.meterinstallapp.Utils.SharedPresences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private static int WELCOME_TIMEOUT = 3000;
    ImageView mImageView, mImageViewname;

    SessionManager sessionManager;
    SharedPresences sharedPresencesUtility;
    List<String> listPermissionsNeeded = new ArrayList<>();

    SwipeRefreshLayout swipeToRefresh;

    public static final int MULTIPLE_PERMISSIONS = 10;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    String permissionsDenied = "";
    RelativeLayout ll_relmain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mImageView = (ImageView) findViewById(R.id.sips_logoo);
        mImageViewname = (ImageView) findViewById(R.id.sips_name);
        ll_relmain = findViewById(R.id.ll_relmain);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        sharedPresencesUtility = new SharedPresences(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());


        new CountDownTimer(1000, 1000) {

            boolean flag = true;

            @Override
            public void onTick(long millisUntilFinished) {

                if (flag) {

                    AlphaAnimation ar = new AlphaAnimation(0.2f, 1.0f);
                    ar.setInterpolator(new BounceInterpolator());
                    ar.setDuration(100);
                    mImageView.setAnimation(ar);
                    ar.start();
                    flag = false;
                }
                if (!flag) {

                    AlphaAnimation ar1 = new AlphaAnimation(1.0f, 0.2f);
                    ar1.setInterpolator(new BounceInterpolator());
                    ar1.setDuration(500);

                    mImageView.setAnimation(ar1);
                    ar1.start();
                    flag = true;

                }

                mImageView.invalidate();

            }

            @Override
            public void onFinish() {


                if (checkPermissions()) {
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c);
                    Log.i("Today", formattedDate);
                    String lastlogindate = sharedPresencesUtility.getLastloginDate(getApplicationContext());

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    Date d1 = null;
                    try {
                        d1 = sdf.parse(formattedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date d2 = null;
                    try {
                        d2 = sdf.parse( lastlogindate );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (compareTo(d1, d2) < 0) {
                        //Log.i("Today","d1 is before d2");
                        sessionManager.logoutUser();

                    } else if (compareTo(d1, d2) > 0) {
                        Log.i("Today2", "d1 is after d2");

                    } else {
                        Log.i("Today3", "d1 is equal to d2");
                    }

                    if (sessionManager.isLoggedIn()) {

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Snackbar snackbar1 = Snackbar.make(ll_relmain, "Alow permission and refresh", Snackbar.LENGTH_LONG);
                    snackbar1.show();

                }
                swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Intent refresh = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(refresh);
                        finish();
                        swipeToRefresh.setRefreshing(false);
                    }
                });


            }
        }.start();
    }


    public long milliseconds(String dates) {
        //String date_ = date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = sdf.parse(dates);
            return date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result;
            for (String p : permissions) {
                result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                return false;
            }
            return true;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissionsList[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionsList, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && listPermissionsNeeded.size() == grantResults.length) {
                    Intent refresh = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(refresh);
                    SplashScreenActivity.this.finish();

                    for (String per : permissionsList) {
                        for (int i = 0; i < permissionsList.length; i++) {
                            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                                permissionsDenied = "\n" + per;

                            }
                        }
                    }
                    // Show permissionsDenied
                }
            }
            Log.i("getpermission", permissionsDenied);
        }
    }

    public static long compareTo(java.util.Date date1, java.util.Date date2) {
        return date1.getTime() - date2.getTime();
    }
}