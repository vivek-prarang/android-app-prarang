package com.riversanskiriti.prarang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.riversanskiriti.prarang.AppUtils;
import com.riversanskiriti.prarang.R;

import java.util.List;

/**
 * Created by ARPIT on 07-04-2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private Context context;
    private List<CommentItem> list;
    private CommentAdapter.Listener listener;

    public CommentAdapter(Context context, List<CommentItem> list) {
        this.context = context;
        this.list = list;
    }

    public CommentAdapter(Context context, List<CommentItem> list, CommentAdapter.Listener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.MyViewHolder holder, final int position) {
        CommentItem item = list.get(position);
        if (item.getSubscriberId().equalsIgnoreCase(AppUtils.getInstance(context).getSubscriberId())) {
            //MY Comment
            holder.myCommentView.setVisibility(View.VISIBLE);
            holder.commentView.setVisibility(View.GONE);
            holder.myComment.setText(item.getComment());

            Glide.with(context).load(AppUtils.getInstance(context).getProfileUrl()).dontAnimate().placeholder(R.drawable.ic_userblank).into(holder.myImage);

            holder.myImageFrame.setVisibility(View.GONE);
            if (item.getImageUrl() != null) {
                if (!item.getImageUrl().equalsIgnoreCase("")) {
                    Glide.with(context).load(item.getImageUrl()).thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.myProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.myProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.myCommentImage);
                    holder.myImageFrame.setVisibility(View.VISIBLE);
                }
            }
        } else {
            //Others Comment
            Glide.with(context).load(item.getProfilePic()).placeholder(R.drawable.ic_userblank).dontAnimate().into(holder.otherImage);
            holder.myCommentView.setVisibility(View.GONE);
            holder.commentView.setVisibility(View.VISIBLE);
            holder.name.setText(item.getName());
            holder.comment.setText(item.getComment());
            holder.imageFrame.setVisibility(View.GONE);

            if (item.getImageUrl() != null) {
                if (!item.getImageUrl().equalsIgnoreCase("")) {
                    Glide.with(context).load(item.getImageUrl()).thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.commentImage);
                    holder.imageFrame.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View commentView, myCommentView;
        private TextView name, comment, myComment;
        private ImageView myCommentImage, commentImage, myImage, otherImage;
        private FrameLayout imageFrame, myImageFrame;
        private ProgressBar progressBar, myProgressBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            commentView = itemView.findViewById(R.id.commentView);
            myCommentView = itemView.findViewById(R.id.myCommentView);
            name = (TextView) itemView.findViewById(R.id.name);
            comment = (TextView) itemView.findViewById(R.id.comment);
            myComment = (TextView) itemView.findViewById(R.id.myComment);
            myImage = (ImageView) itemView.findViewById(R.id.myImage);
            otherImage = (ImageView) itemView.findViewById(R.id.otherImage);
            myCommentImage = (ImageView) itemView.findViewById(R.id.myCommentImage);
            commentImage = (ImageView) itemView.findViewById(R.id.commentImage);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            myProgressBar = (ProgressBar) itemView.findViewById(R.id.myProgressBar);
            imageFrame = (FrameLayout) itemView.findViewById(R.id.imageFrame);
            myImageFrame = (FrameLayout) itemView.findViewById(R.id.myImageFrame);

            myImageFrame.setOnClickListener(this);
            imageFrame.setOnClickListener(this);

            myImage.setOnClickListener(this);
            otherImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClickRecyclerView(v, getAdapterPosition());
            }
        }
    }

    public interface Listener {
        public void onClickRecyclerView(View v, int position);
    }
}
