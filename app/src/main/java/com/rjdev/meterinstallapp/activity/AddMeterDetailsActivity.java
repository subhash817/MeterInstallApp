package com.rjdev.meterinstallapp.activity;

import static com.rjdev.meterinstallapp.Utils.Myapp.getContext;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.rjdev.meterinstallapp.BuildConfig;
import com.rjdev.meterinstallapp.DBHelper.DatabaseHelper;
import com.rjdev.meterinstallapp.DBHelper.MeterDatabaseHelper;
import com.rjdev.meterinstallapp.Model.AddMeter;
import com.rjdev.meterinstallapp.Model.InstallDetailModel;
import com.rjdev.meterinstallapp.Model.MeterModel;
import com.rjdev.meterinstallapp.Model.MeterServiceType;
import com.rjdev.meterinstallapp.Model.MeterServiceTypeData;
import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.Utils.CommonMethods;
import com.rjdev.meterinstallapp.Utils.Connectivity;
import com.rjdev.meterinstallapp.Utils.PrefrenceKey;
import com.rjdev.meterinstallapp.Utils.SessionManager;
import com.rjdev.meterinstallapp.Utils.SharedPresences;
import com.rjdev.meterinstallapp.interfaces.ApiInterface;
import com.rjdev.meterinstallapp.interfaces.ImageNavigator;
import com.rjdev.meterinstallapp.server.RetrofitClient;
import com.rjdev.meterinstallapp.viewmodel.ImageViewModel;

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

public class AddMeterDetailsActivity extends BaseActivity implements ImageNavigator, AdapterView.OnItemSelectedListener {

