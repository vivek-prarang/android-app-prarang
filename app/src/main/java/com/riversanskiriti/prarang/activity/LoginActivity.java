package com.riversanskiriti.prarang.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.riversanskiriti.prarang.AppController;
import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.StaticClass;
import com.riversanskiriti.prarang.config.UrlConfig;
import com.riversanskiriti.utils.BaseUtils;
import com.riversanskiriti.utils.Lang;
import com.riversanskiriti.utils.Network;
import com.riversanskiriti.utils.Permission;
import com.riversanskiriti.utils.Valid;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Network.Listener {

    private Toolbar toolbar;
    //    private AdView mAdView;
    private TextView countryCode, timerText;
    private LinearLayout spinnerLayout;
    private EditText name, number, otp;
    private Button submit, submitOTP;
    private FrameLayout otpFrame, loginFrame;
    private View spinnerView1, spinnerView2;
    private Spinner spinner1, spinner2;
    private ArrayList<String> l1, l2, l3, l4;
    private ArrayAdapter<String> da1, da2;
    private BaseUtils baseUtils;
    private AppUtils appUtils;
    private Valid valid;
    private Network network;
    private JSONObject payloadJson;
    private BroadcastReceiver smsBroadcastReceiver;
    private Permission permission;
    private JSONArray cityJSONAarry;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    private void initAdmobBanner() {
//        mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//        mAdView.loadAd(adRequest);
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                mAdView.setVisibility(View.VISIBLE);
//            }
//        });
//    }

    private void initSpinner() {
        da1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, l1);
        da1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        da2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, l3);
        da2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerView1 = findViewById(R.id.spinnerView1);
        spinnerView2 = findViewById(R.id.spinnerView2);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setAdapter(da1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    spinnerView2.setVisibility(View.GONE);
                    return;
                }
                l3.clear();
                l4.clear();
                l3.add(getResources().getString(R.string.filter_city));
                l4.add("");
                try {
                    if (position == (l1.size() - 1)) {
                        l3.add(cityJSONAarry.getJSONObject(cityJSONAarry.length() - 1).getString("cityUnicode"));
                        l4.add(cityJSONAarry.getJSONObject(cityJSONAarry.length() - 1).getString("cityId"));
                    } else {
                        for (int i = 0; i < cityJSONAarry.length(); i++) {
                            l3.add(cityJSONAarry.getJSONObject(i).getString("cityUnicode"));
                            l4.add(cityJSONAarry.getJSONObject(i).getString("cityId"));
                        }
                    }
                } catch (Exception ee) {

                }
                da2 = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_item, l3);
                da2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(da2);
                spinnerView2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(da2);

    }

    private void load() {
        countryCode = (TextView) findViewById(R.id.countryCode);
        timerText = (TextView) findViewById(R.id.timerText);
        spinnerLayout = (LinearLayout) findViewById(R.id.spinnerLayout);

        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);
        otp = (EditText) findViewById(R.id.otp);

        submit = (Button) findViewById(R.id.submitButton);
        submitOTP = (Button) findViewById(R.id.submitOTP);

        otpFrame = (FrameLayout) findViewById(R.id.otpFrame);
        loginFrame = (FrameLayout) findViewById(R.id.loginFrame);

        initToolbar();
