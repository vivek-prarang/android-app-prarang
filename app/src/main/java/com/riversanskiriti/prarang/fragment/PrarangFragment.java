package com.riversanskiriti.prarang.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.activity.TagListActivity;
import com.riversanskiriti.prarang.adapter.PrarangAdapter;
import com.riversanskiriti.prarang.adapter.PrarangItem;
import com.riversanskiriti.prarang.config.UrlConfig;
import com.riversanskiriti.utils.BaseUtils;
import com.riversanskiriti.utils.Lang;
import com.riversanskiriti.utils.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrarangFragment extends Fragment implements PrarangAdapter.Listner, View.OnClickListener, Network.Listener, AdapterView.OnItemSelectedListener  {

    private RecyclerView recyclerView;
    private PrarangAdapter prarangAdapter;
    private ArrayList<PrarangItem> sanskritList;
    private ArrayList<PrarangItem> prakritList;
    private ArrayList<PrarangItem> list;

    private FrameLayout sanskritCard, prakritCard;
    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    TextView sanskritCount, prakritCount;
    private int selectedType = 1;

    public String[] cityCode;
    public String[] cityName;
    public String sel = "";
    String geoCode="";


    public PrarangFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prarang, container, false);
    }

    private void showSanskrit() {
        selectedType = 1;
        list.clear();
        prakritCard.setBackgroundResource(R.drawable.xml_no_border);
        sanskritCard.setBackgroundResource(R.drawable.xml_culture_border);


        for (int i = 0; i < sanskritList.size(); i++) {
            PrarangItem item = sanskritList.get(i);
            item.setType(1);
            switch (i) {
                case 0:
                    item.setIcon(R.drawable.ic_samay_sima);
                    item.setFrameColor(getResources().getColor(R.color.colorSanskritRed));
                    break;
                case 1:
                    item.setIcon(R.drawable.ic_manav_wa_indirya);
                    item.setFrameColor(getResources().getColor(R.color.colorSanskritYellow));
                    break;
                case 2:
                    item.setIcon(R.drawable.ic_manav_wa_awishkar);
                    item.setFrameColor(getResources().getColor(R.color.colorSanskritBlue));
                    break;
            }
            list.add(item);
        }
        prarangAdapter.notifyDataSetChanged();
    }

    private void showPrakrit() {
        selectedType = 2;
        sanskritCard.setBackgroundResource(R.drawable.xml_no_border);
        prakritCard.setBackgroundResource(R.drawable.xml_nature_border);
        list.clear();
        for (int i = 0; i < prakritList.size(); i++) {
            PrarangItem item = prakritList.get(i);
            item.setType(2);
            switch (i) {
                case 0:
                    item.setIcon(R.drawable.ic_bhugol);
                    item.setFrameColor(getResources().getColor(R.color.colorPrakritLightYellow));
                    break;
                case 1:
                    item.setIcon(R.drawable.ic_jiw_jantu);
                    item.setFrameColor(getResources().getColor(R.color.colorPrakritLightGreen));
                    break;
                case 2:
                    item.setIcon(R.drawable.ic_vanaspati);
                    item.setFrameColor(getResources().getColor(R.color.colorPrakritGreen));
                    break;
            }
            list.add(item);
        }
        prarangAdapter.notifyDataSetChanged();
    }

    public void loadFragment() {
        baseUtils = new BaseUtils(getContext());
        appUtils = new AppUtils(getContext());
        network = new Network(getContext(), this);
        lang = new Lang(getContext());

        sanskritList = new ArrayList<>();
        prakritList = new ArrayList<>();
        list = new ArrayList<>();

        // Added By Pawan on 26 Aprl 22

        sel = baseUtils.getStringData("cityFilter");  // 'c1,c2,c3'
//        Log.d("sel--",sel);
        if(sel!=null) {

            List<String> elephantList = Arrays.asList(sel.split(",")); // c1 c2 c3
            Log.d("elephantList", "" + elephantList);
            cityName = new String[elephantList.size() + 1];
            cityCode = new String[elephantList.size() + 1];

            int cNameCntr = 0;
//            cityName[cNameCntr] = "Select City";
            cityName[cNameCntr] = getResources().getString(R.string.filter_city);  //changed by munendra
            cityCode[cNameCntr] = "0";
            cNameCntr++;

            if (!elephantList.isEmpty()) {
                if (elephantList.contains("r4")) {
//                    cityName[cNameCntr] = "Jaunpur";
                    cityName[cNameCntr] = getString(R.string.text_jaunpur);   //changed by munendra
                    cityCode[cNameCntr] = "r4";
                    cNameCntr++;
                }
                if (elephantList.contains("c4")) {
//                    cityName[cNameCntr] = "Lucknow";
                    cityName[cNameCntr] = getString(R.string.text_lucknow);  //changed by munendra
                    cityCode[cNameCntr] = "c4";
                    cNameCntr++;
                }
                if (elephantList.contains("c2")) {
//                    cityName[cNameCntr] = "Meerut";
                    cityName[cNameCntr] = getString(R.string.text_meerut); //changed by munendra
                    cityCode[cNameCntr] = "c2";
                    cNameCntr++;
                }
                if (elephantList.contains("c3")) {
//                    cityName[cNameCntr] = "Rampur";
                    cityName[cNameCntr] = getString(R.string.text_rampur);   //changed by munendra
                    cityCode[cNameCntr] = "c3";
                    cNameCntr++;
                }
                if ((elephantList.contains("r4")) && (elephantList.contains("c4")) && (elephantList.contains("c2")) && (elephantList.contains("c3"))) {
//                    cityName[1] = "Jaunpur";
//                    cityName[2] = "Lucknow";
//                    cityName[3] = "Meerut";
//                    cityName[4] = "Rampur";

                    //changed by munendra
                    cityName[1] = getString(R.string.text_jaunpur);
                    cityName[2] = getString(R.string.text_lucknow);
                    cityName[3] = getString(R.string.text_meerut);
                    cityName[4] = getString(R.string.text_rampur);


                    cityCode[1] = "r4";
                    cityCode[2] = "c4";
                    cityCode[3] = "c2";
                    cityCode[4] = "c3";
                }
            }
        }else {
//            cityName[1] = "Jaunpur";
//            cityName[2] = "Lucknow";
//            cityName[3] = "Meerut";
//            cityName[4] = "Rampur";

            //changed by munendra
            cityName[1] = getString(R.string.text_jaunpur);
            cityName[2] = getString(R.string.text_lucknow);
            cityName[3] = getString(R.string.text_meerut);
            cityName[4] = getString(R.string.text_rampur);

            cityCode[1] = "r4";
            cityCode[2] = "c4";
            cityCode[3] = "c2";
            cityCode[4] = "c3";
        }

            Spinner spin = getView().findViewById(R.id.sp_city);
            spin.setOnItemSelectedListener(this);

            // Create the instance of ArrayAdapter
            // having the list of courses
            ArrayAdapter ad = new ArrayAdapter(getView().getContext(), android.R.layout.simple_spinner_item, cityName);

            // set simple layout resource file
            // for each item of spinner
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Set the ArrayAdapter (ad) data on the
            // Spinner which binds data to spinner
            spin.setAdapter(ad);
            // Added By Pawan



        prarangAdapter = new PrarangAdapter(getView().getContext(), list, this);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(prarangAdapter);

        sanskritCount =  getView().findViewById(R.id.sanskritCount);
        prakritCount =  getView().findViewById(R.id.prakritCount);
        sanskritCard =  getView().findViewById(R.id.sanskritCard);
        prakritCard =  getView().findViewById(R.id.prakritCard);
        sanskritCard.setOnClickListener(this);
        prakritCard.setOnClickListener(this);
        onNetworkSuccess(appUtils.getTagsResponse(), null);
//        loadFragmentData(false);

    }


    public void loadFragmentData(boolean filterApply) {
        prakritList.clear();
        sanskritList.clear();
        HashMap<String, String> map = new HashMap<>();
        map.put("languageCode", lang.getAppLanguage());
        map.put("SubscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
        if (filterApply) {
            map.put("filterApply", "1");
        }
        Log.d("PrarangFragMaps-",map.toString());
//        network.makeRequest(map, UrlConfig.tagcount);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prakritCard:
                showPrakrit();
                break;
            case R.id.sanskritCard:
                showSanskrit();
                break;
        }
        baseUtils.vibrate();
    }

    @Override
    public void onClickPrarangItem(int position) {
        Intent intent = new Intent(getContext(), TagListActivity.class);
        intent.putExtra("type", list.get(position).getType());
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("color", list.get(position).getFrameColor());
        intent.putExtra("geoCode",geoCode);  //Added
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", list.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }
        String savedResponse = appUtils.getTagsResponse();
        Log.e("BalResponse-", response + "");
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                String natureCount = json.getString("natureCount");
                String cultureCount = json.getString("cultureCount");
                JSONArray array = json.getJSONArray("Payload");

                if(array.length()>0){

                    for (int i = 0; i < array.length(); i++) {
                        json = array.getJSONObject(i);
                        PrarangItem item = new PrarangItem();
                        item.setId(json.getInt("tagCategoryId"));
                        item.setTitle(json.getString("tagCategoryInUnicode"));
                        item.setCount(json.getString("totalPost"));
                        if (i < 3) {
                            sanskritList.add(item);
                        } else {
                            prakritList.add(item);
                        }
                        prakritCount.setText(natureCount);
                        sanskritCount.setText(cultureCount);
                        if (selectedType == 1) {
                            showSanskrit();
                        } else {
                            showPrakrit();
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    prakritCount.setText(natureCount);
                    sanskritCount.setText(cultureCount);

                    sanskritList.clear();
                    prakritList.clear();
                    recyclerView.setVisibility(View.GONE);
                }

                appUtils.setTagsResponse(response);
            } else {
                if (savedResponse == null) {
                    Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    onNetworkSuccess(savedResponse, url);
                }
            }
        } catch (JSONException e) {
            if (savedResponse != null) {
                onNetworkSuccess(savedResponse, url);
            }
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        Log.e("onNetworkError", error + "");
        String savedResponse = appUtils.getTagsResponse();
        if (savedResponse == null) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
        } else {
            onNetworkSuccess(savedResponse, url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//        Toast.makeText(getContext(), cityCode[position], Toast.LENGTH_LONG).show();

            prakritList.clear();
            sanskritList.clear();
            HashMap<String, String> map = new HashMap<>();
            map.put("languageCode", lang.getAppLanguage());
            map.put("SubscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
            map.put("filterApply", "0");
            map.put("geographyCode", cityCode[position]);
            geoCode = cityCode[position];

            Log.d("MapsTagCount-", map.toString());
            network.makeRequest(map, UrlConfig.tagcount);
        }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
