package com.rjdev.meterinstallapp.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.Utils.SessionManager;
import com.rjdev.meterinstallapp.Utils.SharedPresences;

public class HomeActivity extends AppCompatActivity {
    LinearLayout ll_report, ll_downloaddata, ll_offlinedata, ll_addmeter, ds, dendd;
    CardView cv_Report;
    FloatingActionButton fab;
    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    TextView tvNewActivity;
    SharedPreferences.Editor editor;
    private static final int APP_UPDATE = 115;
    AppUpdateManager appUpdateManager;
    SharedPresences sharedPresence;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(getApplicationContext());
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.d("TAG", "Update available");
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, HomeActivity.this, APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        appUpdateManager.registerListener(installStateUpdatedListener);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        editor = sharedPreferences.edit();
        sharedPresence = new SharedPresences(getApplicationContext());
        ll_report = findViewById(R.id.ll_report);
        cv_Report = findViewById(R.id.cv_Report);
        ds = findViewById(R.id.dstart);
        ds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, Duty_Start_Activity.class));
            }
        });
        dendd = findViewById(R.id.dend);
        dendd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, Duty_End_Activity.class));
            }
        });
        ll_downloaddata = findViewById(R.id.ll_downloaddata);
        ll_offlinedata = findViewById(R.id.ll_offlinedata);
        ll_addmeter = findViewById(R.id.ll_addmeter);

        fab = findViewById(R.id.fab);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logoutUser();
                editor.clear();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        cv_Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
        ll_downloaddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, DownloadMeterActivity.class);
                startActivity(intent);
            }
        });
        ll_offlinedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, OfflineDetailsActivity.class);
                startActivity(intent);
            }
        });
        ll_addmeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddMeterDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        clickDone();
    }

    @Override
    protected void onStop() {
        if (appUpdateManager != null)
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    private final InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                showCompletedUpdate();
            }
        }
    };

    private void showCompletedUpdate() {

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New SIPS Meter app is ready!!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == APP_UPDATE && resultCode != RESULT_OK) {
            Toast.makeText(HomeActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.sips_log)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishAffinity();
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
