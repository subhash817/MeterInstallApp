package com.rjdev.meterinstallapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rjdev.meterinstallapp.R;


public class BaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    public void checkForCrashes() {
//        CrashManager.register(this);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showProgressBar() {

        if (progressDialog != null && progressDialog.isShowing()) {
            // do nothing
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading....");
            progressDialog.show();
        }
    }

    public void hideProgressBar() {
        if (this.progressDialog != null && !this.isFinishing()) {
            progressDialog.dismiss();
        }
    }

    public void statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void launchActivity(Class<? extends BaseActivity> clazz) {
        if (clazz != null) {
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        }
    }

    public void launchActivity(Class<? extends BaseActivity> clazz, Bundle bundle) {
        if (clazz != null) {
            Intent intent = new Intent(this, clazz);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
        }
    }

    public void launchActivityNewTask(Class<? extends BaseActivity> clazz, Bundle bundle) {
        if (clazz != null) {
            Intent intent = new Intent(this, clazz);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
        }
    }

    public void showToast(String msg) {
        showToast(msg, true);
    }

    public void showToast(String msg, boolean isShort) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(this, msg, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
        }
    }

    public void fullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
