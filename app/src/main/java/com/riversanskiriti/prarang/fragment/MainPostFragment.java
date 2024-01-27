package com.riversanskiriti.prarang.fragment;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPostFragment extends Fragment implements PostItemAdapter.Listner, Network.Listener {

    private RecyclerView recyclerView;
    private List<MainPost> mainList;
    private MainPostAdapter mainPostAdapter;
    private BaseUtils baseUtils;
    private AppUtils appUtils;
    private Network network;
    private MainPost rampurPostItem, meerutPostItem, lucknowPostItem, jaunpurPostItem;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MainPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_post, container, false);
    }

    // Added By Pawan (to reload fragment with new filter data)
    public void reloadFragment(){

//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if (Build.VERSION.SDK_INT >= 26) {
//            ft.setReorderingAllowed(false);
//        }
//        ft.detach(this).attach(this).commit();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayoutFragemnt,new MainPostFragment())
                .commit();

//        Fragment frg = null;
//        frg = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag("TAG");
//        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadFragment() {
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


        String selectedCityByFilter = baseUtils.getStringData("cityFilter");
        Log.d("selectedCityByFilter",""+selectedCityByFilter);
        if(selectedCityByFilter!=null) {
            if (selectedCityByFilter.contains("r4")) {
                mainList.add(jaunpurPostItem);
            }
            if (selectedCityByFilter.contains("c4")) {
                mainList.add(lucknowPostItem);
            }
            if (selectedCityByFilter.contains("c2")) {
                mainList.add(meerutPostItem);
            }
            if (selectedCityByFilter.contains("c3")) {
                mainList.add(rampurPostItem);
            }
        }
        else {
            mainList.add(jaunpurPostItem);
            mainList.add(lucknowPostItem);
            mainList.add(meerutPostItem);
            mainList.add(rampurPostItem);
        }

        // if u comment any one then view,loader,post,title all will be hide
//        mainList.add(meerutPostItem);
//        mainList.add(rampurPostItem);
//        mainList.add(lucknowPostItem);
//        mainList.add(jaunpurPostItem);

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
//        String selectedCityByFilter = baseUtils.getStringData("cityFilter");
//

//        if (selectedCityByFilter!=null) {
//            if (selectedCityByFilter.contains("r4")) {
//                networkCalling(UrlConfig.jaunpurCityPost);
//            }
//            if (selectedCityByFilter.contains("c4")) {
//                networkCalling(UrlConfig.lucknowCityPost);
//            }
//            if (selectedCityByFilter.contains("c2")) {
//                networkCalling(UrlConfig.meerutCityPost);
//            }
//            if (selectedCityByFilter.contains("c3")) {
//                networkCalling(UrlConfig.rampurCityPost);
//            }
//        }

        // Call All the Cities Api (All 4 cities)
        networkCalling(UrlConfig.jaunpurCityPost);
        networkCalling(UrlConfig.lucknowCityPost);
        networkCalling(UrlConfig.meerutCityPost);
        networkCalling(UrlConfig.rampurCityPost);
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
                        if (url.contains(UrlConfig.jaunpurCityPost)) {
                            item.setPostType(1);
//                            if (shouldAddIntoList("cityFilter", chittiName)) {
//                                list.add(item);
//                            }
                        } else if (url.contains(UrlConfig.lucknowCityPost)) {
                             item.setPostType(2);
//                            if (shouldAddIntoList("regionFilter", chittiName)) {
//                                list.add(item);
//                            }
                        } else if (url.contains(UrlConfig.meerutCityPost)) {
                            item.setPostType(3);
//                            if (shouldAddIntoList("regionFilter", chittiName)) {
//                                list.add(item);
//                            }
                        } else if(url.contains(UrlConfig.rampurCityPost)){
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
            if (url.contains(UrlConfig.jaunpurCityPost)){
                jaunpurPostItem.setList(list);
                jaunpurPostItem.setMessage(msg);
                jaunpurPostItem.setLoading(false);
                key = "jaunpurpost";
            }
            else if (url.contains(UrlConfig.lucknowCityPost)) {
                lucknowPostItem.setList(list);
                lucknowPostItem.setMessage(msg);
                lucknowPostItem.setLoading(false);
                key = "lucknowpost";
            }
            else if (url.contains(UrlConfig.meerutCityPost)) {
                meerutPostItem.setList(list);
                meerutPostItem.setMessage(msg);
                meerutPostItem.setLoading(false);
                key = "meerutpost";
            }
            else if (url.contains(UrlConfig.rampurCityPost)) {
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

        // Feed Count Api - Added by Pawan 11 Aprl 22
//        String UserCity = appUtils.getGeographyId().substring( 0, appUtils.getGeographyId().indexOf(","));
//        HashMap<String, String> map2 = new HashMap<>();
//        map2.put("subscriberId", appUtils.getSubscriberId());
//        map2.put("geographyCode", mainList.get(parentPosition).getList().get(position).getGeoCode());
//        map2.put("chittiId", mainList.get(parentPosition).getList().get(position).getId());
//        map2.put("userCity", UserCity);
//        map2.put("isFeed", "1");
//        network.makeRequest(map2, UrlConfig.feedCountUrl);
//        Log.d("feedCountMaps--",map2.toString());


        if (position == -1) {
            position = (mainList.get(parentPosition).getList().size() - 2);
        }
        Intent intent = new Intent(getContext(), PostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", mainList.get(parentPosition));
        bundle.putInt("position", position);
        bundle.putInt("parentPosition", parentPosition);
        bundle.putString("value","");   // Code Added by Munendra on 13 May 22
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
        if (url.contains(UrlConfig.jaunpurCityPost)) {
            key = "jaunpurpost";
        }
        else if (url.contains(UrlConfig.lucknowCityPost)) {
            key = "lucknowpost";
        }
        else if (url.contains(UrlConfig.meerutCityPost)) {
            key = "meerutpost";
        }
        else if (url.contains(UrlConfig.rampurCityPost)) {
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
