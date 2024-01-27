package com.riversanskiriti.prarang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.riversanskiriti.prarang.R;

import java.util.List;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class PostItemAdapter extends RecyclerView.Adapter<PostItemAdapter.MyViewHolder> {

    private List<PostItem> list;
    private Context context;
    private int parentPostion;
    private Listner listner;

    public PostItemAdapter(Context context, List<PostItem> list, int parentPostion, PostItemAdapter.Listner listner) {
        this.context = context;
        this.list = list;
        this.parentPostion = parentPostion;
        this.listner = listner;
    }

    @Override
    public PostItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_post_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PostItemAdapter.MyViewHolder holder, final int position) {
        if (list.get(position).getName() == null) {
            holder.viewMore.setVisibility(View.VISIBLE);
        } else {
            holder.viewMore.setVisibility(View.GONE);
            holder.postName.setText(list.get(position).getName());

            // TestYou
//            list.get(position).setImageUrl("https://youtu.be/YH5f8sna1ug");
//            list.get(position).setImageUrl("https://img.youtube.com/vi/YH5f8sna1ug/0.jpg");
//            list.get(position).setImageUrl("https://img.youtube.com/vi/pz95u3UVpaM/0.jpg");


//            Glide.with(context).load("https://img.youtube.com/vi/YH5f8sna1ug/0.jpg").crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
            Glide.with(context).load(list.get(position).getImageUrl()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    holder.postImageProgressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.postImageProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.postImageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView postName;
        private ImageView postImageView;
        private ProgressBar postImageProgressBar;
        private View cardView, viewMore;

        public MyViewHolder(View itemView) {
            super(itemView);
            postName = (TextView) itemView.findViewById(R.id.postName);
            postName.setSelected(true);
            postImageView = (ImageView) itemView.findViewById(R.id.postImageView);
            postImageProgressBar = (ProgressBar) itemView.findViewById(R.id.postImageProgressBar);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);

            viewMore = itemView.findViewById(R.id.viewMore);
        }

        @Override
        public void onClick(View v) {
            if (listner != null) {
                if (viewMore.getVisibility() == View.VISIBLE) {
                    listner.onClickPostItem(v, parentPostion, -1);
                    return;
                }
                listner.onClickPostItem(v, parentPostion, getAdapterPosition());
            }
        }
    }

    public interface Listner {
        public void onClickPostItem(View v, int parentPosition, int position);
    }
}
