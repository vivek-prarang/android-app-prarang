package com.riversanskiriti.prarang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.riversanskiriti.prarang.activity.HomeActivity;
import com.riversanskiriti.prarang.activity.PostActivity;
import com.riversanskiriti.prarang.activity.SelectLangActivity;
import com.riversanskiriti.prarang.adapter.MainPost;
import com.riversanskiriti.prarang.adapter.PostItem;
import com.riversanskiriti.prarang.fcm.Config;
import com.riversanskiriti.prarang.fcm.NotificationUtils;
import com.riversanskiriti.utils.BaseUtils;
import com.riversanskiriti.utils.Lang;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private Context mContext = SplashActivity.this;
    private Handler handler;
    private Lang lang;
    private BaseUtils baseUtils;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView version;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            redirectToApp();
        }
    };

    private void redirectToApp() {
        Log.e("token", AppUtils.getInstance(mContext).getFirebaseToken() + "");
        if (AppUtils.getInstance(mContext).getSubscriberId() == null) {
            startActivity(new Intent(mContext, SelectLangActivity.class));
            finish();
        } else {
            Intent intent = new Intent(mContext, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        baseUtils = new BaseUtils(this);
        lang = new Lang(this, null);
        lang.setAppLanguage(lang.getAppLanguage());
        version = (TextView) findViewById(R.id.version);
        version.setText("v" + BuildConfig.VERSION_NAME);
        try {
            MainPost mainPost = new MainPost();
            ArrayList<PostItem> list = new ArrayList<PostItem>();

            Bundle bundle = getIntent().getExtras();
//                JSONObject json = new JSONObject(payload);
//                if (json.has("tags") && json.has("image")) {
//                    JSONArray imageArray = json.getJSONArray("image");
//                    JSONArray tagArray = json.getJSONArray("tags");
//                    PostItem item = new PostItem();
//                    item.setName(json.getString("chittiname"));
//                    item.setId(json.getString("chittiId"));
//                    item.setDescription(json.getString("description"));
//                    item.setLiked(json.getString("isLiked"));
//                    item.setTotalLike(json.getInt("totalLike"));
//                    item.setTotalComment(json.getInt("totalComment"));
//                    if (json.has("url")) {
//                        item.setPostUrl(json.getString("url"));
//                    }
//                    item.setDateTime(json.getString("dateOfApprove"));
//                    item.setImageList(imageArray);
//                    item.setTagList(tagArray);
//                    list.add(item);

            if(bundle != null) {

                mainPost.setName(bundle.getString("name"));
                mainPost.setId(Integer.parseInt(bundle.getString("id")));
                mainPost.setList(list);

                Intent intent = new Intent(this, PostActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("item", mainPost);
                bundle.putBoolean("notify", true);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return;
            }
        } catch (Exception ee) {
            Log.e("Exception", ee.getMessage());
        }

        mRegistrationBroadcastReceiver = new

                BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // checking for type intent filter
                        if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                            //Registration Complete

                        } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                            // new push notification is received
                            String message = intent.getStringExtra("message");
                            Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                        }
                    }
                };
        handler = new Handler();
        handler.postDelayed(runnable, 2 * 1000);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
