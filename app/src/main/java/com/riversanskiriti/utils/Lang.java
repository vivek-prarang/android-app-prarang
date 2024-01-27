package com.riversanskiriti.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

/**
 * Created by ARPIT on 19-02-2017.
 */

public class Lang {
    Context mContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Locale locale;
    Configuration config;
    Listener langListener;

    public Lang(Context context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences("MySharedPref",MODE_PRIVATE);
    }

    public Lang(Context context, Listener listener) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences("MySharedPref",MODE_PRIVATE);
        langListener = listener;
    }

    private void setStringData(String key, String value) {
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getStringData(String key) {
        return sharedPreferences.getString(key, null);
    }

    public String getAppLanguage() {
        String cLang = getStringData("lang");
        if (cLang == null) {
            cLang = "en";
        }
        return cLang;
    }

    public void setAppLanguage(String value) {
        if (value == null) {
            value = getStringData("lang");
        } else {
            if (value.equals(getStringData("lang"))) {
                if (langListener != null) {
                    langListener.onLanguageChange();
                }
            }
        }
        if (value != null) {
            locale = new Locale(value);
            Locale.setDefault(locale);
            config = new Configuration();
            config.locale = locale;
            mContext.getResources().updateConfiguration(config,
                    mContext.getResources().getDisplayMetrics());
            setStringData("lang", value);
        }
        if (langListener != null) {
            langListener.onLanguageChange();
        }
    }

    public void setAppLanguage(String value, boolean temp) {
        if (value == null) {
            value = getStringData("lang");
        } else {
            if (value.equals(getStringData("lang"))) {
                if (langListener != null) {
                    langListener.onLanguageChange();
                }
            }
        }
        if (value != null) {
            locale = new Locale(value);
            Locale.setDefault(locale);
            config = new Configuration();
            config.locale = locale;
            mContext.getResources().updateConfiguration(config,
                    mContext.getResources().getDisplayMetrics());
            if (!temp) {
                setStringData("lang", value);
            }
        }
        if (!temp) {
            if (langListener != null) {
                langListener.onLanguageChange();
            }
        }
    }

    public interface Listener {
        public void onLanguageChange();
    }

}
