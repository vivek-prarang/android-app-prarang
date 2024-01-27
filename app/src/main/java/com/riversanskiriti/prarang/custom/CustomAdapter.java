package com.riversanskiriti.prarang.custom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.activity.HomeActivity;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    public static ArrayList<Model> modelArrayList;
    CheckBox cb_all;

    public CustomAdapter(Context context, ArrayList<Model> modelArrayList , CheckBox cb_all) {
        this.context = context;
        this.modelArrayList = modelArrayList;
        this.cb_all=cb_all;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.checkBox = convertView.findViewById(R.id.cb);
            holder.tvAnimal = convertView.findViewById(R.id.animal);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

//        holder.checkBox.setText("Checkbox "+position);
        holder.tvAnimal.setText(modelArrayList.get(position).getAnimal());
        holder.checkBox.setChecked(modelArrayList.get(position).getSelected());
        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag(position);


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                TextView tv = (TextView) tempview.findViewById(R.id.animal);
                Integer pos = (Integer)  holder.checkBox.getTag();
//                Toast.makeText(context, "Checkbox "+pos+" clicked!", Toast.LENGTH_SHORT).show();


                if(modelArrayList.get(pos).getSelected()){
                    modelArrayList.get(pos).setSelected(false);
                }else {
                    modelArrayList.get(pos).setSelected(true);
                }
                boolean isParentToChecked=false;

                for (int i = 0; i < modelArrayList.size(); i++) {
                    if (modelArrayList.get(i).getSelected()) {
                        isParentToChecked = true;
                    }else {
                        isParentToChecked = false;
                        break;
                    }
                }

                if(cb_all!=null){
                    cb_all.setChecked(isParentToChecked);
                }
//
                HomeActivity homeActivity = new HomeActivity();
                homeActivity.setAllChecked(isParentToChecked);
//              ((HomeActivity)context).setAllChecked(isParentToChecked);   // throwing Exception

//                Toast.makeText(context, "isParentToChecked-"+isParentToChecked, Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }



    private class ViewHolder {
        protected CheckBox checkBox;
        private TextView tvAnimal;
    }
}