package com.rjdev.meterinstallapp.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.rjdev.meterinstallapp.Adapter.ReportAdapter;
import com.rjdev.meterinstallapp.Adapter.ReportsByUserAdapter;
import com.rjdev.meterinstallapp.Model.ReportModel;
import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.Utils.BaseUrlActivity;
import com.rjdev.meterinstallapp.Utils.CommonMethods;
import com.rjdev.meterinstallapp.Utils.PrefrenceKey;
import com.rjdev.meterinstallapp.Utils.SharedPresences;
import com.rjdev.meterinstallapp.interfaces.ApiInterface;
import com.rjdev.meterinstallapp.reports.InstallationCountUserdate;
import com.rjdev.meterinstallapp.reports.ReportByUserDate;
import com.rjdev.meterinstallapp.server.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class ReportActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<ReportModel> rarray;
    private ReportAdapter rAdapter;
    DatePickerDialog datePicker;
    SharedPresences sharedPresence;
    ImageView back, imgStart, imgEnd;
    TextView empId, startReading, txtScheDate, endReading, vehicleRegNum,
            startLoc, endLoc, vehicleDeatil, tv_task_date;
    RecyclerView rcvByUserData;
    LinearLayout llUserReports;
    SimpleDateFormat tdf;
    String currentDate, selectDate;
    String userId;
    Context context;
    String todayFormat = "yyyy-MM-dd";
    public String urlEndImage = "http://1.6.10.121/meter/tpdil_api/uploads/end_duty/";
    public String urlStartImage = "http://1.6.10.121/meter/tpdil_api/uploads/start_duty/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        recyclerView = findViewById(R.id.recyclerView);
        back = findViewById(R.id.back);
        empId = findViewById(R.id.txtEmpId);
        startReading = findViewById(R.id.txtStartReading);
        endReading = findViewById(R.id.txtEndReading);
        vehicleRegNum = findViewById(R.id.txtVehicleNum);
        txtScheDate = findViewById(R.id.txtScheDate);
        startLoc = findViewById(R.id.txtStartLoc);
        endLoc = findViewById(R.id.txtEndLoc);
        vehicleDeatil = findViewById(R.id.txtVehicleDetails);
        rcvByUserData = findViewById(R.id.rcvInstallUserDate);
        tv_task_date = findViewById(R.id.tv_task_date);
        llUserReports = findViewById(R.id.llUserReports);
        imgStart = findViewById(R.id.imgStart);
        imgEnd = findViewById(R.id.imgEnd);
        rarray = new ArrayList<ReportModel>();

        tdf = new SimpleDateFormat(todayFormat, Locale.US);
        sharedPresence = new SharedPresences(getApplicationContext());
        userId = sharedPresence.getEmpId(getApplicationContext());
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tv_task_date.setText(currentDate);
        System.out.println("UserDate" + userId + currentDate);
        locinitViews();
        initView();
        getReports();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initView() {
        //date picker yyyy-mm-dd
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        long current = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -29);
        long prev = calendar.getTimeInMillis();

        tv_task_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker = new DatePickerDialog(ReportActivity.this, R.style.Theme_AppCompat_DayNight, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        // adding the selected date in the edittext
                        tv_task_date.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        currentDate = year + "-" + month + "-" + dayOfMonth;
                        selectDate = tv_task_date.getText().toString();
                        showProgressBar();
                        final ApiInterface apiService = RetrofitClient.getService();
                        Call<ReportByUserDate> call = apiService.getReportByUserDate(userId, selectDate);
                        // Call<ReportByUserDate> call = apiService.getReportByUserDate("E-3023", "2021-08-26");
                        call.enqueue(new Callback<ReportByUserDate>() {
                            @Override
                            public void onResponse(Call<ReportByUserDate> call, retrofit2.Response<ReportByUserDate> response) {
                                if (response.code() == 200 && response.body() != null) {
                                    hideProgressBar();
                                    // ReportByUserDate report = response.body();
                                    ReportByUserDate report = response.body();

                                    if (report.getResponseCode().equalsIgnoreCase("0")) {
                                        Toast.makeText(ReportActivity.this, "" + report.getResponseMessage().toString(), Toast.LENGTH_SHORT).show();
                                        llUserReports.setVisibility(View.GONE);

                                    } else {
                                        llUserReports.setVisibility(View.VISIBLE);
                                        List<InstallationCountUserdate> list = report.getInstallationCountUserdate();
                                        Log.d("ReportByUserDate", response.toString());
                                        empId.setText(report.getDutyStartData().getEmployeeId());
                                        startReading.setText(report.getDutyStartData().getStartReading());
                                        endReading.setText(report.getDutyStartData().getEndReading());
                                        vehicleRegNum.setText(report.getDutyStartData().getVehicleRegistrationNo());
                                        startLoc.setText(report.getDutyStartData().getStartLocation());
                                        endLoc.setText(report.getDutyStartData().getEndLocation());
                                        vehicleDeatil.setText(report.getDutyStartData().getVehicleDetails());
                                        txtScheDate.setText(report.getDutyStartData().getScheduleDate());
                                        Glide.with(ReportActivity.this).load(urlStartImage + report.getDutyStartData().getStartImage()).into(imgStart);
                                        Glide.with(ReportActivity.this).load(urlEndImage + report.getDutyStartData().getEndImage()).into(imgEnd);
                                       // fullSizeStartImage(urlStartImage + report.getDutyStartData().getStartImage());
                                        for (int i = 0; i < list.size(); i++) {
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(ReportActivity.this, LinearLayoutManager.VERTICAL, false);
                                            rcvByUserData.setLayoutManager(layoutManager);
                                            ReportsByUserAdapter reports = new ReportsByUserAdapter(list, ReportActivity.this);
                                            rcvByUserData.setAdapter(reports);
                                        }
                                    }


                                } else {
                                    Toast.makeText(ReportActivity.this, response.body().getResponseMessage().toString(), Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<ReportByUserDate> call, Throwable t) {
                                Toast.makeText(ReportActivity.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                }, year, month, day);
                datePicker.getDatePicker().setMinDate(prev);
                datePicker.getDatePicker().setMaxDate(current);
                // show the dialog
                datePicker.show();
            }
        });
    }

    private void locinitViews() {

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        loadLeaveList();
    }

    public void getReports() {
        showProgressBar();

        final ApiInterface apiService = RetrofitClient.getService();
        Call<ReportByUserDate> call = apiService.getReportByUserDate(userId, currentDate);
        // Call<ReportByUserDate> call = apiService.getReportByUserDate("E-3023", "2021-08-26");
        call.enqueue(new Callback<ReportByUserDate>() {
            @Override
            public void onResponse(Call<ReportByUserDate> call, retrofit2.Response<ReportByUserDate> response) {
                if (response.code() == 200 && response.body() != null) {
                    hideProgressBar();
                    ReportByUserDate report = response.body();
                    if (report.getResponseCode().equalsIgnoreCase("0")) {
                        Toast.makeText(ReportActivity.this, "" + report.getResponseMessage().toString(), Toast.LENGTH_SHORT).show();
                        llUserReports.setVisibility(View.GONE);

                    } else {
                        llUserReports.setVisibility(View.VISIBLE);
                        List<InstallationCountUserdate> list = report.getInstallationCountUserdate();
                        Log.d("URL_ReportByUserDate", response.toString());
                        empId.setText(report.getDutyStartData().getEmployeeId());
                        startReading.setText(report.getDutyStartData().getStartReading());
                        endReading.setText(report.getDutyStartData().getEndReading());
                        vehicleRegNum.setText(report.getDutyStartData().getVehicleRegistrationNo());
                        startLoc.setText(report.getDutyStartData().getStartLocation());
                        endLoc.setText(report.getDutyStartData().getEndLocation());
                        vehicleDeatil.setText(report.getDutyStartData().getVehicleDetails());
                        txtScheDate.setText(report.getDutyStartData().getScheduleDate());
                        Glide.with(ReportActivity.this).load(urlStartImage + report.getDutyStartData().getStartImage()).into(imgStart);
                        Glide.with(ReportActivity.this).load(urlEndImage + report.getDutyStartData().getEndImage()).into(imgEnd);
                        //fullSizeStartImage(urlStartImage + report.getDutyStartData().getStartImage());
                        for (int i = 0; i < list.size(); i++) {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(ReportActivity.this, LinearLayoutManager.VERTICAL, false);
                            rcvByUserData.setLayoutManager(layoutManager);
                            ReportsByUserAdapter reports = new ReportsByUserAdapter(list, ReportActivity.this);
                            rcvByUserData.setAdapter(reports);

                        }
                    }


                } else {
                    Toast.makeText(ReportActivity.this, response.body().getResponseMessage().toString(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ReportByUserDate> call, Throwable t) {
                Toast.makeText(ReportActivity.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadLeaveList() {
        showProgressBar();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = BaseUrlActivity.urlmain + "getmeterdetailbyuserday";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    hideProgressBar();
                    JSONObject obj = new JSONObject(response);
                    Log.e("Responce", obj.toString());
                    String success = obj.getString("ResponseCode");
                    String respmessage = obj.getString("ResponseMessage");
                    Log.i("Resp1", success);
                    if (success.equals("200")) {
                        JSONArray heroArray = obj.getJSONArray("Response");
                        for (int i = 0; i < heroArray.length(); i++) {
                            JSONObject heroObject = heroArray.getJSONObject(i);
                            String daily_schedule_id = heroObject.getString("daily_schedule_id");
                            String vehicle_id = heroObject.getString("vehicle_id");
                            String meter_no = heroObject.getString("meter_no");
                            String protocol_no = heroObject.getString("protocol_no");
                            String consumer_no = heroObject.getString("consumer_no");
                            String installation_type_id = heroObject.getString("installation_type_id");
                            String installation_date = heroObject.getString("installation_date");
                            String latitude = heroObject.getString("latitude");
                            String longitude = heroObject.getString("longitude");
                            String location = heroObject.getString("location");
                            String meter_image = heroObject.getString("meter_image");
                            String protocol_image = heroObject.getString("protocol_image");
                            String userid = heroObject.getString("userid");

                            ReportModel homeVersion = new ReportModel();
                            homeVersion.setDaily_schedule_id(daily_schedule_id);
                            homeVersion.setVehicle_id(vehicle_id);
                            homeVersion.setMeter_no(meter_no);
                            homeVersion.setProtocol_no(protocol_no);
                            homeVersion.setConsumer_no(consumer_no);
                            homeVersion.setInstallation_type_id(installation_type_id);
                            homeVersion.setInstallation_date(installation_date);
                            homeVersion.setLatitude(latitude);
                            homeVersion.setLongitude(longitude);
                            homeVersion.setLocation(location);
                            homeVersion.setMeter_image(meter_image);
                            homeVersion.setProtocol_image(protocol_image);
                            homeVersion.setUserid(userid);
                            rarray.add(homeVersion);
                            rAdapter = new ReportAdapter(rarray, ReportActivity.this);
                            recyclerView.setAdapter(rAdapter);
                        }


                    } else if (success.equals("0")) {
                        rarray.clear();
                    } else {
                        Toast.makeText(getApplicationContext(), respmessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.toString());
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("users_id", sharedPresence.getEmpId(getApplicationContext()));
                // params.put("users_id", "E-2626");
                params.put("sel_date", tdf.format(new Date()));
                // params.put("sel_date", "2020-07-02");
                return params;
            }
        };
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void fullSizeStartImage(String photoUrl) {
        Picasso.setSingletonInstance(new Picasso.Builder(this).build());
        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setBackgroundColor(Color.BLACK);
        imagePopup.setFullScreen(false);
        imagePopup.setHideCloseIcon(true);
        imagePopup.setImageOnClickClose(true);
        //final String photoUrl = "http://1.6.10.121/meter/tpdil_api/uploads/start_duty/s20230504060332.jpg";
        Picasso.get().load(photoUrl).into(imgStart);
        imagePopup.initiatePopupWithPicasso(photoUrl);
        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePopup.viewPopup();
            }
        });
    }


}