package com.rjdev.meterinstallapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.rjdev.meterinstallapp.R;

public class Background_Location_Alert extends AppCompatActivity {

    private Button bt_deny,bt_accept;
    String prevStarted = "yes";
    boolean accept;
    SharedPreferences sharedpreferences;

    @Override
    protected void onResume() {
        super.onResume();

            sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            if (!sharedpreferences.getBoolean(prevStarted, false)) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(prevStarted, Boolean.TRUE);
                editor.apply();
            }
            else {
                moveToSecondary();
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_location_alert);

       // bt_deny = findViewById(R.id.bt_deny);
        bt_accept = findViewById(R.id.bt_accept);

        bt_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Background_Location_Alert.this,SplashScreenActivity.class);
                startActivity(intent);
            }
        });
        /*bt_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
*/
    }
    public void moveToSecondary(){
        // use an intent to travel from one activity to another.
        Intent intent = new Intent(this,SplashScreenActivity.class);
        startActivity(intent);
       // finish();
    }
}