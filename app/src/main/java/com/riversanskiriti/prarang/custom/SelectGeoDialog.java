package com.riversanskiriti.prarang.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.activity.HomeActivity;
import com.riversanskiriti.utils.BaseUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Pawan Dey on 03-04-2022.
 */

public class SelectGeoDialog extends Dialog{

    private Context context;
    private AppUtils appUtils;
    private BaseUtils baseUtils;
    private ListView lv;
    private CheckBox cb_all;
    private ArrayList<Model> modelArrayList;
    private CustomAdapter customAdapter;
    TextView btnnext,dismiss;
//    public String[] animallist = new String[]{"Jaunpur-Hindi", "Lucknow-Hindi", "Meerut-Hindi", "Rampur-Hindi"};
    public String[] animallistId = new String[]{"r4", "c4", "c2", "c3"};
    public String sel = "";
    boolean[] checkedColors;
    public String[] c1;


    private void initCities() {
        baseUtils = new BaseUtils(context);
        sel = baseUtils.getStringData("cityFilter");

        c1 = new String[4];
        String dat = appUtils.getFilerLocation();
        Log.d("filterlocation-", String.valueOf(dat));

        try {
            JSONArray array = new JSONArray(appUtils.getFilerLocation());
            JSONObject json = array.optJSONObject(2);

            JSONArray mainArray = json.optJSONArray("city");

            if (mainArray != null) {

                for (int i = 1; i < mainArray.length(); i++) {
                    JSONObject jsonObject = mainArray.optJSONObject(i);
                    c1[i-1] = jsonObject.optString("cityName");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("filterlocation2-", Arrays.toString(c1));


    }

    private void initDialog() {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.layout_selectgeo2);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams wmlp = this.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;

        this.getWindow().setAttributes(wmlp);
    }

    private void configSpinners() {
        lv = findViewById(R.id.lv);
        btnnext = findViewById(R.id.next);
        cb_all = findViewById(R.id.cb_all);
        dismiss = findViewById(R.id.dismiss);

        checkedColors = new boolean[c1.length];
        if (sel != null) {

            if (sel.contains("r4")) {
                checkedColors[0] = true;
            }
            if (sel.contains("c4")) {
                checkedColors[1] = true;
            }
            if (sel.contains("c2")) {
                checkedColors[2] = true;
            }
            if (sel.contains("c3")) {
                checkedColors[3] = true;
            }
            if ((sel.contains("r4")) && (sel.contains("c4")) && (sel.contains("c2")) && (sel.contains("c3"))) {
                cb_all.setChecked(true);
            }
        }
        else{
            checkedColors[0] = true;
            checkedColors[1] = true;
            checkedColors[2] = true;
            checkedColors[3] = true;
            cb_all.setChecked(true);
        }

        modelArrayList = setModel(checkedColors);
        customAdapter = new CustomAdapter(context, modelArrayList, cb_all);
        lv.setAdapter(customAdapter);


        cb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cb_all.isChecked()) {
                    modelArrayList = getModel(true);
                } else {
                    modelArrayList = getModel(false);
                }
                customAdapter = new CustomAdapter(context, modelArrayList, cb_all);
                lv.setAdapter(customAdapter);
            }
        });

        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String data = "";
                for (int i = 0; i < CustomAdapter.modelArrayList.size(); i++) {
                    if (CustomAdapter.modelArrayList.get(i).getSelected()) {
                        data = data + "," + CustomAdapter.modelArrayList.get(i).getId();
                    }
                }
//                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "";
                for (int i = 0; i < CustomAdapter.modelArrayList.size(); i++) {
                    if (CustomAdapter.modelArrayList.get(i).getSelected()) {
                        data = data + "," + CustomAdapter.modelArrayList.get(i).getId();
                    }
                }
                if (!data.isEmpty()) {
                    baseUtils.setStringData("cityFilter", data.substring(1));
                    ((HomeActivity) context).makeReqCall(data.substring(1));
                    dismiss();
                } else {
                    Toast.makeText(context, "Please select atleast one City", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<Model> getModel(boolean isSelect) {
        ArrayList<Model> list = new ArrayList<>();
        for (int i = 0; i < c1.length; i++) {
            Model model = new Model();
            model.setSelected(isSelect);
            model.setAnimal(c1[i]);
            model.setId(animallistId[i]);
            list.add(model);
        }
        return list;
    }

    private ArrayList<Model> setModel(boolean[] sel) {
        ArrayList<Model> list = new ArrayList<>();
        for (int i = 0; i < c1.length; i++) {
            Model model = new Model();
            model.setSelected(sel[i]);
            model.setAnimal(c1[i]);
            model.setId(animallistId[i]);
            list.add(model);
        }
        return list;
    }


    public SelectGeoDialog(Context context) {
        super(context);
        this.context = context;
        appUtils = AppUtils.getInstance(context);
        initCities();
        initDialog();
        configSpinners();
    }
}
