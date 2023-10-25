package com.rjdev.meterinstallapp.activity;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.rjdev.meterinstallapp.DBHelper.DatabaseHelper;
import com.rjdev.meterinstallapp.DBHelper.MeterDatabaseHelper;
import com.rjdev.meterinstallapp.Model.AddMeter;
import com.rjdev.meterinstallapp.Model.InstallDetailModel;
import com.rjdev.meterinstallapp.Model.MeterModel;
import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.Utils.CommonMethods;
import com.rjdev.meterinstallapp.Utils.Connectivity;
import com.rjdev.meterinstallapp.Utils.PrefrenceKey;
import com.rjdev.meterinstallapp.Utils.SharedPresences;
import com.rjdev.meterinstallapp.interfaces.ApiInterface;
import com.rjdev.meterinstallapp.interfaces.ImageNavigator;
import com.rjdev.meterinstallapp.server.RetrofitClient;
import com.rjdev.meterinstallapp.viewmodel.ImageViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Others_Users extends BaseActivity implements ImageNavigator {
    EditText ed_protocolno, ed_customerno, mark;
    TextView tv_address, ed_cordinate;
    TextView ed_installationdate;
    Spinner ed_installationtype;
    EditText ed_meterno;
    Bitmap bitimgmeter, bitimgprotocol;
    String[] spinneritems = {"Select", "NSC", "REP DEF", "REP NM", "REP EM", "Other"};
    Button btsubmit;
    ImageView back;
    SimpleDateFormat tdf;
    String instalation_type = "Others";
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 5;
    String str_latitude, str_longitude, str_address;
    ImageView img_meter, img_protocol;
    String groupId, groupTypeName, meterServiceTypeName, meterServiceTypeID;

    Others_Users.LocationListener[] mLocationListeners = new Others_Users.LocationListener[]{
            new Others_Users.LocationListener(LocationManager.GPS_PROVIDER),
            new Others_Users.LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    RelativeLayout rl_addimgprotocol, rl_addimgmeter;
    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    };
    String permissionsDenied = "";
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_REQUEST1 = 18888;
    String selcectimg = "None";
    String strbitmeterimg, strbitprotocolimg;
    String str_day;
    ArrayList<String> meterdetail;
    LinearLayout ll_main_layout;
    SharedPresences sharedPresence;
    String todayFormat = "yyyy-MM-dd";
    private ImageViewModel viewModel;
    private Uri filePath;
    private Uri filePath1;
    private Uri photoURI;
    private Uri photoURI1;
    File pImage, mImage;
    TextView tvOthers;
    String DailyScheduleId, installationI_Id;
    String daily_scheduleId, groupType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others__users);
        daily_scheduleId = CommonMethods.getPrefsData(Others_Users.this, PrefrenceKey.DailyScheduleId, "");
        groupId = CommonMethods.getPrefsData(Others_Users.this, PrefrenceKey.GroupId, "");
        groupType = CommonMethods.getPrefsData(Others_Users.this, PrefrenceKey.GroupType, "");
        groupTypeName = CommonMethods.getPrefsData(Others_Users.this, PrefrenceKey.GroupType, "");
        meterServiceTypeID = CommonMethods.getPrefsData(Others_Users.this, PrefrenceKey.meterService_TypeID, "");
        meterServiceTypeName = CommonMethods.getPrefsData(Others_Users.this, PrefrenceKey.meterService_TypeName, "");
        System.out.println("DailyScheduleId: " + DailyScheduleId + " groupId: " + groupId + " groupTypeName: " + groupTypeName + " installationI_Id: " + installationI_Id
                + " meter_service_type_name :" + meterServiceTypeName);
        viewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        viewModel.setNavigator(this);
        ed_cordinate = findViewById(R.id.ed_cordinate);
        mark = findViewById(R.id.rremarks);
        ed_installationtype = findViewById(R.id.ed_installationtype);
        back = findViewById(R.id.back);
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
        tvOthers = findViewById(R.id.tvOthers);
        tvOthers.setText(meterServiceTypeName);
        meterdetail = new ArrayList<>();
        tdf = new SimpleDateFormat(todayFormat, Locale.US);
        sharedPresence = new SharedPresences(getApplicationContext());
        initializeLocationManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissions();
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
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

        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ed_protocolno.getText().toString().isEmpty()) {
                    Toast.makeText(Others_Users.this, "Please Fill All field", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ed_customerno.getText().toString().isEmpty()) {
                    Toast.makeText(Others_Users.this, "Please Fill All field", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mark.getText().toString().isEmpty()) {
                    Toast.makeText(Others_Users.this, "Please Fill All field", Toast.LENGTH_SHORT).show();
                    return;
                }


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
                    if (mImage == null) {
                        Toast.makeText(Others_Users.this, "Please upload meter image!", Toast.LENGTH_LONG).show();
                    } else if (pImage == null) {
                        Toast.makeText(Others_Users.this, "Please upload protocol image!", Toast.LENGTH_LONG).show();
                    }
                    else if (ed_customerno.length() < 12) {
                        Toast.makeText(Others_Users.this, "Please enter valid 12 digit Customer No", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        uploadBitmap();
                    }
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
               /* Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                capturePhoto();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddMeterDetailsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), AddMeterDetailsActivity.class);
        startActivity(intent);
        finish();
    }

    private void capturePhoto() {
        if (selcectimg.matches("Meter")) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images.jpeg");
            photoURI = FileProvider.getUriForFile(this, getContext().getApplicationContext().getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, CAMERA_REQUEST);
        } else if (selcectimg.matches("Protocol")) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file1 = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "images.jpeg");
            photoURI1 = FileProvider.getUriForFile(this, getContext().getApplicationContext().getPackageName() + ".fileprovider", file1);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI1);
            startActivityForResult(intent, CAMERA_REQUEST1);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

        }
        switch (requestCode) {
            case CAMERA_REQUEST:

                if (resultCode == RESULT_OK) {

                    File cachePath = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images.jpeg");
                    filePath = Uri.parse(String.valueOf(photoURI));
//                    filePath = FileProvider.getUriForFile(Others_Users.this,
//                            BuildConfig.APPLICATION_ID + ".provider", cachePath);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    } catch (Exception e) {
                    }
                    if (bitmap != null) {
                        File f = new File(cachePath.getAbsolutePath());
                        viewModel.compressImage(this, f.getParent(), f.getName().replaceFirst("[.][^.]+$", ""), f, 0);
                        img_meter.setImageBitmap(bitmap);
                    }
                }
                break;

            case CAMERA_REQUEST1:

                File cachePath1 = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "images.jpeg");
                filePath = Uri.parse(String.valueOf(photoURI1));
//                filePath1 = FileProvider.getUriForFile(Others_Users.this,
//                        BuildConfig.APPLICATION_ID + ".provider", cachePath1);
                Bitmap bitmap1 = null;
                try {
                    bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                } catch (Exception e) {
                }
                if (bitmap1 != null) {
                    File f = new File(cachePath1.getAbsolutePath());
                    viewModel.compressImage(this, f.getParent(), f.getName().replaceFirst("[.][^.]+$", ""), f, 0);
                    img_protocol.setImageBitmap(bitmap1);
                }
                break;


//            Bitmap mphoto = (Bitmap) data.getExtras().get("data");
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            mphoto.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//            String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
//
//            Log.i("All is well", imageEncoded);
//            Bitmap newbit = StringToBitMap(imageEncoded);
//            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
//            newbit.compress(Bitmap.CompressFormat.JPEG, 50, baos1);
//            String imageEncoded1 = Base64.encodeToString(baos1.toByteArray(), Base64.DEFAULT);
//            Log.i("How ti return",imageEncoded1);

           /* Bitmap mphoto = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // change
            mphoto.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            if (selcectimg.matches("Meter")) {
                bitimgmeter = mphoto;
                strbitmeterimg = imageEncoded;
                img_meter.setImageBitmap(bitimgmeter);
            } else if (selcectimg.matches("Protocol")) {
                bitimgprotocol = mphoto;
                strbitprotocolimg = imageEncoded;
                img_protocol.setImageBitmap(bitimgprotocol);
            } else {

            }

            Log.i("All is well", imageEncoded);
        }*/
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
        if (selcectimg.matches("Meter")) {
            this.mImage = imageFile;
        } else if (selcectimg.matches("Protocol")) {
            this.pImage = imageFile;
        }
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
                tv_address.setText(str_address);


            } catch (Exception e) {
                Log.e(TAG, "onNetworkChange: " + e.getMessage());
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            if (!isFinishing()) {
                showSettingsAlert();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Others_Users.this);

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


    private void uploadBitmap() {
        final ProgressDialog progressDialog = new ProgressDialog(Others_Users.this);
        progressDialog.show();
        progressDialog.setMessage("Please Wait.... ");

        //unique name of image changed here.
        String imagename = sharedPresence.getUserId(getApplicationContext()) + tdf.format(new Date()) + ed_meterno.getText().toString();

        String meterName = mImage.getAbsolutePath().replace(mImage.getName(), "" + "Meter_" + imagename + ".jpeg");
        String protocolName = pImage.getAbsolutePath().replace(pImage.getName(), "" + "Protocol_" + imagename + ".jpeg");
        RequestBody metrerFile = RequestBody.create(MediaType.parse("image/*"), mImage);
        MultipartBody.Part meterimage = MultipartBody.Part.createFormData("photo", meterName, metrerFile);
        RequestBody protocolFile = RequestBody.create(MediaType.parse("image/*"), pImage);
        MultipartBody.Part protocolimage = MultipartBody.Part.createFormData("proto_photo", protocolName, protocolFile);
        RequestBody daily_schedule_id = RequestBody.create(MediaType.parse("text/plain"), daily_scheduleId);
        RequestBody meter_service_type_name = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeName);
        RequestBody meter_service_type_id = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeID);
        RequestBody group_id = RequestBody.create(MediaType.parse("text/plain"), groupId);
        RequestBody group_type = RequestBody.create(MediaType.parse("text/plain"), groupType);
        RequestBody group_Type_Name = RequestBody.create(MediaType.parse("text/plain"), groupTypeName);
        RequestBody installation_type_id = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeID);
        RequestBody vehicle_id = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getUserDesignation(getApplicationContext()));
        RequestBody meter_no = RequestBody.create(MediaType.parse("text/plain"), ed_meterno.getText().toString());
        RequestBody protocol_no = RequestBody.create(MediaType.parse("text/plain"), ed_protocolno.getText().toString());
        RequestBody consumer_no = RequestBody.create(MediaType.parse("text/plain"), ed_customerno.getText().toString());
        RequestBody remark = RequestBody.create(MediaType.parse("text/plain"), mark.getText().toString());
        RequestBody installation_date = RequestBody.create(MediaType.parse("text/plain"), ed_installationdate.getText().toString());
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), str_longitude);
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), str_latitude);
        RequestBody location = RequestBody.create(MediaType.parse("text/plain"), tv_address.getText().toString());
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getEmpId(getApplicationContext()));
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("meter_service_type_id", meter_service_type_id);
        map.put("meter_service_type_name", meter_service_type_name);
        map.put("daily_schedule_id", daily_schedule_id);
        map.put("group_id", group_id);
        map.put("group_type", group_type);
        map.put("group_Type_Name", group_Type_Name);
        map.put("installation_type_id", installation_type_id);
        map.put("vehicle_id", vehicle_id);
        map.put("meter_no", meter_no);
        map.put("protocol_no", protocol_no);
        map.put("consumer_no", consumer_no);
        map.put("remark", remark);
        map.put("installation_date", installation_date);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("location", location);
        map.put("userid", userid);

        final ApiInterface apiService = RetrofitClient.getService();
        Call<AddMeter> call = apiService.updateMeterDetailOthers(meterimage, protocolimage, map);
        call.enqueue(new Callback<AddMeter>() {
            @Override
            public void onResponse(Call<AddMeter> call, retrofit2.Response<AddMeter> response) {
                Log.e("response", response.body().getResponseMessage());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    AddMeter addMeter = response.body();
                    Toast.makeText(Others_Users.this, "" + addMeter.getResponseMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Others_Users.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();


                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Others_Users.this, "Error", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<AddMeter> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("response", "error" + t);
            }
        });

    }

    /*private void uploadBitmap() {
        final ProgressDialog progressDialog = new ProgressDialog(Others_Users.this);
        progressDialog.show();
        progressDialog.setMessage("Please wait...");

       // showProgressBar();
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
                                progressDialog.dismiss();

                               // hideProgressBar();
                                Snackbar snackbar1 = Snackbar.make(ll_main_layout, respmessage, Snackbar.LENGTH_LONG);
                                snackbar1.show();
                                Intent intent = new Intent(Others_Users.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                            //    hideProgressBar();
                                progressDialog.dismiss();
                                Snackbar snackbar1 = Snackbar.make(ll_main_layout, respmessage, Snackbar.LENGTH_LONG);
                                snackbar1.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(Others_Users.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // hideProgressBar();
                        progressDialog.dismiss();
                        //  Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            *//*
     * If you want to add more parameters with the image
     * you can do it here
     * here we have only one parameter with the image
     * which is tags
     * *//*
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("daily_schedule_id", "2");
                params.put("vehicle_id", "1");
                params.put("meter_no", ed_meterno.getText().toString());
                params.put("protocol_no", ed_protocolno.getText().toString());
                params.put("consumer_no", ed_customerno.getText().toString());
                //  params.put("installation_type_id", ed_installationtype.getSelectedItem().toString());
                params.put("installation_date", ed_installationdate.getText().toString());
              //  params.put("meter_make", metermark.getText().toString());
                params.put("remark", mark.getText().toString());
                params.put("installation_type", instalation_type);
                params.put("latitude", str_latitude);
                params.put("longitude", str_longitude);
                params.put("location", tv_address.getText().toString());
                params.put("userid", sharedPresence.getEmpId(getApplicationContext()));

                return params;
            }


            *//*
     * Here we are passing image by renaming it with a unique name
     * *//*
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                String  imagename = sharedPresence.getUserId(getApplicationContext())+tdf.format(new Date())+ed_meterno.getText().toString() ;
                params.put("photo", new VolleyMultipartRequest.DataPart(  "Meter_" + imagename + ".png", getFileDataFromDrawable(bitimgmeter)));
                params.put("proto_photo", new VolleyMultipartRequest.DataPart(  "Protocol_" + imagename + ".png", getFileDataFromDrawable(bitimgprotocol)));
                return params;
            }
        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }*/

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.nik, menu);
    //    return true;
    // }

    // @Override
    //   public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    //   int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    //   if (id == R.id.action_privacy) {
    //      Intent intent=new Intent(Intent.ACTION_SEND);
    //     String[] recipients={"a.shyamindus@gmail.com"};
    //    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
    //    intent.putExtra(Intent.EXTRA_SUBJECT,"APP ISSUE");
    ////    intent.setType("text/html");
    //    startActivity(Intent.createChooser(intent, "Send mail"));
    //    return true;

    // }

    // if (id == R.id.action_settings) {

    //sessionManager.logoutUser();
    //  presencesUtility.logout();
    //    Intent i = new Intent(getApplication(), LoginActivity.class);
    //    startActivity(i);
    //   finish();
    //    return true;
    // }

    //   return super.onOptionsItemSelected(item);
    // }


}
