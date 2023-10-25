package com.rjdev.meterinstallapp.activity;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static com.rjdev.meterinstallapp.Utils.Myapp.getContext;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.rjdev.meterinstallapp.BuildConfig;
import com.rjdev.meterinstallapp.DBHelper.DatabaseHelper;
import com.rjdev.meterinstallapp.DBHelper.MeterDatabaseHelper;
import com.rjdev.meterinstallapp.Model.EndDuty;
import com.rjdev.meterinstallapp.Model.InstallDetailModel;
import com.rjdev.meterinstallapp.Model.MeterModel;
import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.Utils.Connectivity;
import com.rjdev.meterinstallapp.Utils.SharedPresences;
import com.rjdev.meterinstallapp.interfaces.ApiInterface;
import com.rjdev.meterinstallapp.interfaces.ImageNavigator;
import com.rjdev.meterinstallapp.server.RetrofitClient;
import com.rjdev.meterinstallapp.viewmodel.ImageViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Duty_End_Activity extends AppCompatActivity implements ImageNavigator {
    EditText ed_protocolno, ed_customerno, mark;
    TextView tv_address, ed_cordinate;
    TextView ed_installationdate;
    Spinner ed_installationtype;
    EditText ed_meterno;
    private Uri filePath;
    private Uri photoURI;
    String[] spinneritems = {"Select", "NSC", "REP DEF", "REP NM", "REP EM", "Other"};
    Button btsubmit;
    ImageView back;
    SimpleDateFormat tdf;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 5;
    String str_latitude, str_longitude, str_address;
    ImageView img_meter, img_protocol;
    File imageFile;
    private ImageViewModel viewModel;

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(GPS_PROVIDER),
            new LocationListener(NETWORK_PROVIDER)
    };
    RelativeLayout rl_addimgprotocol, rl_addimgmeter;
    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    };
    String permissionsDenied = "";
    private static final int CAMERA_REQUEST = 1888;
    String selcectimg = "None";
    String strbitmeterimg, strbitprotocolimg;
    String str_day;
    ArrayList<String> meterdetail;
    LinearLayout ll_main_layout;
    SharedPresences sharedPresence;
    String todayFormat = "yyyy-MM-dd";


    Date dt = new Date();
    String time;
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPresence = new SharedPresences(getApplicationContext());
        setContentView(R.layout.activity_duty_end);
        //current time
        time = sdf.format(dt);

        viewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        viewModel.setNavigator(this);

        ed_cordinate = findViewById(R.id.ed_cordinate);
        mark = findViewById(R.id.rremarks);
        ed_installationtype = findViewById(R.id.ed_installationtype);
        ed_installationdate = findViewById(R.id.ed_installationdate);
        ed_protocolno = findViewById(R.id.ed_protocolno);
        ed_meterno = findViewById(R.id.ed_meterno);
        ed_customerno = findViewById(R.id.ed_customerno);
        btsubmit = findViewById(R.id.btsubmit);
        tv_address = findViewById(R.id.tv_address);
        ll_main_layout = findViewById(R.id.ll_main_layout);
        rl_addimgmeter = findViewById(R.id.rl_addimgmeter);
        rl_addimgprotocol = findViewById(R.id.rl_addimgprotocol);
        img_protocol = findViewById(R.id.img_protocol);
        img_meter = findViewById(R.id.img_meter);
        back = findViewById(R.id.back);
        meterdetail = new ArrayList<>();
        tdf = new SimpleDateFormat(todayFormat, Locale.US);
        sharedPresence = new SharedPresences(getApplicationContext());
        initializeLocationManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissions();
        }
        try {
            mLocationManager.requestLocationUpdates(
                    NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        // loadmeterno();
        SimpleDateFormat dsdf = new SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault());
        //SimpleDateFormat dsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        str_day = dsdf.format(new Date());
        ed_installationdate.setText(str_day);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinneritems);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ed_installationtype.setAdapter(aa);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Connectivity.isConnected(getApplicationContext())) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());

                    InstallDetailModel contact = new InstallDetailModel(
                            ed_customerno.getText().toString(),
                            ed_meterno.getText().toString(),
                            ed_protocolno.getText().toString(),
                            ed_installationdate.getText().toString(),
                            ed_installationtype.getSelectedItem().toString(),
                            ed_cordinate.getText().toString(),
                            tv_address.getText().toString(),
                            strbitmeterimg,
                            strbitprotocolimg);
                    if (databaseHelper.addDetails(contact)) {
                        Toast.makeText(getApplicationContext(), "Details Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error Saving", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (imageFile != null)
                        uploadBitmap(imageFile);
                    else
                        Toast.makeText(Duty_End_Activity.this, "Please upload image!", Toast.LENGTH_LONG).show();
                }

            }
        });
        rl_addimgmeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selcectimg = "Meter";
                /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                capturePhoto();
            }
        });
        rl_addimgprotocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selcectimg = "Protocol";
                /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                capturePhoto();

            }
        });


    }

    private void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images.jpeg");
        photoURI = FileProvider.getUriForFile(this, getContext().getApplicationContext().getPackageName() + ".fileprovider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {


            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

                File cachePath = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images.jpeg");
                //  filePath = Uri.parse(photoURI);
                filePath = Uri.parse(String.valueOf(photoURI));
//                filePath = FileProvider.getUriForFile(Duty_End_Activity.this,
//                        BuildConfig.APPLICATION_ID + ".provider", cachePath);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                } catch (Exception e) {
                }
                if (bitmap != null) {
                    File f = new File(cachePath.getAbsolutePath());
                    viewModel.compressImage(this, f.getParent(), f.getName().replaceFirst("[.][^.]+$", ""), f, 0);
                    img_meter.setImageBitmap(bitmap);
                    // api calls
                    //sendImage(f);
                }
            }
        }
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void fileCompressed(File imageFile, int imageType) {
        this.imageFile = imageFile;
    }

    class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            str_latitude = Double.toString(location.getLatitude());
            str_longitude = Double.toString(location.getLongitude());
            Log.i("Localityyy", str_latitude + " " + str_longitude);


            try {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                str_address = addresses.get(0).getAddressLine(0) + ", " +
                        addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
                Log.i("Localityyy1", str_address);
                str_address = str_address.replaceAll(", null", "");
                Log.i("Localityyy2", str_address);
                ed_cordinate.setText(str_latitude + " " + str_longitude);
                tv_address.setText("Time : "+time +",  "+ str_address);

            } catch (Exception e) {
                Log.e(TAG, "onNetworkChange: " + e.getMessage());
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            if (!isFinishing()) {
                // showSettingsAlert(); this method is allow two time permission

                //allow location permission one time
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                Toast.makeText(Duty_End_Activity.this, "Please Allow Location", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Duty_End_Activity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions) {
                result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                return false;
            }
            return true;
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissionsList[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionsList, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {

                    for (String per : permissionsList) {
                        for (int i = 0; i < permissionsList.length; i++) {
                            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                                permissionsDenied = "\n" + per;

                            }
                        }

                    }

                    // Show permissionsDenied

                }


            }
            Log.i("getpermission", permissionsDenied);


        }
    }

    public void loadmeterno() {
        MeterDatabaseHelper databaseHelper = new MeterDatabaseHelper(getApplicationContext());
        Cursor cursor = databaseHelper.getDetailActive(new MeterModel("", "", "1"));

        //iterate through all the rows contained in the database
        if (!cursor.moveToNext()) {
            Toast.makeText(getApplicationContext(), "There are no data to show", Toast.LENGTH_SHORT).show();
        }

        try {


            while (cursor.isAfterLast() == false) {
                //  meterdetail.add(cursor.getString(1));
                meterdetail.add("33");
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.i("LOGGGG11", e.toString());
        } finally {
            cursor.close();
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, meterdetail);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // ed_meterno.setAdapter(adapter);
    }

    private void uploadBitmap(File f) {
        final ProgressDialog progressDialog = new ProgressDialog(Duty_End_Activity.this);
        progressDialog.show();
        progressDialog.setMessage("Please Wait.... ");
        String imagename = sharedPresence.getUserId(getApplicationContext()) + tdf.format(new Date()) + ed_meterno.getText().toString();
        String endName = f.getAbsolutePath().replace(f.getName(), "" + "Endimage_" + imagename + ".jpeg");
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), f);

        MultipartBody.Part body = MultipartBody.Part.createFormData("end_image", endName, requestFile);
        RequestBody end_reading = RequestBody.create(MediaType.parse("text/plain"), mark.getText().toString());
        RequestBody end_latitude = RequestBody.create(MediaType.parse("text/plain"), str_latitude);
        RequestBody end_longitude = RequestBody.create(MediaType.parse("text/plain"), str_longitude);
        RequestBody end_location = RequestBody.create(MediaType.parse("text/plain"), tv_address.getText().toString());
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getEmpId(getApplicationContext()));

        final ApiInterface apiService = RetrofitClient.getService();
        Call<EndDuty> call = apiService.updateEndDuty(body, end_reading, end_latitude, end_longitude, end_location, userid);

        call.enqueue(new Callback<EndDuty>() {
            @Override
            public void onResponse(Call<EndDuty> call, retrofit2.Response<EndDuty> response) {
                if (response.code() ==200 && response.body()!=null){
                    if (response.body().getResponseCode().equalsIgnoreCase("200")){
                        progressDialog.dismiss();
                        Toast.makeText(Duty_End_Activity.this, "" + response.body().getResponseMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(Duty_End_Activity.this, "" + response.body().getResponseMessage(), Toast.LENGTH_SHORT).show();

                    }
                }

            }

            @Override
            public void onFailure(Call<EndDuty> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("response", "error" + t);
            }
        });
    }
}

