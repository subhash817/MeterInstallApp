package com.rjdev.meterinstallapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rjdev.meterinstallapp.Adapter.MeterAdapter;
import com.rjdev.meterinstallapp.DBHelper.MeterDatabaseHelper;
import com.rjdev.meterinstallapp.Model.MeterModel;
import com.rjdev.meterinstallapp.R;

import java.util.ArrayList;

public class DownloadMeterActivity extends AppCompatActivity {

    ArrayList<MeterModel> meterdetail;
    RecyclerView recyclerView;
    MeterAdapter datalistAdapter;
    Button bt_download;
    RelativeLayout main_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_meter);
        recyclerView = findViewById(R.id.recyclerView);
        bt_download = findViewById(R.id.bt_download);
        main_layout = findViewById(R.id.main_layout);
        meterdetail  = new ArrayList<>();
       // adddummy();
        setupDetailssList();
    }
    private void adddummy(){
        MeterDatabaseHelper databaseHelper = new MeterDatabaseHelper(getApplicationContext());

        MeterModel contact = new MeterModel(
                "111111",
                "29/08/2020",
                "1");
        if(databaseHelper.addDetails(contact)){
            Toast.makeText(getApplicationContext(), "Details Saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Error Saving", Toast.LENGTH_SHORT).show();
        }
    }

    private void locinitViewsEvent() {


        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(DownloadMeterActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setHasFixedSize(true);
        datalistAdapter = new MeterAdapter(meterdetail, DownloadMeterActivity.this);
        recyclerView.setAdapter(datalistAdapter);
    }

        private void setupDetailssList() {
        meterdetail.clear();
        MeterDatabaseHelper databaseHelper = new MeterDatabaseHelper(getApplicationContext());
        Cursor cursor = databaseHelper.getAllDetails();

        //iterate through all the rows contained in the database
        if(!cursor.moveToNext()){
            Toast.makeText(getApplicationContext(), "There are no data to show", Toast.LENGTH_SHORT).show();
        }

        try {

            while(cursor.isAfterLast() == false){
                meterdetail.add(new MeterModel(cursor.getString(1),cursor.getString(2),cursor.getString(3)));
                cursor.moveToNext();
            }
        }
        catch (Exception e)
        {
            Log.i("LOGGGG11",e.toString());
        }
        finally {
            cursor.close();
        }

        locinitViewsEvent();
    }
}