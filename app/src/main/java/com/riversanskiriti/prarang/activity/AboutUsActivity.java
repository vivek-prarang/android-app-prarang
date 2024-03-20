package com.riversanskiriti.prarang.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.utils.BaseUtils;
import com.riversanskiriti.utils.Lang;
import com.riversanskiriti.utils.Permission;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
//    private AdView mAdView;
    private View shareButtonView, rateButtonView;
    private BaseUtils baseUtils;
    private Permission permission;
    private Lang lang;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_aboutus));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        baseUtils = new BaseUtils(this);
        permission = new Permission(this);
        lang = new Lang(this);
        initToolbar();

        WebView mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/html/" + lang.getAppLanguage() + ".html");

        shareButtonView = findViewById(R.id.shareButtonView);
        rateButtonView = findViewById(R.id.rateButtonView);
        shareButtonView.setOnClickListener(this);
        rateButtonView.setOnClickListener(this);

//        initAdmobBanner();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareButtonView:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] permissionsToCheck = {Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES};
                    if (!permission.checkSelfPermissionMultiple(permissionsToCheck)) {
                        permission.requestPermissionMultiple(permissionsToCheck, null);
                        return;
                    }
                } else {
                    if (!permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
                        return;
                    }
                }
                baseUtils.shareApp(getResources().getString(R.string.sharetext_aboutus));
                break;
            case R.id.rateButtonView:
                baseUtils.rateApp();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                baseUtils.shareApp(getResources().getString(R.string.sharetext_aboutus));
            }
        }
    }
}
