package com.rjdev.meterinstallapp.Utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.rjdev.meterinstallapp.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CommonMethods {

    private static SharedPreferences.Editor editor;
    private static SharedPreferences.Editor editorURL;
    public static final String PREF_ALL = "pref_all";
    public static final String PREF_SERVER_IP = "pref_server_ip";


    public static void setPrefsData(Context context, String prefsKey,
                                    String prefValue) {

        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_ALL, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(prefsKey, prefValue);
        editor.apply();
    }

    public static String getPrefsData(Context context, String prefsKey,
                                      String defaultValue) {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_ALL, Context.MODE_PRIVATE);
        String prefsValue = sharedPreferences.getString(prefsKey, defaultValue);
        return prefsValue;
    }

    public static String getPrefsDataURL(Context context, String prefsKey,
                                         String defaultValue) {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_SERVER_IP, Context.MODE_PRIVATE);
        String prefsValue = sharedPreferences.getString(prefsKey, defaultValue);
        return prefsValue;
    }

    public static void setPrefsDataURL(Context context, String prefsKey,
                                       String prefValue) {

        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_SERVER_IP, Context.MODE_PRIVATE);

        editorURL = sharedPreferences.edit();
        editorURL.putString(prefsKey, prefValue);
        editorURL.commit();
    }


    public static void delPrefsData(Context context) {

        // SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences pref = context.getSharedPreferences(PREF_ALL, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    @SuppressLint("SimpleDateFormat")
    public static String changeDateFormat(String inputDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd/MM/yyyy");
        String outputDate = format.format(newDate);
        return outputDate;
    }

    public static String convertUTF(String s) {
        String data = "";
        try {
            data = URLEncoder.encode(s, "UTF-8");
            if (data.contains("%5C")) {
                data = data.replaceAll("%5C", "\\\\");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Phone validation
     */
    public static boolean isValidMobile(String phoneNo) {
        boolean check = false;
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else if (phoneNo.length() < 10 || phoneNo.length() > 15) return false;
        else return false;
    }

    private static final String PATTERN = "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
            + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
            + "|[1-9][0-9]|[0-9]))";

    public static boolean IPvalidate(String IP) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(IP);
        return matcher.matches();
    }

    public static void showAlert(String message, Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        try {
            builder.show();
            //set pop up top of screen  04March2022
            //.getWindow().setGravity(Gravity.TOP);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Called for checking Internet connection
     */
    public static boolean isOnline(Context context) {

      /*  NetworkInfo netInfo = null;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();*/
/*
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }

        }
        return false;*/

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }


    public static byte[] downloadUrl(URL toDownload) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outputStream.toByteArray();
    }

    public static void turnGPSOn(Context ctx) {
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    // automatic turn off the gps
    public static void turnGPSOff(Context ctx) {
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }


    private Pattern pattern;
    private Matcher matcher;

    private static final String DATE_PATTERN =
            "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";


    /**
     * Validate date format with regular expression
     *
     * @param date date address for validation
     * @return true valid date format, false invalid date format
     */
    public boolean validateONDOB(final String date) {

        matcher = pattern.matcher(date);

        if (matcher.matches()) {
            matcher.reset();

            if (matcher.find()) {
                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));

                if (day.equals("31") &&
                        (month.equals("4") || month.equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month.equals("06") ||
                                month.equals("09"))) {
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                } else if (month.equals("2") || month.equals("02")) {
                    //leap year
                    if (year % 4 == 0) {
                        return !day.equals("30") && !day.equals("31");
                    } else {
                        return !day.equals("29") && !day.equals("30") && !day.equals("31");
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    /*  gps location mode like :
      0 = LOCATION_MODE_OFF
      1 = LOCATION_MODE_SENSORS_ONLY
      2 = LOCATION_MODE_BATTERY_SAVING
      3 = LOCATION_MODE_HIGH_ACCURACY*/
    public static int getLocationMode(Context context) {
        int value = 0;
        try {
            value = Settings.Secure.getInt(context.getContentResolver(), String.valueOf(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }


    public static void openGPSSettings(Context context) {
        //Get GPS now state (open or closed)
        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);

        if (gpsEnabled) {
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, 0);
        } else {
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, 3);
        }
    }

    public static String getPackageInfo(Context context, String packageInfo) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo.equals("versionCode")) {
            return String.valueOf(info.versionCode);
        }
        if (packageInfo.equals("versionName")) {
            return info.versionName;
        }
        if (packageInfo.equals("packageName")) {
            return info.packageName;
        }
        return null;
    }

    public static String bitMapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.NO_WRAP);
        return temp;
    }

    public static Bitmap Base64StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(image_absolute_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static void askForPermission(String permission, Integer requestCode, Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        }  // Toast.makeText(activity, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();

    }

    public static boolean checkPermission(String permision, Context context) {
        String permission = permision;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void mutipleRequestPermission(Activity activity, String[] accessPemissiom, int permissionCode) {
        ActivityCompat.requestPermissions(activity, accessPemissiom, permissionCode);
    }

    @SuppressLint("SimpleDateFormat")
    public static String timeFormate(String time) {
        SimpleDateFormat in = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat out = new SimpleDateFormat("hh:mm");
        Date date = null;
        try {
            date = in.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return out.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String timeFormate1(String time) {
        SimpleDateFormat in = new SimpleDateFormat("hh:mm");
        SimpleDateFormat out = new SimpleDateFormat("hh:mm");
        Date date = null;
        try {
            date = in.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return out.format(date);
    }


    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    @SuppressLint("SimpleDateFormat")
    public static long calculateDates(Context context, String from, String to) {
        long diffrence = 0;
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = myFormat.parse(from);
            Date date2 = myFormat.parse(to);
            long diff = date2.getTime() - date1.getTime();
            diffrence = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diffrence;
    }

    public static void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_LONG);
        snackbar.show();
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }
    @SuppressLint("SimpleDateFormat")
    public static String changeDateFormateToMMDDYY(String strDate) {
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String changeDateTOddMMMyyy(String inputDate) {
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }
    @SuppressLint("SimpleDateFormat")
    public static String changeDateTOyyyyMMdd(String inputDate) {
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }
    @SuppressLint("SimpleDateFormat")
    public static String changeDateFromMMddyyyy(String inputDate) {
        DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String changeDateFromyyyyMMdd(String inputDate) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }

    //get month text.
    public static String getMonth(int month) {
        return new DateFormatSymbols().getShortMonths()[month - 1];
    }

    public static String pad(int input) {
        String str = " ";
        if (input > 10 || input == 10) {
            str = Integer.toString(input);
        } else {
            str = "0" + input;
        }
        return str;
    }


    @TargetApi(Build.VERSION_CODES.O)
    public static String convertToIndianCurrency(String num) {
        BigDecimal bd = new BigDecimal(num);
        long number = bd.longValue();
        long no = bd.longValue();
        int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
        int digits_length = String.valueOf(no).length();
        int i = 0;
        ArrayList<String> str = new ArrayList<>();
        HashMap<Integer, String> words = new HashMap<>();
        words.put(0, "");
        words.put(1, "One");
        words.put(2, "Two");
        words.put(3, "Three");
        words.put(4, "Four");
        words.put(5, "Five");
        words.put(6, "Six");
        words.put(7, "Seven");
        words.put(8, "Eight");
        words.put(9, "Nine");
        words.put(10, "Ten");
        words.put(11, "Eleven");
        words.put(12, "Twelve");
        words.put(13, "Thirteen");
        words.put(14, "Fourteen");
        words.put(15, "Fifteen");
        words.put(16, "Sixteen");
        words.put(17, "Seventeen");
        words.put(18, "Eighteen");
        words.put(19, "Nineteen");
        words.put(20, "Twenty");
        words.put(30, "Thirty");
        words.put(40, "Forty");
        words.put(50, "Fifty");
        words.put(60, "Sixty");
        words.put(70, "Seventy");
        words.put(80, "Eighty");
        words.put(90, "Ninety");
        String digits[] = {"", "Hundred", "Thousand", "Lakh", "Crore"};
        while (i < digits_length) {
            int divider = (i == 2) ? 10 : 100;
            number = no % divider;
            no = no / divider;
            i += divider == 10 ? 1 : 2;
            if (number > 0) {
                int counter = str.size();
                String plural = (counter > 0 && number > 9) ? "s" : "";
                String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " + digits[counter] + plural : words.get(Integer.valueOf((int) Math.floor(number / 10) * 10)) + " " + words.get(Integer.valueOf((int) (number % 10))) + " " + digits[counter] + plural;
                str.add(tmp);
            } else {
                str.add("");
            }
        }

        Collections.reverse(str);
        String Rupees = TextUtils.join(" ", str).trim();

        String paise = (decimal) > 0 ? " And Paise " + words.get(Integer.valueOf((int) (decimal - decimal % 10))) + " " + words.get(Integer.valueOf((int) (decimal % 10))) : "";
        return "Rupees " + Rupees + paise + " Only";
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    public static String mobileCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }


    @SuppressWarnings("deprecation")
    public static Spanned stripHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static void showSnack(boolean isConnected, View root) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_LONG);
        snackbar.show();
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

    }


}
