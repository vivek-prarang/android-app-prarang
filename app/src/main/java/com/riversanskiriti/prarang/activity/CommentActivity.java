package com.riversanskiriti.prarang.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.adapter.CommentAdapter;
import com.riversanskiriti.prarang.adapter.CommentItem;
import com.riversanskiriti.prarang.adapter.PostItem;
import com.riversanskiriti.prarang.config.UrlConfig;
import com.riversanskiriti.prarang.custom.Tasks;
import com.riversanskiriti.utils.BaseUtils;
import com.riversanskiriti.utils.Lang;
import com.riversanskiriti.utils.Network;
import com.riversanskiriti.utils.Permission;
import com.riversanskiriti.utils.network.Multipart;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CommentActivity extends AppCompatActivity implements View.OnClickListener, Tasks.Listener, Network.Listener, Multipart.Listener, CommentAdapter.Listener {

    private Toolbar toolbar;
//    private AdView mAdView;
    private PostItem item;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<CommentItem> list;
    private EditText comment;
    private ProgressBar progressBar;

    private BaseUtils baseUtils;
    private AppUtils appUtils;
    private Network network;
    private Permission permission;
    private Tasks tasks;

    private ImageView attachedImage;
    private FrameLayout attachedFrame, attachView, sendView;
    private byte[] attachedImageBytes;
    private Multipart multipart;
    private int itemPosition;


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_comment));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_cross);
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

    private LinearLayoutManager linearLayoutManager;

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();
        adapter = new CommentAdapter(this, list, this);
        recyclerView.setAdapter(adapter);

        HashMap<String, String> map = new HashMap<>();
        map.put("chittiId", item.getId());
        map.put("languageCode", new Lang(this).getAppLanguage());
        map.put("subscriberId", appUtils.getSubscriberId());
        network.setShowProgress(false);
        network.makeRequest(map, UrlConfig.getChittiComment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Bundle bundle = getIntent().getExtras();
        item = (PostItem) bundle.getSerializable("item");
        itemPosition = bundle.getInt("position");

        comment = (EditText) findViewById(R.id.comment);
        sendView = (FrameLayout) findViewById(R.id.sendView);
        sendView.setOnClickListener(this);
        attachView = (FrameLayout) findViewById(R.id.attachView);
        attachView.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        attachedImage = (ImageView) findViewById(R.id.attachedImage);
        attachedFrame = (FrameLayout) findViewById(R.id.attachedFrame);
        attachedFrame.setOnClickListener(this);

        //Classes
        baseUtils = new BaseUtils(this);
        appUtils = AppUtils.getInstance(this);
        network = new Network(this, this);
        multipart = new Multipart(this, this);
        permission = new Permission(this);
        tasks = new Tasks(this, this);

        initToolbar();
        initRecyclerView();
//        initAdmobBanner();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1111);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //If permission is granted
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case 1111:
                    CropImage.activity(data.getData()).setAllowRotation(false).setAllowFlipping(false).setFixAspectRatio(false).setActivityTitle("Comment")
                            .start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    tasks.executeCompressImage(result.getUri());
                    break;
            }
        }
    }

    private void makeMultipartCall(HashMap<String, String> map, byte[] attachement) {
        multipart.setShowProgress(true);
        multipart.setMap(map);
        multipart.setUrl(UrlConfig.postChittiComment);
        multipart.setBytes(attachement);
        multipart.execute();
        attachedImageBytes = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendView:
                String commentText = comment.getText().toString();
                commentText.trim();
                String UserCity = appUtils.getGeographyId().substring( 0, appUtils.getGeographyId().indexOf(","));
                if (commentText.length() > 0) {
                    comment.setText("");
                    attachedFrame.setVisibility(View.GONE);
                    attachView.setVisibility(View.VISIBLE);

                    // Code Added by Pawan 11 Apr 22
                    HashMap<String, String> map = new HashMap<>();
                    map.put("chittiId", item.getId());
//                    map.put("languageCode", new Lang(this).getAppLanguage());
//                    map.put("name", appUtils.getName());
                    map.put("subscriberId", appUtils.getSubscriberId());
                    map.put("geographyCode", item.getGeoCode());
                    map.put("userCity", UserCity);
                    map.put("comment", commentText);
                    Log.d("commentmaps--",map.toString());

                    if (attachedImageBytes != null) {
                        makeMultipartCall(map, attachedImageBytes);
                    } else {
                        //Add Comment
                        CommentItem commentItem = new CommentItem();
                        commentItem.setSubscriberId(appUtils.getSubscriberId());
                        commentItem.setName(appUtils.getName());
                        commentItem.setChittiId(item.getId());
                        commentItem.setComment(commentText);
                        list.add(0, commentItem);
                        adapter.notifyItemInserted(0);
                        recyclerView.scrollToPosition(0);
                        baseUtils.vibrate();
                        network.makeRequest(map, UrlConfig.postChittiComment);
                    }
                } else {
                    Toast.makeText(this, "Please enter the comment.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.attachedFrame:
                attachedFrame.setVisibility(View.GONE);
                attachView.setVisibility(View.VISIBLE);
                attachedImageBytes = null;
                break;
            case R.id.attachView:
                if (!permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
                } else {
                    openGallery();
                }
                break;
        }
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        progressBar.setVisibility(View.GONE);
        try {
            Log.e("Response", response);
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                if (json.has("Payload")) {
                    if (url.indexOf(UrlConfig.getChittiComment) != -1) {
                        JSONArray array = json.getJSONArray("Payload");
                        for (int i = 0; i < array.length(); i++) {
                            json = array.getJSONObject(i);
                            CommentItem commentItem = new CommentItem();
                            commentItem.setId(json.getString("id"));
                            commentItem.setSubscriberId(json.getString("subscriberId"));
                            commentItem.setName(json.getString("name"));
                            commentItem.setChittiId(json.getString("chittiId"));
                            commentItem.setComment(json.getString("Comment"));
                            if (json.has("profilePic")) {
                                commentItem.setProfilePic(json.getString("profilePic"));
                            }
                            if (json.has("imageUrl")) {
                                commentItem.setImageUrl(json.getString("imageUrl"));
                            }
                            list.add(commentItem);
                        }
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(0);
                    }
                } else {
                    //Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_LONG).show();
                }
            } else {
                //Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            //Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        Toast.makeText(this, "" + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMultipartSuccess(String response, String url) {
        try {
            Log.e("Response", response);
            //response = response.substring(response.indexOf("{"), response.length());
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                if (json.has("Payload")) {
                    json = json.getJSONObject("Payload");
                    CommentItem commentItem = new CommentItem();
                    commentItem.setId(item.getId());
                    commentItem.setSubscriberId(json.getString("subscriberId"));
                    commentItem.setName(json.getString("name"));
                    commentItem.setChittiId(json.getString("chittiId"));
                    commentItem.setComment(json.getString("Comment"));
                    if (json.has("profilePic")) {
                        commentItem.setProfilePic(json.getString("profilePic"));
                    }
                    if (json.has("imageUrl")) {
                        commentItem.setImageUrl(json.getString("imageUrl"));
                    }
                    list.add(0, commentItem);
                    adapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                    baseUtils.vibrate();
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

    @Override
    public void onClickRecyclerView(View v, int position) {
        ArrayList<String> imageList = new ArrayList<>();
        switch (v.getId()) {
            case R.id.myImage:
                imageList.add(appUtils.getProfileUrl());
                break;
            case R.id.otherImage:
                imageList.add(list.get(position).getProfilePic());
                break;
            default:
                imageList.add(list.get(position).getImageUrl());
                break;
        }
        Intent intent = new Intent(this, PostImagesActivity.class);
        intent.putStringArrayListExtra("images", imageList);
        startActivity(intent);
    }

    @Override
    public void onTaskPostExecute(Object object) {
        attachedImageBytes = (byte[]) object;
        if (attachedImageBytes == null) {
            Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(attachedImageBytes, 0, attachedImageBytes.length);
            attachedImage.setImageBitmap(bitmap);
            attachedFrame.setVisibility(View.VISIBLE);
            attachView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
//        Intent i = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putInt("comment", list.size());
//        bundle.putInt("position", itemPosition);
//        i.putExtras(bundle);
//        setResult(1001, i);
        super.onBackPressed();
    }
}
