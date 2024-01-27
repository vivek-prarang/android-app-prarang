package com.riversanskiriti.prarang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;

import java.util.List;

/**
 * Created by ARPIT on 04-03-2017.
 */

public class NavMenuAdapter extends RecyclerView.Adapter<NavMenuAdapter.MyViewHolder> {

    private Listener listener;
    private Context context;
    private List<NavItem> list;

    public NavMenuAdapter(Context context, List list, NavMenuAdapter.Listener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.nav_menu_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.rowIcon.setImageResource(list.get(position).getIcon());
        holder.rowTitle.setText(list.get(position).getTitle());
        if (list.get(position).getType() == 0) {
            holder.rowSwitch.setVisibility(View.GONE);
        } else {
            holder.rowSwitch.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.rowSwitch.setChecked(AppUtils.getInstance(context).getIsHindi());
            } else {
                holder.rowSwitch.setChecked(AppUtils.getInstance(context).getIsEnglish());
            }
            if (holder.rowSwitch.isChecked()) {
                holder.rowSwitch.setTrackDrawable(context.getResources().getDrawable(R.drawable.xml_switch_bg_selector));
            } else {
                holder.rowSwitch.setTrackDrawable(context.getResources().getDrawable(R.drawable.xml_switch_bg_notselector));
            }

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface Listener {
        public void onClickNavMenu(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView rowIcon;
        SwitchCompat rowSwitch;
        TextView rowTitle;
        View rowLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            rowLayout = itemView.findViewById(R.id.rowLayout);
            rowIcon = (ImageView) itemView.findViewById(R.id.rowIcon);
            rowSwitch = (SwitchCompat) itemView.findViewById(R.id.rowSwitch);
            rowTitle = (TextView) itemView.findViewById(R.id.rowTitle);
            rowLayout.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            listener.onClickNavMenu(v, getAdapterPosition());
        }
    }
}
