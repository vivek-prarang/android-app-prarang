package com.riversanskiriti.prarang;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.riversanskiriti.prarang.config.UrlConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ARPIT on 19-03-2017. Edited by Pawan on 12 Apr 2022
 */

public class AppController extends Application implements LifecycleObserver {

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    AppUtils appUtils;
    private static AppController mInstance;
    String userCity;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        appUtils = new AppUtils(this);

        String userCityVal = appUtils.getGeographyId();
        if(userCityVal!=null){
            userCity = userCityVal.substring( 0, appUtils.getGeographyId().indexOf(","));
        }
        else {
            userCity=null;
        }
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("Firebase Token",pref.getString("FirebaseToken",""));
    }


    // Code Added by Pawan on 12 Apr 22 for app usage
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        Log.d("AppController", "App in foreground");
        if(appUtils.getSubscriberId()!=null && userCity!=null) {
            callAppUsage(appUtils.getSubscriberId(), "Start", userCity);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.d("AppController", "App in background");
        if(appUtils.getSubscriberId()!=null && userCity!=null){
            callAppUsage(appUtils.getSubscriberId(),"End",userCity);
        }
        else {
            callAppUsage(StaticClass.subsId,"End",StaticClass.userCity);
        }
    }


    public void callAppUsage(final String subscriberId, final String flag, final String userCity) {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, UrlConfig.appUsageURL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AppusageRes--",response.toString());
//                Toast.makeText(mInstance, "AppusageRes" + response, Toast.LENGTH_SHORT).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mInstance, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("subscriberId", subscriberId);
                params.put("flag", flag);
                params.put("userCity", userCity);
                Log.d("params-",params.toString());
                return params;
            }
        };
        queue.add(request);
    }
    // Code Added by Pawan on 12 Apr 22 for app usage


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}