    EditText ed_protocolno, ed_customerno, metermark;
    TextView othersclick, conecetions, cables, wioutcables;
    TextView tv_address, ed_cordinate;
    TextView ed_installationdate;
    Spinner ed_installationtype;
    EditText ed_meterno;
    Bitmap bitimgmeter, bitimgprotocol;
    String[] spinneritems = {"Select", "NSC", "REP DEF", "REP NM", "REP EM", "Other"};
    Button btsubmit;
    SimpleDateFormat tdf;
    // String status="1";
    String instalation_type = "";
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 5;
    String str_latitude, str_longitude, str_address;
    ImageView img_meter, img_protocol, img_old_meter, img_old_meter_replacement, img_new_meter_replacement, img_protocol_replacement;
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    RelativeLayout rl_addimgprotocol, rl_addimgmeter, rl_addImgOldMeter, rl_protocol_replacement, rl_old_meter_replacement, rl_new_meter_replacement;
    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    };
    String permissionsDenied = "";
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_REQUEST1 = 18888;
    private static final int CAMERA_REQUEST2 = 1888888;
    String selcectimg = "None";
    String strbitmeterimg, strbitprotocolimg;
    String str_day;
    ArrayList<String> meterdetail;
    LinearLayout ll_main_layout;
    SharedPresences sharedPresence;
    SessionManager sessionManager;
    String todayFormat = "yyyy-MM-dd";
    private ImageViewModel viewModel;
    private Uri filePath;
    private Uri filePath1;
    private Uri filePath2;
    private Uri photoURI;
    private Uri photoURI1;
    private Uri photoURI2;
    File pImage, mImage, oImage;
    ImageView back;
    final ApiInterface apiService = RetrofitClient.getService();
    private List<MeterServiceTypeData> meterServiceTypeDataList;
    private ArrayAdapter<MeterServiceTypeData> spinnerArrayAdapter;
    private Spinner spinner;
    String meterServiceTypeName, meterServiceTypeID;
    LinearLayout ll_old_meter, ll_protocol, ll_meter_protocol_image, ll_book_no, ll_replacement, ll_protocol_replacement, ll_old_meter_number;
    Boolean oldMeterImageFlag = false;
    Boolean dumpDataFlag = false;
    MultipartBody.Part oldMetreImage;
    EditText edBookNo, edOldMeterNo;
    String group;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String daily_scheduleId,groupId,groupType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meter_details);
        daily_scheduleId= CommonMethods.getPrefsData(AddMeterDetailsActivity.this, PrefrenceKey.DailyScheduleId,"");
        groupId= CommonMethods.getPrefsData(AddMeterDetailsActivity.this, PrefrenceKey.GroupId,"");
        groupType= CommonMethods.getPrefsData(AddMeterDetailsActivity.this, PrefrenceKey.GroupType,"");
        sharedPreferences=getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor=sharedPreferences.edit();
        group = sharedPreferences.getString("group","");
        viewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        viewModel.setNavigator(this);
        sessionManager = new SessionManager(getApplicationContext());
        ed_cordinate = findViewById(R.id.ed_cordinate);
        wioutcables = (TextView) findViewById(R.id.withoutcable);
        back = findViewById(R.id.back);
        ll_old_meter = findViewById(R.id.ll_old_meter);
        edOldMeterNo = findViewById(R.id.ed_old_meter_no);
        ll_old_meter_number = findViewById(R.id.ll_old_meter_number);
        ll_replacement = findViewById(R.id.ll_replacement);
        ll_protocol_replacement = findViewById(R.id.ll_protocol_replacement);
        ll_old_meter.setVisibility(View.GONE);
        ll_old_meter_number.setVisibility(View.GONE);
        ll_replacement.setVisibility(View.GONE);
        ll_protocol_replacement.setVisibility(View.GONE);
        wioutcables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                instalation_type = "Replacement without cables";
                inactived();
                wioutcables.setTextColor(getResources().getColorStateList(R.color.colorAccent));


                //  inactived();
                //  wioutcables.setTextColor(getResources().getColorStateList(R.color.colorPrimaryDark));

            }
        });

        cables = (TextView) findViewById(R.id.cable);
        cables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inactived();

                instalation_type = "Replacement with cables";
                cables.setTextColor(getResources().getColorStateList(R.color.colorAccent));
            }


        });
        conecetions = (TextView) findViewById(R.id.new_connection);
        conecetions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instalation_type = "NEW CONNECTION SERVICES";
                inactived();
                conecetions.setTextColor(getResources().getColorStateList(R.color.colorAccent));


            }
        });
        click();
        othersclick = (TextView) findViewById(R.id.others);
        othersclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Others_Users.class);
                startActivity(intent);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ed_installationtype = findViewById(R.id.ed_installationtype);
        metermark = findViewById(R.id.meter_mark);
        ed_installationdate = findViewById(R.id.ed_installationdate);
        edBookNo = findViewById(R.id.ed_book_no);
        ed_protocolno = findViewById(R.id.ed_protocolno);
        ed_meterno = findViewById(R.id.ed_meterno);
        ed_customerno = findViewById(R.id.ed_customerno);
        btsubmit = findViewById(R.id.btsubmit);
        tv_address = findViewById(R.id.tv_address);
        ll_main_layout = findViewById(R.id.ll_main_layout);
        rl_addimgmeter = findViewById(R.id.rl_addimgmeter);
        rl_addimgprotocol = findViewById(R.id.rl_addimgprotocol);
        img_old_meter_replacement = findViewById(R.id.img_old_meter_replacement);
        img_new_meter_replacement = findViewById(R.id.img_new_meter_replacement);
        img_protocol_replacement = findViewById(R.id.img_protocol_replacement);
        rl_protocol_replacement = findViewById(R.id.rl_protocol_replacement);
        rl_old_meter_replacement = findViewById(R.id.rl_old_meter_replacement);
        rl_new_meter_replacement = findViewById(R.id.rl_new_meter_replacement);
        rl_addImgOldMeter = findViewById(R.id.rl_addImgOldMeter);
        img_protocol = findViewById(R.id.img_protocol);
        img_meter = findViewById(R.id.img_meter);
        img_old_meter = findViewById(R.id.img_old_meter);
        ll_protocol = findViewById(R.id.ll_protocol);
        ll_book_no = findViewById(R.id.ll_book_no);
        ll_meter_protocol_image = findViewById(R.id.ll_meter_protocol_image);
        spinner = findViewById(R.id.spin_ltype);
        meterdetail = new ArrayList<>();
        tdf = new SimpleDateFormat(todayFormat, Locale.US);
        sharedPresence = new SharedPresences(getApplicationContext());
        spinner.setOnItemSelectedListener(this);
        initializeLocationManager();
        getMeterServiceTypeApi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissions();
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
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

        if (group.matches("TPSODL")){
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(12); // Set max length to 10 characters
            ed_customerno.setFilters(filters);
        }else if (group.matches("TPCODL")){
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(11); // Set max length to 10 characters
            ed_customerno.setFilters(filters);
        }else if (group.matches("TPNODL")){
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(12); // Set max length to 10 characters
            ed_customerno.setFilters(filters);
        }
        Log.e("groupdata",group);

        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner.getSelectedItemPosition() < 0) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please Select Service Type", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (ed_customerno.getText().toString().isEmpty()) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter Customer No", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (group.matches("TPSODL") && ed_customerno.length() < 12) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter valid 12 digit Customer No", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (group.matches("TPCODL") && ed_customerno.length() < 11) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter valid 12 digit Customer No", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (group.matches("TPNODL") && ed_customerno.length() < 12) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter valid 12 digit Customer No", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (ed_meterno.getText().toString().isEmpty()) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter Meter No", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (ed_protocolno.getText().toString().isEmpty() && !dumpDataFlag) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter Protocol No", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (edBookNo.getText().toString().isEmpty() && !dumpDataFlag) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter Book No", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (metermark.getText().toString().isEmpty()) {
                    Snackbar snackbar1 = Snackbar.make(ll_main_layout, "Please enter Meter Make", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                } else if (!Connectivity.isConnected(getApplicationContext())) {
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
                    if (mImage == null && !dumpDataFlag) {
                        Toast.makeText(AddMeterDetailsActivity.this, "Please upload meter image!", Toast.LENGTH_LONG).show();
                    } else if (pImage == null && !dumpDataFlag) {
                        Toast.makeText(AddMeterDetailsActivity.this, "Please upload protocol image!", Toast.LENGTH_LONG).show();
                    } else if (oldMeterImageFlag) {
                        if (oImage == null && !dumpDataFlag) {
                            Toast.makeText(AddMeterDetailsActivity.this, "Please upload old meter image!", Toast.LENGTH_LONG).show();
                        } else {
                            uploadBitmapWithOlMeter();
                        }
                    } else if (dumpDataFlag) {
                        uploadBitmapDumpData();
                    } else {
                        uploadBitmap();
                    }
                }
            }
        });
        rl_new_meter_replacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selcectimg = "Meter";
                capturePhoto();
            }
        });
        rl_old_meter_replacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selcectimg = "OldMeter";
                capturePhoto();
            }
        });
        rl_protocol_replacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selcectimg = "Protocol";
                capturePhoto();
            }
        });
        rl_addimgmeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selcectimg = "Meter";
                capturePhoto();
            }
        });
        rl_addimgprotocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selcectimg = "Protocol";
                capturePhoto();
            }
        });
        rl_addImgOldMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selcectimg = "OldMeter";
                capturePhoto();
            }
        });
    }

    private void capturePhoto() {
        if (selcectimg.matches("Meter")) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "newMeterImages.jpeg");
            photoURI = FileProvider.getUriForFile(this, getContext().getApplicationContext().getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, CAMERA_REQUEST);
        } else if (selcectimg.matches("Protocol")) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file1 = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "protocolImages.jpeg");
            photoURI1 = FileProvider.getUriForFile(this, getContext().getApplicationContext().getPackageName() + ".fileprovider", file1);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI1);
            startActivityForResult(intent, CAMERA_REQUEST1);
        } else if (selcectimg.matches("OldMeter")) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file2 = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "oldMeterImages.jpeg");
            photoURI2 = FileProvider.getUriForFile(this, getContext().getApplicationContext().getPackageName() + ".fileprovider", file2);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI2);
            startActivityForResult(intent, CAMERA_REQUEST2);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

        }
        switch (requestCode) {
            case CAMERA_REQUEST:

                if (resultCode == RESULT_OK) {

                    File cachePath = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "newMeterImages.jpeg");
                    //  filePath = Uri.parse(photoURI);
                    filePath = Uri.parse(String.valueOf(photoURI));
//                    filePath = FileProvider.getUriForFile(AddMeterDetailsActivity.this,
//                            BuildConfig.APPLICATION_ID + ".provider", cachePath);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    } catch (Exception e) {
                    }
                    if (bitmap != null) {
                        File f = new File(cachePath.getAbsolutePath());
                        viewModel.compressImage(this, f.getParent(), f.getName().replaceFirst("[.][^.]+$", ""), f, 0);
                        if (ll_meter_protocol_image.getVisibility() == View.VISIBLE){
                            img_meter.setImageBitmap(bitmap);
                        } else {
                            img_new_meter_replacement.setImageBitmap(bitmap);
                        }
                    }
                }
                break;

            case CAMERA_REQUEST1:
                if (resultCode == RESULT_OK) {

                    File cachePath1 = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "protocolImages.jpeg");
                    //  filePath = Uri.parse(photoURI);
                    filePath = Uri.parse(String.valueOf(photoURI1));
