package com.riversanskiriti.utils.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.riversanskiriti.prarang.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ARPIT on 17-04-2017.
 */

public class Multipart {
    private String url;
    private HashMap<String, String> map;
    private byte[] bytes;
    private boolean showProgress = false;

    private Multipart.Listener listener;
    private Context context;
    private String attachedKey = "commentImage";

    public Multipart(Context context, Multipart.Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    private int timeout = 30 * 1000;

    // Check Network Availability
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setBytes(byte[] bytes, String attachedKey) {
        this.bytes = bytes;
        this.attachedKey = attachedKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
    }

    private class MultiTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isShowProgress()) {
                pd = new ProgressDialog(context);
                pd.setMessage("Uploading..");
                pd.setCancelable(false);
                pd.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                URL url = new URL(Multipart.this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(timeout);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                // Set the Content-Type header for multipart/form-data
                String boundary = "---------------------------boundary";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = connection.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                // Add the file part
                dataOutputStream.writeBytes("--" + boundary + "\r\n");
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + attachedKey + "\"; filename=\"comment.jpg\"\r\n");
                dataOutputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                dataOutputStream.write(bytes);
                dataOutputStream.writeBytes("\r\n");

                // Add the other form fields
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    dataOutputStream.writeBytes("--" + boundary + "\r\n");
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
                    dataOutputStream.writeBytes(entry.getValue() + "\r\n");
                }

                // End of multipart/form-data
                dataOutputStream.writeBytes("--" + boundary + "--\r\n");
                dataOutputStream.flush();
                dataOutputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    result = response.toString();
                    reader.close();
                } else {
                    result = "network";
                }

                connection.disconnect();
            } catch (Exception e) {
                result = "network";
                Log.e("Multipart Exception", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values.length != 0) {
                listener.onMultipartProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (isShowProgress()) {
                pd.dismiss();
            }
            if (response == null || response.equalsIgnoreCase("network")) {
                if (response != null) {
                    response = context.getResources().getString(R.string.msg_nointernet);
                }
                listener.onMultipartError(response, url);
            } else {
                try {
                    response = URLEncoder.encode(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                listener.onMultipartSuccess(response, url);
            }
        }
    }

    public void execute() {
        new MultiTask().execute();
    }

    public interface Listener {
        void onMultipartSuccess(String response, String url);

        void onMultipartProgress(int progress);

        void onMultipartError(String error, String url);
    }
}
