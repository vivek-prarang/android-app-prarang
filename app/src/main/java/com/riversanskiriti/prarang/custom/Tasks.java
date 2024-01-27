package com.riversanskiriti.prarang.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ARPIT on 21-04-2017.
 */

public class Tasks {

    private Context context;
    private Listener listener;
    private int maxSize = 250;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Tasks(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    private class ImageCompress extends AsyncTask<Uri, Integer, byte[]> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Compressing..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            int compression = 100;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compression, stream);
            byte[] imageInByte = stream.toByteArray();
            long lengthbmp = imageInByte.length / 1024;

            while (lengthbmp > maxSize) {
                compression = (compression / 4) * 3;
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, compression, stream);
                imageInByte = stream.toByteArray();
                lengthbmp = imageInByte.length / 1024;
            }
            return imageInByte;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            pd.dismiss();
            if (listener != null) {
                try {
                    listener.onTaskPostExecute(bytes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void executeCompressImage(Uri uri) {
        new ImageCompress().execute(uri);
    }

    //Interface Callbacks
    public interface Listener {
        public void onTaskPostExecute(Object object) throws JSONException;
    }
}
