package com.rjdev.meterinstallapp.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.rjdev.meterinstallapp.Utils.Resizer;
import com.rjdev.meterinstallapp.interfaces.ImageNavigator;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ImageViewModel extends AndroidViewModel {
    ImageNavigator navigator;

    public ImageNavigator getNavigator() {
        return navigator;
    }

    public void setNavigator(ImageNavigator navigator) {
        this.navigator = navigator;
    }

    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    public ArrayList<String> getImageFilePath(Context context, Uri uri) {
        ArrayList imagePathList = new ArrayList<String>();

        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imagePathList.add(imagePath);
            cursor.close();
        }

        return imagePathList;
    }

    public String convertMediaUriToPath(Context context, Uri contentUri) {
        Cursor cursor = null;
        String picturePath = "";
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(proj[0]);
                picturePath = cursor.getString(columnIndex);
                Log.e("image path", "path : " + picturePath);

            }
        } catch (Exception e) {
            Log.e("convertMediaUriToPath", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return picturePath;
    }

    @SuppressLint("CheckResult")
    public void compressImage(@NonNull Context context, String outputDirPath, String outputFilename, File sourceImage, final int imageType ){
        new Resizer(context)
                .setTargetLength(1080)
                .setQuality(80)
                .setOutputFormat("JPEG")
                .setOutputFilename(outputFilename)
                .setOutputDirPath(outputDirPath)
                .setSourceImage(sourceImage)
                .getResizedFileAsFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        if(navigator != null)navigator.fileCompressed(file, imageType);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
