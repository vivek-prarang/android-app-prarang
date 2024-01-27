package com.riversanskiriti.prarang.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.utils.BaseUtils;

import java.util.List;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder> {

    private List<TagItem> list;
    private Context context;
    private Listner listner;
    private BaseUtils baseUtils;

    public TagAdapter(Context context, List<TagItem> list, TagAdapter.Listner listner) {
        this.context = context;
        this.list = list;
        this.listner = listner;
        baseUtils = new BaseUtils(context);
    }

    @Override
    public TagAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tag, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TagAdapter.MyViewHolder holder, int position) {
        holder.tagTitle.setText(list.get(position).getTitle());
        Glide.with(context).load(list.get(position).getIconUrl()).into(holder.tagIcon);
        GradientDrawable gd = (GradientDrawable) holder.iconFrame.getBackground().getCurrent();
        gd.setStroke(baseUtils.dpToPx(1), list.get(position).getFrameColor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tagTitle;
        private FrameLayout iconFrame;
        private ImageView tagIcon;
        private View tagView;

        public MyViewHolder(View itemView) {
            super(itemView);
            tagTitle = (TextView) itemView.findViewById(R.id.tagTitle);

            tagIcon = (ImageView) itemView.findViewById(R.id.tagIcon);
            iconFrame = (FrameLayout) itemView.findViewById(R.id.iconFrame);

            tagView = itemView.findViewById(R.id.tagView);
            tagView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listner != null) {
                listner.onClickTagItem(getAdapterPosition(), v);
            }
        }
    }

    public interface Listner {
        public void onClickTagItem(int position, View view);
    }
}
