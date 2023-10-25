package com.rjdev.meterinstallapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;

/**
 * Created by Deepak Kaushik on 4/6/2021.
 */

public class ImageUtils {
    public static File getScaledImage(int targetLength, int quality, Bitmap.CompressFormat compressFormat,
                                      String outputDirPath, String outputFilename, File sourceImage) throws IOException {
        File directory = new File(outputDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Prepare the new file name and path
        String outputFilePath = FileUtils.getOutputFilePath(compressFormat, outputDirPath, outputFilename, sourceImage);

        // Write the resized image to the new file
        Bitmap scaledBitmap = getScaledBitmap(targetLength, sourceImage);
        scaledBitmap = modifyOrientation(scaledBitmap, sourceImage.getAbsolutePath());
        FileUtils.writeBitmapToFile(scaledBitmap, compressFormat, quality, outputFilePath);
        scaledBitmap.recycle();
        return new File(outputFilePath);
    }

    public static Bitmap getScaledBitmap(int targetLength, File sourceImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(sourceImage.getAbsolutePath(), options);

        // Get the dimensions of the original bitmap
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        float aspectRatio = (float) originalWidth / originalHeight;

        // Calculate the target dimensions
        int targetWidth, targetHeight;

        if (originalWidth > originalHeight) {
            targetWidth = targetLength;
            targetHeight = Math.round(targetWidth / aspectRatio);
        } else {
            aspectRatio = 1 / aspectRatio;
            targetHeight = targetLength;
            targetWidth = Math.round(targetHeight / aspectRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
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
}
