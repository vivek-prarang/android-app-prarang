

package com.riversanskiriti.prarang.activity;

import static com.android.volley.BuildConfig.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.StaticClass;
import com.riversanskiriti.prarang.adapter.NavItem;
import com.riversanskiriti.prarang.adapter.NavMenuAdapter;
import com.riversanskiriti.prarang.config.UrlConfig;
import com.riversanskiriti.prarang.custom.SelectGeoDialog;
import com.riversanskiriti.prarang.custom.Tasks;
import com.riversanskiriti.prarang.fragment.CityPortalFragment;
import com.riversanskiriti.prarang.fragment.GulakFragment;
import com.riversanskiriti.prarang.fragment.MainPostFragment;
import com.riversanskiriti.prarang.fragment.PrarangFragment;
import com.riversanskiriti.utils.BaseUtils;
import com.riversanskiriti.utils.Lang;
import com.riversanskiriti.utils.Network;
import com.riversanskiriti.utils.Permission;
import com.riversanskiriti.utils.network.Multipart;
import com.theartofdev.edmodo.cropper.CropImage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, Multipart.Listener, Tasks.Listener, LocationListener, NavMenuAdapter.Listener, Network.Listener, ViewPager.OnPageChangeListener, CityPortalFragment.OnFragmentInteractionListener {
    //  private AdView mAdView;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<String> tabs = new ArrayList<>();
    private RecyclerView navRecyclerView;
    private NavMenuAdapter navMenuAdapter;
    private List<NavItem> menuList = new ArrayList<>();
    private LinearLayout spinnerLayout;
    private int[] tabIcons = {R.drawable.ic_tab_patra,
            R.drawable.ic_tab_prarang,
            R.drawable.city,
            R.drawable.ic_tab_gulak};

    private SelectGeoDialog selectGeoDialog;
    private BaseUtils baseUtils;
    private AppUtils appUtils;
    private Network network;
    private Permission permission;
    private Tasks tasks;
    private Multipart multipart;
    private CircleImageView profileImage;
    private FrameLayout editProfileButton;
    private TextView userName, userCity;
    private MainPostFragment mainPostFragment;
    private PrarangFragment prarangFragment;
    private GulakFragment gulakFragment;
    private CityPortalFragment cityPortalFragment;
    private FirebaseAnalytics firebaseAnalytics;
    private String geographyid = null;
    private LocationManager locationManager;

    // For Custom Multiple Selection Filter
    CheckBox cb_all;
    String userCityVal = "10";

    private void initToolbar() {

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.layout_filtertoolbarview, null);
        toolbar.addView(mCustomView, new Toolbar.LayoutParams(Gravity.CENTER_VERTICAL | Gravity.RIGHT));

        mCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGeoDialog = new SelectGeoDialog(HomeActivity.this);
                selectGeoDialog.show();
            }
        });

        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    public void setAllChecked(boolean check){
        if(cb_all!=null) {
            cb_all.setChecked(check);
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mainPostFragment = new MainPostFragment();
        prarangFragment = new PrarangFragment();
        cityPortalFragment = new CityPortalFragment();
        gulakFragment = new GulakFragment();

        adapter.addFrag(mainPostFragment, tabs.get(0));
        adapter.addFrag(prarangFragment, tabs.get(1));
        adapter.addFrag(cityPortalFragment, tabs.get(2));
        adapter.addFrag(gulakFragment, tabs.get(3));

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        for (int i = 0; i < tabs.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            title.setText(tabs.get(i));

            icon.setImageResource(tabIcons[i]);
            tabLayout.getTabAt(i).setCustomView(view);
        }
    }

//    private void initAdmobBanner() {
//        mAdView = (AdView) findViewById(R.id.adView);
//        mAdView.setVisibility(View.GONE);
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

    private void loadProfileData(boolean loadProfileData) {
        if (loadProfileData) {
            Glide.with(HomeActivity.this).load(appUtils.getProfileUrl()).placeholder(R.drawable.ic_userblank).dontAnimate().into(profileImage);
            userName.setText(appUtils.getName());
        }
        String[] geoNames = appUtils.getGeographyName().split(",");
        userCity.setText(geoNames[0]);
    }

    private void initNavMenu() {
        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        editProfileButton = (FrameLayout) findViewById(R.id.editProfileButton);

        profileImage.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);

        userName = (TextView) findViewById(R.id.userName);
        userCity = (TextView) findViewById(R.id.userCity);
        loadProfileData(true);

        navMenuAdapter = new NavMenuAdapter(this, menuList, this);
        menuList.clear();
        navRecyclerView = (RecyclerView) findViewById(R.id.navRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        navRecyclerView.setLayoutManager(mLayoutManager);
        navRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navRecyclerView.setAdapter(navMenuAdapter);

        //menuList.add(new NavItem(getResources().getString(R.string.text_viewhindi), appUtils.getIsHindi(), R.drawable.ic_hindiview, 1));
        //menuList.add(new NavItem(getResources().getString(R.string.text_viewenglish), appUtils.getIsEnglish(), R.drawable.ic_englishview, 1));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_changelang), null, R.drawable.ic_languagechange, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_aboutus), null, R.drawable.ic_feedback, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_apptour), null, R.drawable.ic_info, 0));

        menuList.add(new NavItem(getResources().getString(R.string.text_menu_city_portals), null, R.drawable.blank, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_jaunpur), null, R.drawable.ic_bhugol, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_lucknow), null, R.drawable.ic_bhugol, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_meerut), null, R.drawable.ic_bhugol, 0));
        menuList.add(new NavItem(getResources().getString(R.string.text_menu_rampur), null, R.drawable.ic_bhugol, 0));

        navMenuAdapter.notifyDataSetChanged();
    }

    private void loadActivity() {
        tabs.clear();
        tabs.add(getResources().getString(R.string.tab_patra));
        tabs.add(getResources().getString(R.string.tab_prarang));
        tabs.add(getResources().getString(R.string.tab_city_portals));
        tabs.add(getResources().getString(R.string.tab_gulak));


        permission = new Permission(this);
        initToolbar();
        initViewPager();
        initTabLayout();
//        initAdmobBanner();
        initNavMenu();

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // default
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission.requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, null);
        } else {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                onLocationChanged(location);
            }
        }
        // Obtain the Firebase Analytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("id", appUtils.getSubscriberId());
        bundle.putString("name", appUtils.getName());
        if (appUtils.getSavedLocation() != null) {
            bundle.putString("location", appUtils.getSavedLocation());
        }
        firebaseAnalytics.logEvent("PRARANG", bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(getResources().getString(R.string.title_home));
        baseUtils = new BaseUtils(this);
        appUtils = AppUtils.getInstance(this);
        network = new Network(this, this);
        tasks = new Tasks(this, this);
        multipart = new Multipart(this, this);


        //Clear data
        //baseUtils.setStringData("citypost", null);
        //baseUtils.setStringData("regionpost", null);
        //baseUtils.setStringData("countrypost", null);


        // String UserCity = appUtils.getGeographyId().substring( 0, appUtils.getGeographyId().indexOf(","));

        // App Usage Time For Start - Added by Pawan 06 Aprl 22
//        HashMap<String, String> map2 = new HashMap<>();
//        map2.put("subscriberId", appUtils.getSubscriberId());
//        map2.put("flag", "Start");
//        map2.put("userCity", UserCity);
//        network.makeRequest(map2, UrlConfig.appUsageURL);
//        Log.d("appUsageStart-",map2.toString());

        //  playStoreURL
        HashMap<String, String> map = new HashMap<>();
        map.put("id", getPackageName());
        network.makeRequest(map, UrlConfig.playStoreURL);

        loadActivity();

    /*    if (!appUtils.isFirstLaunch()) {
            appUtils.setFirstLaunch(true);
            startActivity(new Intent(this, AppTourActivity.class));
        }*/
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    public void appInResumeState() {
//        Toast.makeText(this,"In Foreground",Toast.LENGTH_LONG).show();
//        callAppUsage(appUtils.getSubscriberId(),"Start",userCityVal);
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    public void appInPauseState() {
//        Toast.makeText(this,"In Background",Toast.LENGTH_LONG).show();
//        callAppUsage(appUtils.getSubscriberId(),"End",userCityVal);
//    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    private void onAppBackgrounded() {
//        Log.d("AppController", "App in background");
////        userCityVal = appUtils.getGeographyId().substring( 0, appUtils.getGeographyId().indexOf(","));
//        Log.d("userCityVal1-",userCityVal);
//        callAppUsage(appUtils.getSubscriberId(),"End",userCityVal);
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    private void onAppForegrounded() {
//        Log.d("AppController", "App in foreground");
////        userCityVal = appUtils.getGeographyId().substring( 0, appUtils.getGeographyId().indexOf(","));
//        Log.d("userCityVal2-",userCityVal);
//        callAppUsage(appUtils.getSubscriberId(),"Start",userCityVal);
//    }

//    public void callAppUsage(final String subscriberId, final String flag, final String userCityVal) {
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        StringRequest request = new StringRequest(Request.Method.POST, UrlConfig.appUsageURL, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("AppusageRes--",response.toString());
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(HomeActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("subscriberId", subscriberId);
//                params.put("flag", flag);
//                params.put("userCity", userCityVal);
//                Log.d("params-",params.toString());
//                return params;
//            }
//        };
//        queue.add(request);
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        String UserCity = appUtils.getGeographyId().substring( 0, appUtils.getGeographyId().indexOf(","));
//        // App Usage Time For End - Added by Pawan 06 Aprl 22
//        HashMap<String, String> map2 = new HashMap<>();
//        map2.put("subscriberId", appUtils.getSubscriberId());
//        map2.put("flag", "End");
//        map2.put("userCity", UserCity);
//        network.makeRequest(map2, UrlConfig.appUsageURL);
//
//    }

    private boolean shouldExit = false;
    private Handler exitHandler;
    private Runnable exitRunnable;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (shouldExit) {
                if (exitHandler != null) {
                    exitHandler.removeCallbacks(exitRunnable);
                }
                super.onBackPressed();
            } else {
                shouldExit = true;
                Toast.makeText(this, getResources().getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
                exitHandler = new Handler();
                exitRunnable = new Runnable() {
                    @Override
                    public void run() {
                        shouldExit = false;
                    }
                };
                exitHandler.postDelayed(exitRunnable, 3000);
            }
//            if (viewPager.getCurrentItem() != 0) {
//                viewPager.setCurrentItem(0);
//            } else {
//                super.onBackPressed();
//            }
        }
    }


    @Override
    public void onClickNavMenu(View view, int position) {

        //TODO change nav
        switch (position) {
//            case 0:
//                if (appUtils.getIsHindi()) {
//                    appUtils.setIsHindi(false);
//                    appUtils.setIsEnglish(true);
//                } else {
//                    appUtils.setIsHindi(true);
//                    appUtils.setIsEnglish(false);
//                }
//                makeReqCall(null);
//                break;
//            case 1:
//                if (appUtils.getIsEnglish()) {
//                    appUtils.setIsEnglish(false);
//                    appUtils.setIsHindi(true);
//                } else {
//                    appUtils.setIsEnglish(true);
//                    appUtils.setIsHindi(false);
//                }
//                makeReqCall(null);
//                break;
            case 0:
                Intent intent = new Intent(this, SelectLangActivity.class);
                intent.putExtra("ChangeLanguage", true);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case 2:
                startActivity(new Intent(this, AppTourActivity.class));
                break;

            case 4:
//                Intent rampurIntent = new Intent(Intent.ACTION_VIEW);
//                rampurIntent.setData(Uri.parse(getString(R.string.rampur_portal_links)));
//                startActivity(rampurIntent);
                Intent jaunpurIntent = new Intent(Intent.ACTION_VIEW);
                jaunpurIntent.setData(Uri.parse(getString(R.string.jaunpur_portal_links)));
                startActivity(jaunpurIntent);
                break;
            case 5:
//                Intent meerutIntent = new Intent(Intent.ACTION_VIEW);
//                meerutIntent.setData(Uri.parse(getString(R.string.meerut_portal_links)));
//                startActivity(meerutIntent);
                Intent lucknowIntent = new Intent(Intent.ACTION_VIEW);
                lucknowIntent.setData(Uri.parse(getString(R.string.lucknow_portal_links)));
                startActivity(lucknowIntent);
                break;
            case 6:
//                Intent lucknowIntent = new Intent(Intent.ACTION_VIEW);
//                lucknowIntent.setData(Uri.parse(getString(R.string.lucknow_portal_links)));
//                startActivity(lucknowIntent);
                Intent meerutIntent = new Intent(Intent.ACTION_VIEW);
                meerutIntent.setData(Uri.parse(getString(R.string.meerut_portal_links)));
                startActivity(meerutIntent);
                break;
            case 7:
//                Intent jaunpurIntent = new Intent(Intent.ACTION_VIEW);
//                jaunpurIntent.setData(Uri.parse(getString(R.string.jaunpur_portal_links)));
//                startActivity(jaunpurIntent);
                Intent rampurIntent = new Intent(Intent.ACTION_VIEW);
                rampurIntent.setData(Uri.parse(getString(R.string.rampur_portal_links)));
                startActivity(rampurIntent);
                break;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAllData() {
        if (geographyid != null) {
            AppUtils.getInstance(this).setGeoFilterId(geographyid);
            Log.d("geographyidall",geographyid);
        }
        navMenuAdapter.notifyDataSetChanged();
        loadProfileData(false);

        Log.d("current Page","--"+viewPager.getCurrentItem());
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0, true);
        }
        mainPostFragment.makeNetworkCall();
        mainPostFragment.reloadFragment();
        prarangFragment.loadFragment();

    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        String currentVersion = getAppVersionName();
        if (url.contains(UrlConfig.playStoreURL)) {
            if (response != null) {
                String key = "softwareVersion";
                int index = response.indexOf(key);
                if (index != -1) {
                    index = index + key.length() + 2;
                    String newVersion = response.substring(index, index + 8);
                    newVersion = newVersion.trim();
                    int status = baseUtils.compareVersionNames(currentVersion, newVersion);
                    if (status != 0) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Update available");
                        builder.setMessage(getResources().getString(R.string.app_name) + " version " + newVersion + " is available on playstore.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                baseUtils.rateApp();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            }
            return;
        }

        // To check response for App Usage Url
//        if (url.contains(UrlConfig.appUsageStartURL)) {
//            if (response != null) {
//                try {
//                    response = response.substring(response.indexOf("{"), response.length());
//                    JSONObject jsonObject = new JSONObject(response);
//                    if (jsonObject.getString("responseCode").equals("1")) {
//                        Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_LONG).show();
//                }
//            }}

        if (response != null) {
            try {
                response = response.substring(response.indexOf("{"), response.length());
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("responseCode").equals("1")) {
                    loadAllData();
                } else {
                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
            loadAllData();
        }
    }

    private String getAppVersionName() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }


    @Override
    public void onNetworkError(String error, String url) {
        if (url.contains(UrlConfig.playStoreURL)) {
            return;
        }
        loadAllData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }



    @Override
    public void onPageSelected(final int position) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case 0:
                        mainPostFragment.makeNetworkCall();
                        StaticClass.isBalance = false;
                        Log.d("isBalance-", String.valueOf(StaticClass.isBalance));
                        break;
                    case 1:
//                        prarangFragment.loadFragmentData(true);
                        prarangFragment.loadFragment();
                        StaticClass.isBalance = true;
                        Log.d("isBalance-", String.valueOf(StaticClass.isBalance));
                        break;

                    // Before in Older Version
//                    case 2:
//                        gulakFragment.loadFragmentData();
//                        break;
//                    case 3:
//                        break;

                    // Code Added by Pawan on 12 Apr 22 (fix : save to bank data not populating instantly)
                    case 2:
                        cityPortalFragment.newInstance("","");
                        StaticClass.isBalance = false;
                        Log.d("isBalance-", String.valueOf(StaticClass.isBalance));
                        break;
                    case 3:
                        gulakFragment.loadFragment();
                        StaticClass.isBalance = false;
                        Log.d("isBalance-", String.valueOf(StaticClass.isBalance));
                        break;
                }
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onLocationChanged(Location location) {
        appUtils.setSavedLocation(location.getLatitude() + "," + location.getLongitude());
    }

//    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileImage:
                //TODO
                ArrayList<String> list = new ArrayList<>();
                list.add(appUtils.getProfileUrl());
                Intent intent = new Intent(this, PostImagesActivity.class);
                intent.putStringArrayListExtra("images", list);
                startActivity(intent);
                break;
            case R.id.editProfileButton:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] permissionsToCheck = {Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES};
                    if (!permission.checkSelfPermissionMultiple(permissionsToCheck)) {
                        permission.setRequestCode(1001);
                        permission.requestPermissionMultiple(permissionsToCheck, null);
                    } else {
                        openGallery();
                    }
                } else {
                    if (!permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        permission.setRequestCode(1001);
                        permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
                    } else {
                        openGallery();
                    }
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void makeReqCall(String geoIds) {
        geographyid = geoIds;
        HashMap<String, String> map = new HashMap();
        map.put("name", appUtils.getName());
        map.put("mobile", "+91");
        map.put("gcmKey", appUtils.getFirebaseToken());
        map.put("languageCode", new Lang(this).getAppLanguage());
        map.put("otpToBeSend", "0");
        if (geographyid == null) {
            map.put("geographyid", appUtils.getGeoFilterId());
        } else {
            map.put("geographyid", geographyid);
            AppUtils.getInstance(this).setGeoFilterId(geographyid);
        }
        network.setShowProgress(true);
        Log.d("afterfiltermap",map.toString());
        network.makeRequest(map, UrlConfig.login);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }

    boolean isFirstResume = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
        } else {
            if (mainPostFragment != null) {
                //mainPostFragment.makeNetworkCall();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1111);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //If permission is granted
        if (grantResults.length > 0) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted && !permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                openGallery();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case 1111:
                    CropImage.activity(data.getData()).setAllowRotation(false).setAllowFlipping(false).setFixAspectRatio(true).setActivityTitle("Profile")
                            .start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    tasks.executeCompressImage(result.getUri());
                    break;
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTaskPostExecute(Object object) throws JSONException {

        byte[] bytes = (byte[]) object;
        // Using encode() added by munendra
//       String result = Base64.getdncoder().encodeToString(bytes);
        byte[] encode = Base64. getEncoder(). encode(bytes);



        String result = new String(encode);
        Log.i("","jsonnnnn -   " + result);
        System.out.println(result);
//        String ss = "{"+result+"}";
//        Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
//        byte[] bytes = (byte[]) object;
        if (result == null) {
            Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
        } else {

//            Gson gson = new Gson();
//            String str =   gson.(result);
//            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();


            Log.i("","jsonmmmm--------- " + result);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("subscriberId", appUtils.getSubscriberId());
            map.put("result",result);
            multipart.setShowProgress(true);
            multipart.setMap(map);
//            StringBuilder b = new StringBuilder();
//            for(int i=0;i<bytes.length;i++){
//
//                b.append(bytes[i]+",");
//            }
//            Toast.makeText(this, "mmmmmmm---- " + b.toString(), Toast.LENGTH_SHORT).show();
            Log.i("","mmmmmmmmmmmm ----- " + bytes.toString());

            multipart.setUrl(UrlConfig.profilePic);
            multipart.setBytes(bytes, "profilePic");
            multipart.execute();

            JSONObject jsonData = new JSONObject();
            jsonData.put("name",appUtils.getSubscriberId());
            jsonData.put("address", result);

            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            network.makeRequest(map, UrlConfig.profilePic);
//            registerReceiver(, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }
    }

    @Override
    public void onMultipartSuccess(String response, String url) {
        try {
            Log.e("Response", response);
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                if (json.has("Payload")) {
                    json = json.getJSONObject("Payload");
                    String profilePicUrl = json.getString("profilePicUrl");
                    Log.i("","wswsws" + profilePicUrl);
                    appUtils.setProfileUrl(profilePicUrl);
                    Glide.with(this).load(profilePicUrl).placeholder(R.drawable.ic_userblank).dontAnimate().into(profileImage);
                } else {
                    Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMultipartProgress(int progress) {
    }

    @Override
    public void onMultipartError(String error, String url) {
        Toast.makeText(this, error + "", Toast.LENGTH_LONG).show();
    }

    public void onRampurClick(View view) {
        Intent rampurIntent = new Intent(Intent.ACTION_VIEW);
        rampurIntent.setData(Uri.parse(getString(R.string.rampur_portal_links)));
        startActivity(rampurIntent);
    }

    public void onMeerutClick(View view) {
        Intent meerutIntent = new Intent(Intent.ACTION_VIEW);
        meerutIntent.setData(Uri.parse(getString(R.string.meerut_portal_links)));
        startActivity(meerutIntent);
    }

    public void onLucknowClick(View view) {
        Intent lucknowIntent = new Intent(Intent.ACTION_VIEW);
        lucknowIntent.setData(Uri.parse(getString(R.string.lucknow_portal_links)));
        startActivity(lucknowIntent);
    }

    public void onJaunpurClick(View view) {
        Intent jaunpurIntent = new Intent(Intent.ACTION_VIEW);
        jaunpurIntent.setData(Uri.parse(getString(R.string.jaunpur_portal_links)));
        startActivity(jaunpurIntent);
    }
}

