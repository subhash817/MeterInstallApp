package com.rjdev.meterinstallapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rjdev.meterinstallapp.DBHelper.DatabaseHelper;
import com.rjdev.meterinstallapp.Model.InstallDetailModel;
import com.rjdev.meterinstallapp.R;

public class MeterDetailActivity extends AppCompatActivity {
    String strcno,strmeter_no,strprono,strinstdate,strinsttype,strgps,str_address;
    Bitmap b_bitimg,b_bitpro;
    ImageView img_protocol,img_meter;
    TextView ed_customerno,ed_meterno,ed_protocolno,ed_installationdate,ed_installationtype,ed_cordinate,tv_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_detail);
        ed_customerno = findViewById(R.id.ed_customerno);
        ed_meterno = findViewById(R.id.ed_meterno);
        ed_protocolno = findViewById(R.id.ed_protocolno);
        ed_installationdate = findViewById(R.id.ed_installationdate);
        ed_installationtype = findViewById(R.id.ed_installationtype);
        ed_cordinate = findViewById(R.id.ed_cordinate);
        tv_address = findViewById(R.id.tv_address);
        img_protocol = findViewById(R.id.img_protocol);
        img_meter = findViewById(R.id.img_meter);

        Intent intent = getIntent();
        String txt = intent.getStringExtra("customer");
        setupDetailssList(txt);
    }

    private void setupDetailssList(String cust) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor cursor = databaseHelper.getDetailsID(new InstallDetailModel(cust,"","","","",
                "","","",""));

        //iterate through all the rows contained in the database
        if (!cursor.moveToNext()) {
            Toast.makeText(getApplicationContext(), "There are no data to show", Toast.LENGTH_SHORT).show();
        }

        try {

            while (cursor.isAfterLast() == false) {
                 strcno = cursor.getString(1);
                 strmeter_no = cursor.getString(2);
                 strprono = cursor.getString(3);
                 strinstdate = cursor.getString(4);
                 strinsttype = cursor.getString(5);
                 strgps = cursor.getString(6);
                 str_address = cursor.getString(7);
                 b_bitimg = StringToBitMap(cursor.getString(8));
                 b_bitpro = StringToBitMap(cursor.getString(9));
                Log.i("STRCUST",strcno);

                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.i("LOGGGG11", e.toString());
        } finally {
            cursor.close();
        }
        showonBoard();

    }
    void  showonBoard(){
        img_meter.setImageBitmap(b_bitimg);
        img_protocol.setImageBitmap(b_bitpro);
        ed_cordinate.setText(strgps);
        ed_protocolno.setText(strprono);
        ed_meterno.setText(strmeter_no);
        ed_installationdate.setText(strinstdate);
        ed_installationtype.setText(strinsttype);
        tv_address.setText(str_address);
        ed_customerno.setText(strcno);
    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmaps = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmaps;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}