package com.rjdev.meterinstallapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.Utils.BaseUrlActivity;
import com.rjdev.meterinstallapp.Utils.CommonMethods;
import com.rjdev.meterinstallapp.Utils.Connectivity;
import com.rjdev.meterinstallapp.Utils.PrefrenceKey;
import com.rjdev.meterinstallapp.Utils.SessionManager;
import com.rjdev.meterinstallapp.Utils.SharedPresences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    Button bt_login;
    LinearLayout ll_main_layout;
    EditText ed_empid, ed_emppass;
    String IMEI;
    SharedPresences sharedPresence;
    SessionManager sessionManager;
    String fcmToken = " ";
    CheckBox checkbox_rem, checkbox_tpsodl, checkbox_tpcodl,checkbox_tpnodl;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bt_login = findViewById(R.id.bt_login);
        ll_main_layout = findViewById(R.id.ll_main_layout);
        ed_empid = findViewById(R.id.ed_empid);
        ed_emppass = findViewById(R.id.ed_emppass);
        checkbox_rem = findViewById(R.id.checkbox_rem);
        checkbox_tpsodl = findViewById(R.id.checkbox_tpsodl);
        checkbox_tpcodl = findViewById(R.id.checkbox_tpcodl);
        checkbox_tpnodl = findViewById(R.id.checkbox_tpnodl);
        sessionManager = new SessionManager(getApplicationContext());
        sharedPresence = new SharedPresences(getApplicationContext());
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String empid = sharedPreferences.getString("empid", "");
        String passwords = sharedPreferences.getString("passowrd", "");

        ed_empid.setText(empid);
        ed_emppass.setText(passwords);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            IMEI = telephonyManager.getDeviceId();
            if (!IMEI.isEmpty()) {
                IMEI = telephonyManager.getDeviceId();
            } else {
                IMEI = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);

            }
        } catch (Exception e) {
            IMEI = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);

        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                fcmToken = instanceIdResult.getToken();
                Log.i("FID123", fcmToken);
            }
        });

        checkbox_tpcodl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // show password
                    group = "TPCODL";
                    editor.putString("group", group);
                    checkbox_tpsodl.setChecked(false);
                    checkbox_tpnodl.setChecked(false);
                } else {
                    // hide password
                }
            }
        });

        checkbox_tpsodl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // show password
                    group = "TPSODL";
                    editor.putString("group", group);
                    checkbox_tpcodl.setChecked(false);
                    checkbox_tpnodl.setChecked(false);
                } else {
                    // hide password
                }
            }
        });

        checkbox_tpnodl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // show password
                    group = "TPNODL";
                    editor.putString("group", group);
                    checkbox_tpcodl.setChecked(false);
                    checkbox_tpsodl.setChecked(false);
                } else {
                    // hide password
                }
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_rem.isChecked()) {
                    editor.putString("empid", ed_empid.getText().toString());
                    editor.putString("passowrd", ed_emppass.getText().toString());
                    editor.commit();
                } else {
                    editor.putString("empid", "");
                    editor.putString("passowrd", "");
                    editor.commit();
                }
                if (ed_empid.getText().toString().isEmpty()) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter employee id", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                }
                if (ed_empid.getText().toString().length() < 6) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter valid id", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (ed_emppass.getText().toString().isEmpty()) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter password", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (ed_emppass.getText().toString().length() < 2) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter valid passwoord", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (!checkbox_tpsodl.isChecked() && !checkbox_tpcodl.isChecked() && !checkbox_tpnodl.isChecked()) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please check group", Snackbar.LENGTH_LONG);
                    snackbar1.show();

                } else if (!Connectivity.isConnected(getApplicationContext())) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "No Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else {
                    loginuser();
                    CommonMethods.setPrefsData(LoginActivity.this, PrefrenceKey.GroupType,group);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void loginuser() {
        showProgressBar();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = BaseUrlActivity.urlmain + "loginUser";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
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

                                JSONObject heroObject = obj.getJSONObject("User_data");
                                String user_id = heroObject.getString("user_id");
                                String emp_id = heroObject.getString("emp_id");
                                String vehicle_id = heroObject.getString("vehicle_id");
                                String group_id = heroObject.getString("group_id");
                                sessionManager.createLoginSession(user_id, emp_id);
                                sharedPresence.setEmpId(getApplicationContext(), emp_id);
                                // sharedPresence.setVhicleId(getApplicationContext(),vehicle_id);
                                sharedPresence.setUserName(getApplicationContext(), group_id);
                                sharedPresence.setUserDesignation(getApplicationContext(), vehicle_id);
                                //sharedPresence.setDutyinId(getApplicationContext(),"vehicle_id");
                                // sharedPresence.setUserName(getApplicationContext(),"");
                                // sharedPresence.setUserDesignation(getApplicationContext(),"vehicle_id");
                                sharedPresence.setIMEI(getApplicationContext(), fcmToken);
                                CommonMethods.setPrefsData(LoginActivity.this, PrefrenceKey.GroupId,group_id);

                               // System.out.println("UserLogin"+user_id+ " emp_id "+emp_id+ " vehicle_id "+vehicle_id+ " group_id"+group_id);
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else if (success.equals("0")) {
                                hideProgressBar();
                                Toast toast = Toast.makeText(getApplicationContext(), respmessage, Toast.LENGTH_LONG);
                                toast.show();
                                Snackbar snackbar1 = Snackbar.make(ll_main_layout, respmessage, Snackbar.LENGTH_LONG);
                                snackbar1.show();

                            } else {
                                hideProgressBar();
                                Toast toast = Toast.makeText(getApplicationContext(), respmessage, Toast.LENGTH_LONG);
                                toast.show();
                                Snackbar snackbar1 = Snackbar.make(ll_main_layout, respmessage, Snackbar.LENGTH_LONG);
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
                        // error
                        hideProgressBar();
                        Log.d("Error.Response", error.toString());
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("IMEI_NO", IMEI);
                params.put("EMP_ID", ed_empid.getText().toString().trim());
                params.put("FCM_ID", fcmToken);
                params.put("EMP_PASSWORD", ed_emppass.getText().toString().trim());
//                params.put("IMEI_NO",IMEI);
//                params.put("EMP_ID",ed_empid.getText().toString().trim());
//                params.put("FCM_ID",fcmToken);
//                params.put("EMP_PASSWORD",ed_emppass.getText().toString().trim());

                return params;
            }
        };
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
}
