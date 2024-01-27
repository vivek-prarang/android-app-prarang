package com.riversanskiriti.utils;

/**
 * Created by arpit on 25/11/16.
 */

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.ScrollView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Valid {
    Context mContext;

    public Valid(Context context) {
        mContext = context;
    }

    //Singleton Instance
    private static Valid sInstance;

    public static synchronized Valid getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Valid(context);
        }
        return sInstance;
    }

    ScrollView mScrollview = null;

    public Valid(Context context, ScrollView scrollview) {
        mScrollview = scrollview;
    }

    Spanned passwordMessage = Html
            .fromHtml("<font color='red'>Must have min 6 characters at least 1 Alphabet, 1 Number and 1 Special Character</font>");
    Spanned emailMessage = Html
            .fromHtml("<font color='red'>Invalid Email</font>");
    Spanned invalidMessage = Html
            .fromHtml("<font color='red'>Invalid</font>");
    Spanned passwordMatchMessage = Html
            .fromHtml("<font color='red'>Passwords not match.</font>");

    // Check Email Validation
    public boolean isValidEmail(EditText edt) {
        String value = edt.getText().toString();
        boolean status = android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches();
        if (!status) {
            edt.setError(emailMessage);
        }
        return status;
    }

    // Check Password Validation
    public boolean isValidPassword(EditText edt) {
        String value = edt.getText().toString();
        String passwordReg = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{6,}$";
        Pattern pattern = Pattern.compile(passwordReg);
        Matcher matcher = pattern.matcher(value);
        boolean status = matcher.matches();
        if (!status) {
            edt.setError(passwordMessage);
        }
        return status;
    }

    public boolean isValidName(EditText edt) {
        String value = edt.getText().toString().trim();
        if (value.length() == 0) {
            edt.setError(invalidMessage);
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidNumber(EditText edt) {
        String value = edt.getText().toString().trim();

        if(value.length() != 10)
            return false;

        boolean status = value.matches("[0-9]+");
        if (!status) {
            edt.setError(invalidMessage);
            return false;
        } else {
            return true;
        }
    }


    public boolean isMatch(EditText edt1, EditText edt2) {
        String value1 = edt1.getText().toString();
        String value2 = edt2.getText().toString();
        if (!value1.matches(value2)) {
            edt2.setError(passwordMatchMessage);
            return false;
        }
        return true;
    }
}

