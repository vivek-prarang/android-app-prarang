package com.riversanskiriti.prarang.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.activity.CommentActivity;
import com.riversanskiriti.prarang.activity.PostImagesActivity;
import com.riversanskiriti.prarang.activity.TagListActivity;
import com.riversanskiriti.prarang.adapter.PostAdapter;
import com.riversanskiriti.prarang.adapter.PostItem;
import com.riversanskiriti.prarang.config.UrlConfig;
import com.riversanskiriti.prarang.db.DbHandler;
import com.riversanskiriti.utils.BaseUtils;
import com.riversanskiriti.utils.Lang;
import com.riversanskiriti.utils.Network;
import com.riversanskiriti.utils.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
/*public class GulakFragment extends Fragment implements PostAdapter.Listener {

    private View fView;
    private RecyclerView recyclerView;
    private List<PostItem> list;
    private PostAdapter postAdapter;

    private DbHandler db;
    private BaseUtils baseUtils;
    private TextView norecord;


    public GulakFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_post, container, false);
        recyclerView = fView.findViewById(R.id.recyclerView);
        return fView;
    }

    private void loadFragment() {
        db = new DbHandler(getContext());
        baseUtils = new BaseUtils(getContext());
        norecord = (TextView) getView().findViewById(R.id.norecord);
        norecord.setText(getContext().getResources().getString(R.string.msg_nochitti));
        list = new ArrayList<>();

        postAdapter = new PostAdapter(getContext(), list, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getView().getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postAdapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        loadFragmentData();
    }

    public void loadFragmentData() {
        List<PostItem> tempList = db.getGulak(null);
        list.clear();
        for (int i = 0; i < tempList.size(); i++) {
            tempList.get(i).setSaved(true);
            list.add(tempList.get(i));
        }
        if (list.size() > 0) {
            norecord.setVisibility(View.GONE);
        } else {
            norecord.setVisibility(View.VISIBLE);
        }
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadFragment();
            }
        });
    }

    private PostAdapter.ViewHolder shareHolder;
    private int sharePosition;

    @Override
    public void onClickRecycleView(View view, int position, PostAdapter.ViewHolder holder) {
        switch (view.getId()) {
            case R.id.shareButtonView:
                shareHolder = holder;
                sharePosition = position;
                Permission permission = new Permission(getContext());
                if (permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    baseUtils.sharePost(holder.postImageView, list.get(position).getPostUrl(),list.get(position).getGeoCode(),list.get(position).getId());
                } else {
                    permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, this);
                }
                break;
            case R.id.likeButtonView:
                int totalLike = list.get(position).getTotalLike();
                if (Boolean.parseBoolean(list.get(position).getLiked())) {
                    list.get(position).setLiked("false");
                    holder.likeImageView.setImageResource(R.drawable.ic_like);
                    totalLike = totalLike - 1;
                } else {
                    list.get(position).setLiked("true");
                    holder.likeImageView.setImageResource(R.drawable.ic_like_blue);
                    totalLike = totalLike + 1;
                }
                if (totalLike < 0) {
                    totalLike = 0;
                }
                list.get(position).setTotalLike(totalLike);
                if (totalLike == 0) {
                    holder.totalLike.setText("");
                } else {
                    holder.totalLike.setText("[" + totalLike + "]");
                }
                db.updateGulalItem(list.get(position));
                Network network = new Network(getContext());
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
                map.put("chittiId", list.get(position).getId());
                map.put("isLiked", list.get(position).getLiked());
                map.put("languageCode", new Lang(getContext()).getAppLanguage());
                network.makeRequest(map, UrlConfig.chittiLike);
                baseUtils.vibrate();
                break;
            case R.id.commentButtonView:
                Intent i = new Intent(getContext(), CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", list.get(position));
                bundle.putInt("position", position);
                i.putExtras(bundle);
                startActivityForResult(i, 1001);
                break;
            case R.id.gulakButtonView:
                if (list.get(position).isSaved()) {
                    db.deleteFromGulak(list.get(position).getId());
                    list.remove(position);
                    if (list.size() > 0) {
                        postAdapter.notifyItemRemoved(position);
                        norecord.setVisibility(View.GONE);
                    } else {
                        postAdapter.notifyDataSetChanged();
                        norecord.setVisibility(View.VISIBLE);
                    }
                }
                baseUtils.vibrate();
                break;
            case R.id.postImageView:
                Intent intent = new Intent(getContext(), PostImagesActivity.class);
                intent.putStringArrayListExtra("images", list.get(position).getImageList());
                startActivity(intent);
                break;
            case R.id.showTagView:
                i = new Intent(getContext(), TagListActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("item", list.get(position));
                bundle.putBoolean("showtags", true);
                i.putExtras(bundle);
                startActivity(i);
                break;
        }
        //postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                baseUtils.sharePost(shareHolder.postImageView, list.get(sharePosition).getPostUrl(),list.get(sharePosition).getGeoCode(),list.get(sharePosition).getId());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1001) {
                Bundle bundle = data.getExtras();
                int comment = bundle.getInt("comment");
                int position = bundle.getInt("position");

                PostItem item = list.get(position);
                item.setTotalComment(comment);
                list.set(position, item);
                postAdapter.notifyItemChanged(position);
                db.updateGulalItem(item);
            }
        }
    }
}*/



