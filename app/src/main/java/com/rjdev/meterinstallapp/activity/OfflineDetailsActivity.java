package com.rjdev.meterinstallapp.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.rjdev.meterinstallapp.Adapter.OfflineDetailAdapter;
import com.rjdev.meterinstallapp.DBHelper.DatabaseHelper;
import com.rjdev.meterinstallapp.Model.InstallDetailModel;
import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.Utils.BaseUrlActivity;
import com.rjdev.meterinstallapp.Utils.SharedPresences;
import com.rjdev.meterinstallapp.Utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OfflineDetailsActivity extends BaseActivity {
    ArrayList<InstallDetailModel> installdetail;
    RecyclerView recyclerView;
    OfflineDetailAdapter datalistAdapter;
    int startcounter = 0;
    int checkcounter = 0;
    Button bt_upload;
    RelativeLayout main_layout;
    SharedPresences sharedPresences;
    String todayFormat = "yyyy-MM-dd";
    SimpleDateFormat tdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_details);
        recyclerView = findViewById(R.id.recyclerView);
        bt_upload = findViewById(R.id.bt_upload);
        main_layout = findViewById(R.id.main_layout);
        tdf = new SimpleDateFormat(todayFormat, Locale.US);
        installdetail  = new ArrayList<>();
        sharedPresences = new SharedPresences(getApplicationContext());
        setupDetailssList();
        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startupload();
            }
        });
    }
    private void locinitViewsEvent() {
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(OfflineDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setHasFixedSize(true);
        datalistAdapter = new OfflineDetailAdapter(installdetail,OfflineDetailsActivity.this);
        recyclerView.setAdapter(datalistAdapter);

    }

    private void setupDetailssList() {
        installdetail.clear();
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor cursor = databaseHelper.getAllDetails();

        //iterate through all the rows contained in the database
        if(!cursor.moveToNext()){
            Toast.makeText(getApplicationContext(), "There are no data to show", Toast.LENGTH_SHORT).show();
        }

        try {
            while(cursor.isAfterLast() == false){
                installdetail.add(new InstallDetailModel(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5), cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9)));
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
    public  void startupload(){
        checkcounter = installdetail.size();
        for(int i=0; i<installdetail.size(); i++){
            doNormalPostOperation(i);
        }
    }
    public void doNormalPostOperation(final int i)
    {
        startcounter++;
        String strcoordinate = installdetail.get(i).getGps_coordinate();
        final String[]  splitStr = strcoordinate.split("\\s+");
        showProgressBar();
        String url = BaseUrlActivity.urlmain + "addmeter";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {

                            Log.e("REsponce22", response.toString());
                            JSONObject obj = new JSONObject(new String(response.data));

                            Log.e("REsponce", obj.toString());

                            String success = obj.getString("ResponseCode");
                            String respmessage = obj.getString("ResponseMessage");
                            Log.i("Resp1234", success);
                            if (success.equals("200")) {


                                if (startcounter==checkcounter){
                                    DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                                    databaseHelper.deleteAll();
                                    recyclerView.setVisibility(View.GONE);
                                    Snackbar.make(main_layout,"Uploaded Successfully",Snackbar.LENGTH_LONG).show();
                                    hideProgressBar();
                                    Intent intent = new Intent(OfflineDetailsActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }


                            } else {
                                hideProgressBar();

                                Snackbar snackbar1 = Snackbar.make(main_layout, respmessage, Snackbar.LENGTH_LONG);
                                snackbar1.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressBar();
                        //  Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("daily_schedule_id", "2");
                params.put("vehicle_id", "1");
                params.put("meter_no", installdetail.get(i).getmeter_no());
                params.put("protocol_no", installdetail.get(i).getprotocol_no());
                params.put("consumer_no", installdetail.get(i).getconsumer_no());
                params.put("installation_type_id", installdetail.get(i).getinstallation_type());
                params.put("installation_date", installdetail.get(i).getinstallation_date());
             //   params.put("meter_make", metermark.getText().toString());
              //  params.put("installation_type", instalation_type);
                params.put("latitude", splitStr[0]);
                params.put("longitude", splitStr[1]);
                params.put("location", installdetail.get(i).getAddress());
                params.put("userid", sharedPresences.getEmpId(getApplicationContext()));

                return params;
            }
            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                String  imagename = sharedPresences.getUserId(getApplicationContext())+tdf.format(new Date())+" "+installdetail.get(i).getmeter_no() ;
                params.put("photo", new VolleyMultipartRequest.DataPart(  "Meter_" + imagename + ".png", getFileDataFromDrawable(StringToBitMap(installdetail.get(i).getBit_meterimage()))));
                params.put("proto_photo", new VolleyMultipartRequest.DataPart(  "Protocol_" + imagename + ".png", getFileDataFromDrawable(StringToBitMap(installdetail.get(i).getBit_protocol()))));
                return params;
            }
        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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
