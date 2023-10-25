package com.rjdev.meterinstallapp.Utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by Deepak Kaushik on 4/6/2021.
 */

public class FileUtils {
    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;

    public static String getOutputFilePath(Bitmap.CompressFormat compressFormat, String outputDirPath, String outputFilename, File sourceImage) {
        String originalFileName = sourceImage.getName();
        String targetFileName;
        String targetFileExtension = "." + compressFormat.name().toLowerCase(Locale.US).replace("jpeg", "jpg");

        if (outputFilename == null) {
            int extensionIndex = originalFileName.lastIndexOf('.');
            if (extensionIndex == -1) {
                targetFileName = originalFileName + targetFileExtension;
            } else {
                targetFileName = originalFileName.substring(0, extensionIndex) + targetFileExtension;
            }
        } else {
            targetFileName = outputFilename + targetFileExtension;
        }

        return outputDirPath + File.separator + targetFileName;
    }

    public static void writeBitmapToFile(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality, String filePath) throws IOException {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }

    public static String getFileSize(File file) {

        if (!file.isFile()) {
            throw new IllegalArgumentException("Expected a file");
        }
        final double length = file.length();

        if (length > MiB) {
            return format.format(length / MiB) + " MiB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KiB";
        }
        return format.format(length) + " B";
    }
}