import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.activity.HomeActivity;
import com.riversanskiriti.prarang.activity.PostActivity;
import com.riversanskiriti.prarang.adapter.MainPost;
import com.riversanskiriti.prarang.adapter.MainPostAdapter;
import com.riversanskiriti.prarang.adapter.PostItem;
import com.riversanskiriti.prarang.adapter.PostItemAdapter;
import com.riversanskiriti.prarang.config.UrlConfig;
import com.riversanskiriti.utils.BaseUtils;
import com.riversanskiriti.utils.Lang;
import com.riversanskiriti.utils.Network;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GulakFragment extends Fragment implements PostItemAdapter.Listner, Network.Listener {

    public static String s;
    private RecyclerView recyclerView;
    private List<MainPost> mainList;
    private MainPostAdapter mainPostAdapter;
    private BaseUtils baseUtils;
    private AppUtils appUtils;
    private Network network;
    private MainPost rampurPostItem, meerutPostItem, lucknowPostItem, jaunpurPostItem;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public GulakFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_post, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadFragment() {
        baseUtils = new BaseUtils(getContext());
        appUtils = new AppUtils(getContext());
        network = new Network(getContext(), this);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);

        mainList = new ArrayList<>();
        mainPostAdapter = new MainPostAdapter(getView().getContext(), mainList, this);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getView().getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mainPostAdapter);
        progressBar.setVisibility(View.GONE);

        jaunpurPostItem = new MainPost(getContext().getResources().getString(R.string.text_jaunpur), null);
        lucknowPostItem = new MainPost(getContext().getResources().getString(R.string.text_lucknow), null);
        meerutPostItem  = new MainPost(getContext().getResources().getString(R.string.text_meerut), null);
        rampurPostItem  = new MainPost(getContext().getResources().getString(R.string.text_rampur), null);

        // if u comment any one then view,loader,post,title all will be hide
        mainList.add(jaunpurPostItem);
        mainList.add(lucknowPostItem);
        mainList.add(meerutPostItem);
        mainList.add(rampurPostItem);

        mainPostAdapter.notifyDataSetChanged();
        makeNetworkCall();

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeNetworkCall();
            }
        });
    }

    public void makeNetworkCall() { //if u comment any one then view with loader,title will show but not the post fot that cities

        // Call All the Cities Api (All 4 cities)
        networkCalling(UrlConfig.jaunpurCityPostBankURL);
        networkCalling(UrlConfig.lucknowCityPostBankURL);
        networkCalling(UrlConfig.meerutCityPostBankURL);
        networkCalling(UrlConfig.rampurCityPostBankURL);
    }

    private void networkCalling(String url) {
        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
        map.put("languageCode", new Lang(getContext()).getAppLanguage());
        map.put("offset", "0");
        network = new Network(getContext(), this);
        network.makeRequest(map, url);
        Log.i("cityWiseChittiMap", map.toString());
        Log.i("cityWiseChittiMapURL", url);
    }

    private Boolean shouldAddIntoList(String key, String value) {
        String savedValue = baseUtils.getStringData(key);
        if (savedValue == null) {
            return true;
        }
        if (value.trim().equalsIgnoreCase(savedValue.trim())) {
            return true;
        }
        return false;
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadCityPost(String stringArray, String url, String msg) {
        try {
            List<PostItem> list = new ArrayList<>();
            if (stringArray != null) {
                JSONArray array = new JSONArray(stringArray);

                if (url.contains(UrlConfig.jaunpurCityPostBankURL)) {
//                    jaunpurPostItem.setName("Jaunpur Saved Feeds - "+array.length());

                    if(getString(R.string.text_jaunpur).equals("Jaunpur")){         // Code Added by Munendra on 13 May 22
                        jaunpurPostItem.setName("Jaunpur Saved Feeds - "+array.length());
                    }else{
                        jaunpurPostItem.setName("जौनपुर बचाया फीड्स - "+array.length());
                    }
                }
                else if (url.contains(UrlConfig.lucknowCityPostBankURL)) {
//                    lucknowPostItem.setName("Lucknow Saved Feeds - "+array.length());

                    if(getString(R.string.text_lucknow).equals("Lucknow")){
                        lucknowPostItem.setName("Lucknow Saved Feeds - "+array.length());
                    }else{
                        lucknowPostItem.setName("लखनऊ बचाया फीड्स - "+array.length());
                    }

                }
                else if (url.contains(UrlConfig.meerutCityPostBankURL)) {
//                    meerutPostItem.setName("Meerut Saved Feeds - "+array.length());

                    if(getString(R.string.text_meerut).equals("Meerut")){
                        meerutPostItem.setName("Meerut Saved Feeds - "+array.length());
                    }else{
                        meerutPostItem.setName("मेरठ बचाया फीड्स - "+array.length());
                    }
                }
                else if (url.contains(UrlConfig.rampurCityPostBankURL)) {
//                    rampurPostItem.setName("Rampur Saved Feeds - "+array.length());

                    if(getString(R.string.text_rampur).equals("Rampur")){
                        rampurPostItem.setName("Rampur Saved Feeds - "+array.length());
                    }else{
                        rampurPostItem.setName("रामपुर बचाया फीड्स - "+array.length());
                    }
                }


                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);

                    if (json.has("tags") && json.has("image")) {
                        JSONArray imageArray = json.getJSONArray("image");
                        JSONArray tagArray = json.getJSONArray("tags");
                        PostItem item = new PostItem();
                        String chittiName = json.getString("Title");
                        item.setName(chittiName);
                        item.setId(json.getString("chittiId"));
                        item.setDescription(json.getString("description"));
                        item.setGeoCode(json.getString("postType"));   // Added by Pawan
                        item.setLiked(json.getString("isLiked"));
                        item.setTotalLike(json.getInt("totalLike"));
                        item.setTotalComment(json.getInt("totalComment"));
                        item.setPostUrl(json.getString("url"));
                        item.setDateTime(json.getString("dateOfApprove"));
                        item.setImageList(imageArray);
                        item.setTagList(tagArray);
                        if (url.contains(UrlConfig.jaunpurCityPostBankURL)) {
                            item.setPostType(1);

//                            if (shouldAddIntoList("cityFilter", chittiName)) {
//                                list.add(item);
//                            }
                        } else if (url.contains(UrlConfig.lucknowCityPostBankURL)) {
                            item.setPostType(2);
//                            if (shouldAddIntoList("regionFilter", chittiName)) {
//                                list.add(item);
//                            }
                        } else if (url.contains(UrlConfig.meerutCityPostBankURL)) {
                            item.setPostType(3);
//                            if (shouldAddIntoList("regionFilter", chittiName)) {
//                                list.add(item);
//                            }
                        } else if(url.contains(UrlConfig.rampurCityPostBankURL)){
                            item.setPostType(4);
//                            if (shouldAddIntoList("countryFilter", chittiName)) {
//                                list.add(item);
//                            }
                        }
                        list.add(item);
                    }
                }
                if (list.size() >= 21) {
                    list.add(new PostItem());
                }
            }
            if (msg == null) {
                msg = getContext().getResources().getString(R.string.msg_networkerror);
            }
            String key = null;
            if (url.contains(UrlConfig.jaunpurCityPostBankURL)){
                jaunpurPostItem.setList(list);
                jaunpurPostItem.setMessage(msg);
                jaunpurPostItem.setLoading(false);
                key = "jaunpurpost";
            }
            else if (url.contains(UrlConfig.lucknowCityPostBankURL)) {
                lucknowPostItem.setList(list);
                lucknowPostItem.setMessage(msg);
                lucknowPostItem.setLoading(false);
                key = "lucknowpost";
            }
            else if (url.contains(UrlConfig.meerutCityPostBankURL)) {
                meerutPostItem.setList(list);
                meerutPostItem.setMessage(msg);
                meerutPostItem.setLoading(false);
                key = "meerutpost";
            }
            else if (url.contains(UrlConfig.rampurCityPostBankURL)) {
                rampurPostItem.setList(list);
                rampurPostItem.setMessage(msg);
                rampurPostItem.setLoading(false);
                key = "rampurpost";
            }

            if (stringArray != null) {
                baseUtils.setStringData(key, stringArray);
            }

            mainPostAdapter.notifyDataSetChanged();
        } catch (Exception ee) {
            //TODO
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadFragment();
            }
        });
    }
    @Override
    public void onClickPostItem(View v, int parentPosition, int position) {
     String s= "s";
        if (position == -1) {
            position = (mainList.get(parentPosition).getList().size() - 2);
        }
        Intent intent = new Intent(getContext(), PostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", mainList.get(parentPosition));
        bundle.putInt("position", position);
        bundle.putInt("parentPosition", parentPosition);
        bundle.putString("value","value");  // Code Added by Munendra on 13 May 22
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            makeNetworkCall();
        }
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        Log.d("onNetworkSuccess", url);
        swipeRefreshLayout.setRefreshing(false);
        JSONArray array = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json.getString("responseCode").equals("1")) {
                array = json.getJSONArray("Payload");
                loadCityPost(array.toString(), url, null);
            } else {
                loadCityPost(null, url, json.getString("message"));
            }
        } catch (JSONException e) {
            loadCityPost(null, url, e.getMessage());
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        swipeRefreshLayout.setRefreshing(false);
        Log.d("onNetworkErrorUrl", url);

        String key = null;
        if (url.contains(UrlConfig.jaunpurCityPostBankURL)) {
            key = "jaunpurpost";
        }
        else if (url.contains(UrlConfig.lucknowCityPostBankURL)) {
            key = "lucknowpost";
        }
        else if (url.contains(UrlConfig.meerutCityPostBankURL)) {
            key = "meerutpost";
        }
        else if(url.contains(UrlConfig.rampurCityPostBankURL)) {
            key = "rampurpost";
        }
        String savedData = baseUtils.getStringData(key);
        Log.d("savedData",savedData);
        if (savedData == null) {
            loadCityPost(null, url, error);
        } else {
            loadCityPost(savedData, url, getResources().getString(R.string.msg_noletter));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }
}
