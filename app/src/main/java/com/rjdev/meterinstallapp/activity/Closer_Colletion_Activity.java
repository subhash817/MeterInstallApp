package com.rjdev.meterinstallapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rjdev.meterinstallapp.R;

public class Closer_Colletion_Activity extends AppCompatActivity {
Button DaycloserAct,DepositAct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closer__colletion_);
        DaycloserAct=(Button)findViewById(R.id.dca);
        DepositAct=(Button)findViewById(R.id.cda);
        DaycloserAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Closer_Colletion_Activity.this, Day_closer_Activity.class);
                startActivity(intent);
            }
        });

        DepositAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Closer_Colletion_Activity.this, Deposit_Activity.class);
                startActivity(intent);
            }
        });



    }
}