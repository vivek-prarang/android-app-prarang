package com.riversanskiriti.prarang;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AppUtils {

    Context mContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Singleton Instance
    private static AppUtils sInstance;

    public static synchronized AppUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppUtils(context);
        }
        return sInstance;
    }

    public AppUtils(Context context) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    private void setStringData(String key, String value) {
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getStringData(String key) {
        return sharedPreferences.getString(key, null);
    }

    private void setIntData(String key, int value) {
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private int getIntData(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    private void setBoolData(String key, Boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean getBoolData(String key) {
        return sharedPreferences.getBoolean(key, false);
    }


    //Methods
    public void setFirebaseToken(String token) {
        setStringData("FirebaseToken", token);
    }

    public String getFirebaseToken() {
        return getStringData("FirebaseToken");
    }

    public String getCityRegionList() {
        return getStringData("CityRegionList");
    }

    public void setCityRegionList(String value) {
        setStringData("CityRegionList", value);
    }

    public void setName(String value) {
        setStringData("username", value);
    }

    public String getName() {
        return getStringData("username");
    }

    public void setNumber(String value) {
        setStringData("usernumber", value);
    }

    public String getNumber() {
        return getStringData("usernumber");
    }

    public void setGeographyId(String value) {
        setStringData("usergeographyid", value);
    }

    public String getGeographyId() {
        return getStringData("usergeographyid");
    }

    public void setGeographyName(String value) {
        setStringData("usergeographyname", value);
    }

    public String getGeographyName() {
        return getStringData("usergeographyname");
    }

    public void setGeoFilterId(String value) {
        setStringData("geofilterid", value);
    }

    public String getGeoFilterName() {
        return getStringData("geofiltername");
    }

    public void setGeoFilterName(String value) {
        setStringData("geofiltername", value);
    }

    public String getGeoFilterId() {
        return getStringData("geofilterid");
    }


    public void setIsHindi(boolean value) {
        setBoolData("userishindiview", value);
    }

    public boolean getIsHindi() {
        return getBoolData("userishindiview");
    }

    public void setIsEnglish(boolean value) {
        setBoolData("userisenglishview", value);
    }

    public boolean getIsEnglish() {
        return getBoolData("userisenglishview");
    }

    public void setSubscriberId(String value) {
        setStringData("subscriberid", value);
    }

    public String getSubscriberId() {
        return getStringData("subscriberid");
    }

    public void setProfileUrl(String value) {
        setStringData("profilepicurl", value);
    }

    public String getProfileUrl() {
        String url = getStringData("profilepicurl");
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        return url;
    }

    public void setTagsResponse(String value) {
        setStringData("tagsresponce", value);
    }

    public String getTagsResponse() {
        return getStringData("tagsresponce");
    }

    public String getCityName(String id) {
        String name = null;
        try {
            JSONArray array = new JSONArray(getCityRegionList());
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                if (json.getString("listCode").equalsIgnoreCase(id)) {
                    name = json.getString("languageUnicode");
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getCityId(String name) {
        String id = null;
        try {
            JSONArray array = new JSONArray(getCityRegionList());
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                if (json.getString("languageUnicode").equalsIgnoreCase(id)) {
                    name = json.getString("listCode");
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getSavedLocation() {
        return getStringData("savedlocation");
    }

    public void setSavedLocation(String loc) {
        setStringData("savedlocation", loc);
    }

    public String getLoginLocation() {
        return getStringData("loginlocation");
    }

    public void setLoginLocation(String loc) {
        setStringData("loginlocation", loc);
    }

    public String getFilerLocation() {
        return getStringData("filterlocation");
    }

    public void setFilterLocation(String loc) {
        setStringData("filterlocation", loc);
    }
    public Boolean isFirstLaunch() {
//        return getBoolData("firstlaunch");
        return getBoolData("newfirstlaunch");
    }

    public void setFirstLaunch(Boolean value) {
//        setBoolData("firstlaunch", value);
        setBoolData("newfirstlaunch", value);
    }
}
