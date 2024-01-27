package com.riversanskiriti.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.riversanskiriti.prarang.AppController;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.config.UrlConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ARPIT on 19-03-2017.
 */

public class Network {
    private Context context;
    private boolean showProgress = false;
    private Network.Listener listener;

    private ProgressDialog pd;
    private int timeout = 120 * 1000;

    public Network(Context context) {
        this.context = context;
    }

    public Network(Context context, Network.Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    private void onNetworkSuccess(String response, String url) {
        if (isShowProgress()) {
            if (pd != null) {
                pd.dismiss();
            }
        }
        if (listener != null) {
            int queryIndex = url.indexOf("?");
            if (queryIndex != -1) {
                url = url.substring(0, queryIndex);
            }
            listener.onNetworkSuccess(response, url);
        }
    }

    private void onNetworkError(String error, String url) {
        if (isShowProgress()) {
            if (pd != null) {
                pd.dismiss();
            }
        }
        if (listener != null) {
            int queryIndex = url.indexOf("?");
            if (queryIndex != -1) {
                url = url.substring(0, queryIndex);
            }
            listener.onNetworkError(error, url);
        }
    }

    private void onNetworkProgress() {
        if (isShowProgress()) {
            pd = new ProgressDialog(context);
            pd.setMessage("Loading..");
            pd.setCancelable(false);
            pd.show();
        }
    }

    private String makeQueryString(HashMap<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value != null) {
                try {
                    value = URLEncoder.encode(String.valueOf(value), "UTF-8");
                    if (builder.length() > 0) {
                        builder.append("&");
                    }
                    builder.append(key).append("=").append(value);
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
        return builder.toString();
    }

    public void makeGETRequest(final HashMap<String, String> map, final String url) {
        makeRequest(map, url, Request.Method.GET);
    }

    public void makePOSTRequest(final HashMap<String, String> map, final String url) {
        makeRequest(map, url, Request.Method.POST);
    }

    //Default
    public void makeRequest(final HashMap<String, String> map, final String url) {
        //Change according to the default behaviour of application
        if (!isNetworkAvailable()) {
            if (url.equals(UrlConfig.rampurCityPost) || url.equals(UrlConfig.meerutCityPost)
                    || url.equals(UrlConfig.lucknowCityPost) || url.equals(UrlConfig.jaunpurCityPost)) {
                onNetworkError(context.getResources().getString(R.string.msg_networkerror), url);
            } else {
                onNetworkError(context.getResources().getString(R.string.msg_nointernet), url);
            }
            return;
        }
        makeRequestWithQueryString(map, url);
    }

    public void makeRequestWithQueryString(HashMap<String, String> map, String url) {
        if (map.size() != 0) {
            url = url + "?" + makeQueryString(map);
        }
        makeRequest(map, url, Request.Method.GET);
    }

    private void makeRequest(final HashMap<String, String> map, final String url, int method) {
        Log.e("UrlAfterFilter", url);
        onNetworkProgress();
        StringRequest req = new StringRequest(method,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        response = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"), "UTF-8");
                        Log.d("response11-",response);
                    } catch (Exception e) {

                    }
                }
                onNetworkSuccess(response, url);  // Calling all Cities Post from this method
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.getMessage() + "");
                onNetworkError("Network time out!", url);
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                return map;
            }
        };
        //req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(req, "string_req");
    }

    public void cancelRequest() {
        AppController.getInstance().getRequestQueue().cancelAll("string_req");
    }

    //================================================Callbacks======================================================
    public interface Listener {
        public void onNetworkSuccess(String response, String url);

        public void onNetworkError(String error, String url);
    }
}