//                    filePath1 = FileProvider.getUriForFile(AddMeterDetailsActivity.this,
//                            BuildConfig.APPLICATION_ID + ".provider", cachePath1);
                    Bitmap bitmap1 = null;
                    try {
                        bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    } catch (Exception e) {
                    }
                    if (bitmap1 != null) {
                        File f = new File(cachePath1.getAbsolutePath());
                        viewModel.compressImage(this, f.getParent(), f.getName().replaceFirst("[.][^.]+$", ""), f, 0);
                        if (ll_meter_protocol_image.getVisibility() == View.VISIBLE){
                            img_protocol.setImageBitmap(bitmap1);
                        } else {
                            img_protocol_replacement.setImageBitmap(bitmap1);
                        }
                    }
                }
                break;

            case CAMERA_REQUEST2:
                if (resultCode == RESULT_OK) {
                    File cachePath2 = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "oldMeterImages.jpeg");
                    //  filePath = Uri.parse(photoURI);
                    filePath = Uri.parse(String.valueOf(photoURI2));
//                    filePath2 = FileProvider.getUriForFile(AddMeterDetailsActivity.this,
//                            BuildConfig.APPLICATION_ID + ".provider", cachePath2);
                    Bitmap bitmap2 = null;
                    try {
                        bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    } catch (Exception e) {
                    }
                    if (bitmap2 != null) {
                        File f = new File(cachePath2.getAbsolutePath());
                        viewModel.compressImage(this, f.getParent(), f.getName().replaceFirst("[.][^.]+$", ""), f, 0);
                        if (ll_meter_protocol_image.getVisibility() == View.VISIBLE){
                            img_old_meter.setImageBitmap(bitmap2);
                        } else {
                            img_old_meter_replacement.setImageBitmap(bitmap2);
                        }
                    }
                }
                break;
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
        } else if (selcectimg.matches("OldMeter")) {
            this.oImage = imageFile;
        }
    }

    private class LocationListener implements android.location.LocationListener {
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
                ed_cordinate.setText(str_latitude + "," + str_longitude);
                tv_address.setText(str_address);


            } catch (Exception e) {
                Log.e(TAG, "onNetworkChange: " + e.getMessage());
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            if (!isFinishing()) {
                //showSettingsAlert();

                //allow location permission one time
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                Toast.makeText(AddMeterDetailsActivity.this, "Please Allow Location", Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddMeterDetailsActivity.this);

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

    private void spinJSON(List<MeterServiceTypeData> response) {

        try {
            spinnerArrayAdapter = new ArrayAdapter<MeterServiceTypeData>(AddMeterDetailsActivity.this, android.R.layout.simple_spinner_item, response);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spinner.setAdapter(spinnerArrayAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        meterServiceTypeID = String.valueOf(meterServiceTypeDataList.get(i).getMeterServiceTypeId());
        meterServiceTypeName = meterServiceTypeDataList.get(i).getMeterServiceTypeName();
        CommonMethods.setPrefsData(AddMeterDetailsActivity.this,PrefrenceKey.meterService_TypeID,meterServiceTypeID);
        CommonMethods.setPrefsData(AddMeterDetailsActivity.this,PrefrenceKey.meterService_TypeName,meterServiceTypeName);
        if (i == 1) {
            oldMeterImageFlag = true;
            //ll_old_meter.setVisibility(View.VISIBLE);
            ll_protocol.setVisibility(View.VISIBLE);
            ll_protocol_replacement.setVisibility(View.VISIBLE);
            ll_replacement.setVisibility(View.VISIBLE);
            ll_meter_protocol_image.setVisibility(View.GONE);
            ll_old_meter_number.setVisibility(View.VISIBLE);
        } else if (i == 2) {
            oldMeterImageFlag = true;
            //ll_old_meter.setVisibility(View.VISIBLE);
            ll_protocol.setVisibility(View.VISIBLE);
            ll_protocol_replacement.setVisibility(View.VISIBLE);
            ll_replacement.setVisibility(View.VISIBLE);
            ll_meter_protocol_image.setVisibility(View.GONE);
            ll_old_meter_number.setVisibility(View.VISIBLE);
        } else if (i == 10) {
            Intent intent = new Intent(getApplicationContext(), Others_Users.class);
            startActivity(intent);
            finish();
        } else if (i == 7) {
            dumpDataFlag = true;
            ll_meter_protocol_image.setVisibility(View.GONE);
            ll_protocol.setVisibility(View.GONE);
            ll_book_no.setVisibility(View.GONE);
            //ll_old_meter.setVisibility(View.GONE);
            ll_protocol_replacement.setVisibility(View.GONE);
            ll_replacement.setVisibility(View.GONE);
            ll_old_meter_number.setVisibility(View.GONE);
        } else {
            //ll_old_meter.setVisibility(View.GONE);
            ll_meter_protocol_image.setVisibility(View.VISIBLE);
            ll_protocol.setVisibility(View.VISIBLE);
            ll_book_no.setVisibility(View.VISIBLE);
            ll_protocol_replacement.setVisibility(View.GONE);
            ll_replacement.setVisibility(View.GONE);
            ll_old_meter_number.setVisibility(View.GONE);
        }
        //Toast.makeText(getApplicationContext(), meterServiceTypeID + " " + meterServiceTypeName  + " " + i, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //ll_old_meter.setVisibility(View.GONE);
        ll_protocol_replacement.setVisibility(View.GONE);
        ll_replacement.setVisibility(View.GONE);
    }

    void getMeterServiceTypeApi() {
        Call<MeterServiceType> call;
        final ProgressDialog progressDialog = new ProgressDialog(AddMeterDetailsActivity.this);
        //progressDialog.show();
        progressDialog.setMessage("Please Wait.... ");
        call = apiService.getMeterServiceType();
        call.enqueue(new Callback<MeterServiceType>() {
            @Override
            public void onResponse(Call<MeterServiceType> call, retrofit2.Response<MeterServiceType> response) {
                MeterServiceType entity = response.body();
                if (response.body() != null) {
                    meterServiceTypeDataList = response.body().getMeterServiceTypeDataList();
                }
                if (entity != null) {
                    if (entity.getMeterServiceTypeDataList() != null && entity.getMeterServiceTypeDataList().size() > 0 && entity.getResponseCode().equals("200")) {
                        Log.i("onSuccess", response.body().toString());
                        List<MeterServiceTypeData> meterServiceTypeDataList;
                        meterServiceTypeDataList = entity.getMeterServiceTypeDataList();
                        spinJSON(meterServiceTypeDataList);
                        progressDialog.hide();
                    }
                }
                if (entity != null && entity.getResponseCode().equals("0")) {
                    //progressDialog.hide();
                }
            }

            @Override
            public void onFailure(Call<MeterServiceType> call, Throwable t) {
                Toast.makeText(getApplication(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadBitmap() {
        final ProgressDialog progressDialog = new ProgressDialog(AddMeterDetailsActivity.this);
        progressDialog.show();
        progressDialog.setMessage("Please Wait.... ");

        //unique name of image changed here.
        String imagename = sharedPresence.getUserId(getApplicationContext()) + tdf.format(new Date()) + ed_meterno.getText().toString();

        String meterName = mImage.getAbsolutePath().replace(mImage.getName(), "" + "Meter_" + imagename + ".jpeg");
        String protocolName = pImage.getAbsolutePath().replace(pImage.getName(), "" + "Protocol_" + imagename + ".jpeg");

        RequestBody metreFile = RequestBody.create(MediaType.parse("image/*"), mImage);
        MultipartBody.Part meterImage = MultipartBody.Part.createFormData("photo", meterName, metreFile);

        RequestBody protocolFile = RequestBody.create(MediaType.parse("image/*"), pImage);
        MultipartBody.Part protocolImage = MultipartBody.Part.createFormData("proto_photo", protocolName, protocolFile);
        RequestBody daily_schedule_id = RequestBody.create(MediaType.parse("text/plain"),daily_scheduleId);
        RequestBody group_id = RequestBody.create(MediaType.parse("text/plain"),groupId);
        RequestBody group_type = RequestBody.create(MediaType.parse("text/plain"),groupType);
        RequestBody meter_no = RequestBody.create(MediaType.parse("text/plain"), ed_meterno.getText().toString());
        RequestBody vehicle_id = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getUserDesignation(getApplicationContext()));
        RequestBody book_no = RequestBody.create(MediaType.parse("text/plain"), edBookNo.getText().toString());
        RequestBody protocol_no = RequestBody.create(MediaType.parse("text/plain"), ed_protocolno.getText().toString());
        RequestBody consumer_no = RequestBody.create(MediaType.parse("text/plain"), ed_customerno.getText().toString());
        RequestBody meter_make = RequestBody.create(MediaType.parse("text/plain"), metermark.getText().toString());
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), str_longitude);
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), str_latitude);
        RequestBody installation_type = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeName);
        RequestBody installation_type_id = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeID);
        RequestBody location = RequestBody.create(MediaType.parse("text/plain"), tv_address.getText().toString());
        RequestBody installation_date = RequestBody.create(MediaType.parse("text/plain"), ed_installationdate.getText().toString());
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getEmpId(getApplicationContext()));
        RequestBody status = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getStatus(getApplicationContext()));
        RequestBody group_name = RequestBody.create(MediaType.parse("text/plain"), group);
        // RequestBody scheduleID = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getDailyScheduleId(getApplicationContext()));
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("daily_schedule_id", daily_schedule_id);
        map.put("group_id", group_id);
        map.put("group_type", group_type);
        map.put("vehicle_id", vehicle_id);
        map.put("meter_no", meter_no);
        map.put("book_no", book_no);
        map.put("protocol_no", protocol_no);
        map.put("consumer_no", consumer_no);
        map.put("meter_make", meter_make);
        map.put("installation_date", installation_date);
        map.put("installation_type", installation_type);
        map.put("installation_type_id", installation_type_id);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("location", location);
        map.put("userid", userid);
        map.put("status", status);
        map.put("group_name", group_name);

        final ApiInterface apiService = RetrofitClient.getService();
        Call<AddMeter> call = apiService.updateMeterDetail(meterImage, protocolImage, map);

        call.enqueue(new Callback<AddMeter>() {
            @Override
            public void onResponse(Call<AddMeter> call,
                                   retrofit2.Response<AddMeter> response) {
                Log.e("response", response.body().getResponseMessage());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    AddMeter addMeter = response.body();
                    Toast.makeText(AddMeterDetailsActivity.this, "" + addMeter.getResponseMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddMeterDetailsActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddMeterDetailsActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<AddMeter> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("response", "error" + t);
            }
        });
    }

    private void uploadBitmapWithOlMeter() {
        final ProgressDialog progressDialog = new ProgressDialog(AddMeterDetailsActivity.this);
        progressDialog.show();
        progressDialog.setMessage("Please Wait.... ");

        //unique name of image changed here.
        String imagename = sharedPresence.getUserId(getApplicationContext()) + tdf.format(new Date()) + ed_meterno.getText().toString();

        String meterName = mImage.getAbsolutePath().replace(mImage.getName(), "" + "Meter_" + imagename + ".jpeg");
        String protocolName = pImage.getAbsolutePath().replace(pImage.getName(), "" + "Protocol_" + imagename + ".jpeg");
        String oldMeterName = oImage.getAbsolutePath().replace(oImage.getName(), "" + "Old_Meter_" + imagename + ".jpeg");

        RequestBody metreFile = RequestBody.create(MediaType.parse("image/*"), mImage);
        MultipartBody.Part meterImage = MultipartBody.Part.createFormData("photo", meterName, metreFile);

        RequestBody protocolFile = RequestBody.create(MediaType.parse("image/*"), pImage);
        MultipartBody.Part protocolImage = MultipartBody.Part.createFormData("proto_photo", protocolName, protocolFile);

        RequestBody oldMetreFile = RequestBody.create(MediaType.parse("image/*"), oImage);
        oldMetreImage = MultipartBody.Part.createFormData("old_meter_image", oldMeterName, oldMetreFile);

        RequestBody daily_schedule_id = RequestBody.create(MediaType.parse("text/plain"),daily_scheduleId);
        RequestBody schedule_date = RequestBody.create(MediaType.parse("text/plain"),"2023-05-04");

        RequestBody group_id = RequestBody.create(MediaType.parse("text/plain"),groupId);
        RequestBody group_type = RequestBody.create(MediaType.parse("text/plain"),groupType);
        RequestBody old_meter_no = RequestBody.create(MediaType.parse("text/plain"), edOldMeterNo.getText().toString());
        RequestBody meter_no = RequestBody.create(MediaType.parse("text/plain"), ed_meterno.getText().toString());
        RequestBody vehicle_id = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getUserDesignation(getApplicationContext()));
        RequestBody book_no = RequestBody.create(MediaType.parse("text/plain"), edBookNo.getText().toString());
        RequestBody protocol_no = RequestBody.create(MediaType.parse("text/plain"), ed_protocolno.getText().toString());
        RequestBody consumer_no = RequestBody.create(MediaType.parse("text/plain"), ed_customerno.getText().toString());
        RequestBody meter_make = RequestBody.create(MediaType.parse("text/plain"), metermark.getText().toString());
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), str_longitude);
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), str_latitude);
        RequestBody installation_type = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeName);
        RequestBody installation_type_id = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeID);
        RequestBody location = RequestBody.create(MediaType.parse("text/plain"), tv_address.getText().toString());
        RequestBody installation_date = RequestBody.create(MediaType.parse("text/plain"), ed_installationdate.getText().toString());
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getEmpId(getApplicationContext()));
        RequestBody status = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getStatus(getApplicationContext()));
        RequestBody group_name = RequestBody.create(MediaType.parse("text/plain"), group);
        // RequestBody scheduleID = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getDailyScheduleId(getApplicationContext()));
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("daily_schedule_id", daily_schedule_id);
        map.put("schedule_date", schedule_date);
        map.put("group_id", group_id);
        map.put("group_type", group_type);
        map.put("vehicle_id", vehicle_id);
        map.put("old_meter_no", old_meter_no);
        map.put("meter_no", meter_no);
        map.put("book_no", book_no);
        map.put("protocol_no", protocol_no);
        map.put("consumer_no", consumer_no);
        map.put("meter_make", meter_make);
        map.put("installation_date", installation_date);
        map.put("installation_type", installation_type);
        map.put("installation_type_id", installation_type_id);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("location", location);
        map.put("userid", userid);
        map.put("status", status);
        map.put("group_name", group_name);

        final ApiInterface apiService = RetrofitClient.getService();
        Call<AddMeter> call = apiService.updateMeterDetailOldMeter(meterImage, protocolImage, oldMetreImage, map);

        call.enqueue(new Callback<AddMeter>() {
            @Override
            public void onResponse(Call<AddMeter> call,
                                   retrofit2.Response<AddMeter> response) {
                Log.e("response", response.body().getResponseMessage());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    AddMeter addMeter = response.body();
                    Toast.makeText(AddMeterDetailsActivity.this, "" + addMeter.getResponseMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddMeterDetailsActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddMeterDetailsActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<AddMeter> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("response", "error" + t);
            }
        });
    }

    private void uploadBitmapDumpData() {
        final ProgressDialog progressDialog = new ProgressDialog(AddMeterDetailsActivity.this);
        progressDialog.show();
        progressDialog.setMessage("Please Wait.... ");

        RequestBody daily_schedule_id = RequestBody.create(MediaType.parse("text/plain"),daily_scheduleId);
        RequestBody schedule_date = RequestBody.create(MediaType.parse("text/plain"),"2023-05-04");
        RequestBody group_id = RequestBody.create(MediaType.parse("text/plain"),groupId);
        RequestBody group_type = RequestBody.create(MediaType.parse("text/plain"),groupType);
        RequestBody meter_no = RequestBody.create(MediaType.parse("text/plain"), ed_meterno.getText().toString());
        RequestBody vehicle_id = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getUserDesignation(getApplicationContext()));
        //RequestBody protocol_no = RequestBody.create(MediaType.parse("text/plain"), ed_protocolno.getText().toString());
        RequestBody consumer_no = RequestBody.create(MediaType.parse("text/plain"), ed_customerno.getText().toString());
        RequestBody meter_make = RequestBody.create(MediaType.parse("text/plain"), metermark.getText().toString());
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), str_longitude);
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), str_latitude);
        RequestBody installation_type = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeName);
        RequestBody installation_type_id = RequestBody.create(MediaType.parse("text/plain"), meterServiceTypeID);
        RequestBody location = RequestBody.create(MediaType.parse("text/plain"), tv_address.getText().toString());
        RequestBody installation_date = RequestBody.create(MediaType.parse("text/plain"), ed_installationdate.getText().toString());
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getEmpId(getApplicationContext()));
        RequestBody status = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getStatus(getApplicationContext()));
        RequestBody group_name = RequestBody.create(MediaType.parse("text/plain"), group);
        // RequestBody scheduleID = RequestBody.create(MediaType.parse("text/plain"), sharedPresence.getDailyScheduleId(getApplicationContext()));
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("daily_schedule_id", daily_schedule_id);
        map.put("schedule_date", schedule_date);
        map.put("group_id", group_id);
        map.put("group_type", group_type);
        map.put("vehicle_id", vehicle_id);
        map.put("meter_no", meter_no);
        //map.put("protocol_no", protocol_no);
        map.put("consumer_no", consumer_no);
        map.put("meter_make", meter_make);
        map.put("installation_date", installation_date);
        map.put("installation_type", installation_type);
        map.put("installation_type_id", installation_type_id);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("location", location);
        map.put("userid", userid);
        map.put("status", status);
        map.put("group_name", group_name);

        final ApiInterface apiService = RetrofitClient.getService();
        Call<AddMeter> call = apiService.updateMeterDetailDumpData(map);

        call.enqueue(new Callback<AddMeter>() {
            @Override
            public void onResponse(Call<AddMeter> call,
                                   retrofit2.Response<AddMeter> response) {
                Log.e("response", response.body().getResponseMessage());
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    AddMeter addMeter = response.body();
                    Toast.makeText(AddMeterDetailsActivity.this, "" + addMeter.getResponseMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddMeterDetailsActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddMeterDetailsActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<AddMeter> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("response", "error" + t);
            }
        });

    }

    private void inactived() {
        // cables.setBackground(  cables.setTextColor(getResources().getColorStateList(R.color.colorPrimary)));
        // conecetions.setBackground(  cables.setTextColor(getResources().getColorStateList(R.color.colorPrimary));));
        conecetions.setTextColor(getResources().getColorStateList(R.color.black));
        cables.setTextColor(getResources().getColorStateList(R.color.black));
        wioutcables.setTextColor(getResources().getColorStateList(R.color.black));
    }

    private void click() {

        instalation_type = "NEW CONNECTION SERVICES";
        inactived();
        conecetions.setTextColor(getResources().getColorStateList(R.color.colorAccent));
    }
}
