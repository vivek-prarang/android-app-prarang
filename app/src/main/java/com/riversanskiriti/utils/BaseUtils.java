package com.riversanskiriti.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.config.UrlConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class BaseUtils {
    Context mContext;
    AppCompatActivity mActivity;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Singleton Instance
    private static BaseUtils sInstance;

    public static synchronized BaseUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BaseUtils(context);
        }
        return sInstance;
    }

    public BaseUtils(Context context) {
        mContext = context;
        try {
            mActivity = (AppCompatActivity) context;
        } catch (Exception ee) {
            mActivity = null;
        }
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
    }

    //Check Network Avaiblity
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    public void setStringData(String key, String value) {
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringData(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setIntData(String key, int value) {
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getIntData(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void setBoolData(String key, Boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getBoolData(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void showToast(String message) {
        Toast.makeText(mContext, message + "", Toast.LENGTH_LONG).show();
    }

    public String getConvertedDateTime(String timestamp, boolean isDate) {
        long mTime = Long.parseLong(timestamp);
        mTime = mTime * 1000;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mTime);
        Date date = cal.getTime();
        if (isDate) {
            return new SimpleDateFormat("dd MMM yyy").format(date);
        }
        return new SimpleDateFormat("HH:mm").format(date);
    }


    public void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }


    public String getPrimaryEmailID() {
//        AccountManager accountManager = AccountManager.get(mContext);
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            return null;
//        }
//        Account[] accounts = accountManager.getAccountsByType("com.google");
//        Account account;
//        if (accounts.length > 0) {
//            return accounts[0].name;
//        } else {
//            return null;
//        }
        return null;
    }

    public void hideAppLauncherIcon() {
        PackageManager pm = mContext.getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(mActivity.getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }


    public String getIEMINumber() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public String getSerialNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }


    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

//    public String getCountryZipCode(String CountryID) {
//        String CountryZipCode = null;
//        // getNetworkCountryIso
//        if (CountryID == null) {
//            TelephonyManager manager = (TelephonyManager) mContext
//                    .getSystemService(Context.TELEPHONY_SERVICE);
//            CountryID = manager.getSimCountryIso().toUpperCase();
//        }
//        String[] countryArray = mContext.getResources().getStringArray(R.array.country_data);
//        for (int i = 0; i < countryArray.length; i++) {
//            String[] g = countryArray[i].split("# ");
//            if (g[2].trim().equals(CountryID.trim())) {
//                //CountryZipCode = g[1] + "(" + g[0].trim() + ")";
//                CountryZipCode = g[1];
//                break;
//            }
//        }
//        return CountryZipCode;
//    }

    public int generateRandomInt() {
        return (int) (Math.random() * 9000) + 1000;
    }

    public void sendVerificationSMS(String number, int code) {
        String smsMessage = "Hi your InTouch verification code is " + code;
        SmsManager.getDefault().sendTextMessage(number, null, smsMessage,
                null, null);
    }

    public void replaceFragment(int redId, Fragment nextFragment) {
        FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        transaction.replace(redId, nextFragment);
        transaction.commitAllowingStateLoss();
    }

    public int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void setMoreTextEllipsis(final TextView textView, final String text, final int maxLine) {
        textView.setText(text);
        textView.post(new Runnable() {
            @Override
            public void run() {
                // Past the maximum number of lines we want to display.
                if (textView.getLineCount() > maxLine) {
                    int lastCharShown = textView.getLayout().getLineVisibleEnd(maxLine - 1);
                    textView.setMaxLines(maxLine);
                    String suffix = "... more";
                    String actionDisplayText = text.substring(0, lastCharShown - suffix.length()) + suffix;
                    textView.setText(actionDisplayText);
                }
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public String encodeToUTF8(String value) {
//        try {
//            value = URLDecoder.decode(URLEncoder.encode(value, "iso8859-1"), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return value;
    }

    public void sharePost(ImageView imageView, String text, String geoCode, String chittiId) {
//        Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        String pathofBmp = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bm, "title", null);
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, "PRARANG");
//        intent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(pathofBmp));
        intent.setType("text/plain");
        mContext.startActivity(Intent.createChooser(intent, "Share"));

        // Added by Pawan Dey
        Network network = new Network(mContext);
        String UserCity = AppUtils.getInstance(mContext).getGeographyId().substring( 0, AppUtils.getInstance(mContext).getGeographyId().indexOf(","));
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("subscriberId", AppUtils.getInstance(mContext).getSubscriberId());
        map2.put("name", AppUtils.getInstance(mContext).getName());
        map2.put("chittiId", chittiId);
        map2.put("Share", "1");
        map2.put("geographyCode", geoCode);
        map2.put("userCity", UserCity);
        Log.d("ShareMaps-",map2.toString());
        network.makeRequest(map2, UrlConfig.postShareUrl);
    }

    public void shareApp(String text) {
        String packageName = "https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text + "\n" + packageName);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    public void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            mContext.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
        }
    }

    public void vibrate() {
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(50);
    }

    public byte[] compressImage(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int compression = 100;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length / 1024;

        while (lengthbmp > 100) {
            compression = (compression / 4) * 3;
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compression, stream);
            imageInByte = stream.toByteArray();
            lengthbmp = imageInByte.length / 1024;
        }
        return imageInByte;
    }

    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                     final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "View More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }

    public int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res;
    }

}
