package com.rjdev.meterinstallapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.rjdev.meterinstallapp.R;

public class Deposit_Activity extends AppCompatActivity {
    Button Depositbank, Depositsuperviser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_);
        Depositbank = (Button) findViewById(R.id.dbank);
        Depositsuperviser = (Button) findViewById(R.id.dsuperviser);
        Depositbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Deposit_Activity.this, Deposit_to_Bank.class);
                startActivity(intent);
            }
        });

        Depositsuperviser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Deposit_Activity.this, Deposit_to_Bank.class);
                startActivity(intent);
            }
        });


    }
}