//        initAdmobBanner();
        initSpinner();

        submit.setOnClickListener(this);
        submitOTP.setOnClickListener(this);
        timerText.setOnClickListener(this);

        //countryCode.setText("+" + baseUtils.getCountryZipCode(null));
    }

    private void initCities() {
        l1 = new ArrayList<String>();
        l2 = new ArrayList<String>();
        l3 = new ArrayList<String>();
        l4 = new ArrayList<String>();


        l1.add(getResources().getString(R.string.filter_country));
        l2.add("");


        try {
            JSONArray array = new JSONArray(appUtils.getLoginLocation());
            JSONObject json = array.getJSONObject(0);

            JSONArray mainArray = json.getJSONArray("country");
            for (int i = 0; i < mainArray.length(); i++) {
                l1.add(mainArray.getJSONObject(i).getString("countryUnicode"));
                l2.add(mainArray.getJSONObject(i).getString("countryId"));
            }

            json = array.getJSONObject(1);
            cityJSONAarry = json.getJSONArray("city");

        } catch (JSONException e) {
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        baseUtils = new BaseUtils(this);
        valid = new Valid(this);
        network = new Network(this, this);
        appUtils = AppUtils.getInstance(this);
        permission = new Permission(this);
        initCities();
        load();

//        SMS Recived Broadcast
//        smsBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Bundle bundle = intent.getExtras();
//                Object messages[] = (Object[]) bundle.get("pdus");
//                SmsMessage smsMessage[] = new SmsMessage[messages.length];
//                for (int n = 0; n < messages.length; n++) {
//                    smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
//                }
//                //String incomingNumber = smsMessage[0].getOriginatingAddress();
//                String messageBody = smsMessage[0].getMessageBody();
//                if (messageBody.indexOf("verification code") != -1) {
//                    String code = messageBody.substring(messageBody.length() - 6, messageBody.length());
//                    otp.setText(code);
//                }
//            }
//        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean finishCounter = false;
    private CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
        public void onTick(long millisUntilFinished) {
            long remaingTime = millisUntilFinished / 1000;
            if (remaingTime < 10) {
                timerText.setText("00:0" + remaingTime);
            } else {
                timerText.setText("00:" + remaingTime);
            }
        }

        public void onFinish() {
            finishCounter = true;
            timerText.setText(getResources().getString(R.string.text_resend));
        }
    };


    private void onClickSubmitButton() {


//        if (!permission.chckSelfPermission(Manifest.permission.RECEIVE_SMS)) {
//            permission.requestPermissions(Manifest.permission.RECEIVE_SMS, null);
//            return;
//        }
//        if (!permission.chckSelfPermission(Manifest.permission.READ_SMS)) {
//            permission.requestPermissions(Manifest.permission.READ_SMS, null);
//            return;
//        }
        if (!valid.isValidNumber(number)) {

            Toast.makeText(this, getResources().getString(R.string.invalid_mobileno), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!valid.isValidName(name)) {
            return;
        }
        if ((spinner1.getSelectedItemPosition() == 0) || (spinner2.getSelectedItemPosition() == 0)) {
            Toast.makeText(this, getResources().getString(R.string.msg_selectonegeo), Toast.LENGTH_SHORT).show();
            return;
        }

        String geographyid = l4.get(spinner2.getSelectedItemPosition()) + "," + l2.get(spinner1.getSelectedItemPosition());  // city+counrty
        String geographyname = l3.get(spinner2.getSelectedItemPosition()) + "," + l1.get(spinner1.getSelectedItemPosition());

        appUtils.setName(name.getText().toString());
        appUtils.setNumber(countryCode.getText().toString() + "" + number.getText().toString());
        appUtils.setGeographyId(geographyid);
        appUtils.setGeographyName(geographyname);

//        Log.i("GCMKEY", FirebaseMessaging.getInstance().getToken().toString());
//        Log.i("GCMKEY1", FirebaseInstanceId.getInstance().getToken().toString());

        String gcmkey = FirebaseInstanceId.getInstance().getToken().toString();
        appUtils.setFirebaseToken(gcmkey);

        network.setShowProgress(true);
        HashMap<String, String> map = new HashMap();
        map.put("name", name.getText().toString());
        map.put("mobile", countryCode.getText().toString() + "" + number.getText().toString());
        map.put("userLocation", geographyid);
        map.put("languageCode", new Lang(this).getAppLanguage());
        map.put("gcmKey", appUtils.getFirebaseToken());
        map.put("otpToBeSend", "1");
        map.put("isHindi", "false");
        map.put("isEnglish", "true");
        Log.d("LoginResMap-",map.toString());

//        Log.i("GCMKEY2", appUtils.getFirebaseToken());

        network.makeRequest(map, UrlConfig.login);
        registerReceiver(smsBroadcastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitButton:
                onClickSubmitButton();
                break;
            case R.id.submitOTP:
                try {
                    String payloadCode = payloadJson.getString("otp");
                    Toast.makeText(this, payloadCode, Toast.LENGTH_SHORT).show();

                    Log.i("","Enter OTP = " + otp.getText().toString());
                    Log.i("","BackEnd OTP = " + payloadCode);


                    if (otp.getText().toString().equals(payloadCode)) {
                        countDownTimer.cancel();
                        appUtils.setSubscriberId(payloadJson.getString("subscriberId"));
                        appUtils.setProfileUrl(payloadJson.getString("profilePicUrl"));

                        StaticClass.subsId = appUtils.getSubscriberId();
                        StaticClass.userCity = appUtils.getGeographyId().substring( 0, appUtils.getGeographyId().indexOf(","));

                        // code added by Pawan
                        callAppUsage(appUtils.getSubscriberId(),"Start", appUtils.getGeographyId().substring( 0, appUtils.getGeographyId().indexOf(",")));

                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        otp.setError(Html
                                .fromHtml("<font color='red'>Invalid</font>"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.timerText:
                if (finishCounter) {
                    onClickSubmitButton();
                }
                break;
        }
    }

    public void callAppUsage(final String subscriberId, final String flag, final String userCity) {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, UrlConfig.appUsageURL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AppusageRes--",response.toString());
//                Toast.makeText(LoginActivity.this, "AppusageRes" + response, Toast.LENGTH_SHORT).show();



            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        loginFrame.setClickable(true);
        if (otpFrame.getVisibility() == View.VISIBLE) {
            otpFrame.setClickable(false);
            otpFrame.setVisibility(View.GONE);
            countDownTimer.cancel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            network.cancelRequest();
            unregisterReceiver(smsBroadcastReceiver);
        } catch (Exception ee) {

        }
    }

    private void openOTPScreen() {
        countDownTimer.start();
        loginFrame.setClickable(false);
        otpFrame.setClickable(true);
        otpFrame.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        Log.d("responseLogin",response);
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("responseCode").equals("1")){
                    payloadJson = jsonObject.optJSONObject("Payload");

                    assert payloadJson != null;
                    JSONArray jsonArray = payloadJson.optJSONArray("geographyCode");
                    Log.d("jsonArray1", String.valueOf(jsonArray));

                    ArrayList<Object> arrayList = new ArrayList<>();
                    if(jsonArray!=null){
                        for(int i=0; i<jsonArray.length();i++){
                            arrayList.add(jsonArray.get(i));

                            StringBuilder str = new StringBuilder("");

                            for (Object eachstring : arrayList) {
                                str.append(eachstring).append(",");
                            }

                            String commaseparatedlist = String.valueOf(str);

                            if (commaseparatedlist.length() > 0)
                                commaseparatedlist = commaseparatedlist.substring(0, commaseparatedlist.length() - 1);

                            Log.d("commaseparatedlist",commaseparatedlist); //"c1,c2,c3"
                            baseUtils.setStringData("cityFilter", commaseparatedlist);
                        }
                    }

                    openOTPScreen();
                } else {
                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
                Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onClickSubmitButton();
    }
}
