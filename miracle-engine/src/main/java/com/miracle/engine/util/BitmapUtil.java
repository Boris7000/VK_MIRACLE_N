package com.miracle.engine.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.FloatRange;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class BitmapUtil {

    public static Bitmap compressBitmap(Bitmap bitmap, @FloatRange(from = 0.1F, to = 1F) float compressionMultiplier) {

        int width = (int) (bitmap.getWidth() * compressionMultiplier);
        int height = (int) (bitmap.getHeight() * compressionMultiplier);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

        byte[] byteArray = outputStream.toByteArray();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }

        return inSampleSize;
    }

    public static Bitmap loadBitmapFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            InputStream inputStream = url.openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
