package com.riversanskiriti.utils.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.riversanskiriti.prarang.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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

    //Check Network Avaiblity
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
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

//    public void setMap(JSONObject map) {
//        this.map = map;
//    }

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
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);

                HttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpPost postRequest = new HttpPost(url);
                ByteArrayBody bab = new ByteArrayBody(getBytes(), "comment.jpg");

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
                reqEntity.addPart(attachedKey, bab);
                for (Map.Entry<String, String> item : map.entrySet()) {
                    reqEntity.addPart(item.getKey(), new StringBody(item.getValue().toString(), Charset.forName(HTTP.UTF_8)));
                }
                postRequest.setEntity(reqEntity);
                HttpResponse response = httpClient.execute(postRequest);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));

                String sResponse;
                StringBuilder s = new StringBuilder();
                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                result = s.toString();
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
                if(response != null){
                    response = context.getResources().getString(R.string.msg_nointernet);
                }
                listener.onMultipartError(response, url);
            } else {
                try {
                    response = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"), "UTF-8");
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
        public void onMultipartSuccess(String response, String url);

        public void onMultipartProgress(int progress);

        public void onMultipartError(String error, String url);
    }
}

