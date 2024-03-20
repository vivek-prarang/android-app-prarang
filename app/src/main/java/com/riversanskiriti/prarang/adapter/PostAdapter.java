package com.riversanskiriti.prarang.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.riversanskiriti.prarang.R;
import com.riversanskiriti.prarang.StaticClass;
import com.riversanskiriti.utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ARPIT on 02-03-2017.
 */
public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PostItem> list;
    private Listener listener;
    private BaseUtils baseUtils;
    private String mimeTypeWebView = "text/html";
    private String encodingWebView = "UTF-8";

    public PostAdapter(Context context, List<PostItem> list, PostAdapter.Listener listener) {
        this.mContext = context;
        this.listener = listener;
        this.list = list;
        baseUtils = new BaseUtils(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_post, parent, false);
            return new ViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int position) {
        if (list.get(position).getType() != 0) {
            return;
        }
        final ViewHolder holder = (ViewHolder) rholder;
        if (list.get(position).isSaved()) {
            holder.gulakImageView.setImageResource(R.drawable.ic_sanrakhit_karey_blue);
        } else {
            holder.gulakImageView.setImageResource(R.drawable.ic_sanrakhit_karey);
        }
        if (Boolean.parseBoolean(list.get(position).getLiked())) {
            holder.likeImageView.setImageResource(R.drawable.ic_like_blue);
        } else {
            holder.likeImageView.setImageResource(R.drawable.ic_like);
        }

        // TestYou
//        list.get(position).setImageUrl("https://youtu.be/YH5f8sna1ug");
//            list.get(position).setImageUrl("https://img.youtube.com/vi/YH5f8sna1ug/0.jpg");

/*        if (list.get(position).isVideoUrl()) {
            Log.d("adddata"," here for video");
            holder.youtubeViewWebView.loadData(list.get(position).getVideoIframe(), mimeTypeWebView, encodingWebView);
            holder.youtubeViewWebView.setVisibility(View.VISIBLE);
            holder.imageFrameLayout.setVisibility(View.GONE);
        } else {*/
            Glide.with(mContext).load(list.get(position).getImageUrl()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    //holder.postImageProgressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.postImageProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.postImageView);
/*
       }
*/


//        String str = "<iframe width=\"100%\" src=\"https://youtu.be/YH5f8sna1ug\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen=\"allowfullscreen\"></iframe>";
//        String str = "<iframe width=\"100%\" src=\"https://www.youtube.com/embed/YH5f8sna1ug\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen=\"allowfullscreen\"></iframe>";
//        String str = "<iframe frameborder=\"0\" src=\"http://www.youtube.com/embed/Z2m6IkVPCdI\" width=\"100%\"></iframe>";
//        holder.youtubeViewWebView.loadUrl("https://www.youtube.com/watch?v=CsVJoCKc9rA");
//        holder.youtubeViewWebView.loadData(str, mimeTypeWebView, encodingWebView);

        String description = list.get(position).getDescription();
        if (description != null)
        {
            if (description.length() > 500) {
//                description = description.substring(0, 500) + "... ";
//                description = description.replaceAll("\n", "<br />");
////                holder.messageTextView.setText(Html.fromHtml(description + "<font color='#2196f3'><u>" + mContext.getResources().getString(R.string.viewmore) + "</u></font>"));
//                holder.messageWebView.loadData((description + "<font color='#2196f3'><u>" + mContext.getResources().getString(R.string.viewmore) + "</u></font>"), mimeTypeWebView, encodingWebView);
//                holder.completeMessageShown = false;
//            } else {
//                holder.messageTextView.setText(description);
//                holder.messageWebView.loadData((description + "<font color='#2196f3'><u>" + mContext.getResources().getString(R.string.viewmore) + "</u></font>"), mimeTypeWebView, encodingWebView);
                holder.messageWebView.loadDataWithBaseURL("http://www.youtube.com/", description, mimeTypeWebView, encodingWebView, null);
                Log.i("WebView desc", description);
                holder.completeMessageShown = true;
            }
        }

        //For Tags and more
        holder.postCity.setText(list.get(position).getName() + " - " + list.get(position).getDateTime());

        List<String> tagList = list.get(position).getTagList();
        String tagNamesText = null;
        int uptoCounter = 0;
        if (tagList.size() > 2) {
            uptoCounter = 2;
        } else {
            uptoCounter = tagList.size();
        }
        for (int i = 0; i < uptoCounter; i++) {
            if (tagNamesText == null) {
                tagNamesText = tagList.get(i).toString().trim();
            } else {
                tagNamesText = tagNamesText + ", " + tagList.get(i).toString().trim();
            }
        }
        holder.builder.clear();
        if(!StaticClass.isBalance) {
            if (tagNamesText != null) {
                holder.builder.append(baseUtils.encodeToUTF8(tagNamesText)).append("  ");
                holder.builder.setSpan(new ImageSpan(mContext, R.drawable.ic_showtags),
                        holder.builder.length() - 1, holder.builder.length(), 0);
                if (uptoCounter == 2) {
                    if ((tagList.size() - 2) > 0) {
                        holder.builder.append(" +" + (tagList.size() - 2) + " " + mContext.getResources().getString(R.string.more));
                    }
                }
                holder.tagNames.setText(holder.builder);
                holder.tagNames.setVisibility(View.VISIBLE);
            } else {
                holder.tagNames.setVisibility(View.GONE);
            }
        }
        else {
            holder.tagNames.setVisibility(View.GONE);
        }

        holder.dotView.removeAllViews();
        ArrayList<String> imageList = list.get(position).getImageList();
        if (imageList.size() > 1) {
            for (int i = 0; i < imageList.size(); i++) {
                addDotView(i, holder.dotView);
            }
        }

        if (list.get(position).getTotalLike() != 0) {
            holder.totalLike.setText("[" + list.get(position).getTotalLike() + "]");
        } else {
            holder.totalLike.setText("");
        }
        if (list.get(position).getTotalComment() != 0) {
            holder.totalComment.setText("[" + list.get(position).getTotalComment() + "]");
        } else {
            holder.totalComment.setText("");

        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView postImageView, gulakImageView, likeImageView;
        View shareButtonView, likeButtonView, commentButtonView, gulakButtonView;
        ProgressBar postImageProgressBar;
        private LinearLayout showTagView, dotView;
        public LinearLayout ll_bottomView;
        CardView cardView;

        private TextView messageTextView, postCity, tagNames, totalComment;
        public TextView totalLike;
        private SpannableStringBuilder builder;
        private AlertDialog alertDialog;

        public WebView messageWebView;
        private boolean completeMessageShown = false;
        ScrollView scrollView;

        WebView youtubeViewWebView;
        FrameLayout imageFrameLayout;

        public ViewHolder(View view) {
            super(view);
            builder = new SpannableStringBuilder();
            showTagView = (LinearLayout) view.findViewById(R.id.showTagView);
            dotView = (LinearLayout) view.findViewById(R.id.dotView);

            postImageView = (ImageView) view.findViewById(R.id.postImageView);
            gulakImageView = (ImageView) view.findViewById(R.id.gulakImageView);
            likeImageView = (ImageView) view.findViewById(R.id.likeImageView);

//            messageTextView = (TextView) view.findViewById(R.id.messageTextView);
            postImageProgressBar = (ProgressBar) view.findViewById(R.id.postImageProgressBar);

            // WebView
            messageWebView = (WebView) view.findViewById(R.id.messageWebView);
            messageWebView.getSettings().setJavaScriptEnabled(true);
            messageWebView.setWebViewClient(new WebViewClient());
            messageWebView.setWebChromeClient(new WebChromeClient());
            scrollView = (ScrollView) view.findViewById(R.id.scrollView);

            youtubeViewWebView = (WebView) view.findViewById(R.id.youtubeViewWebView);
            youtubeViewWebView.getSettings().setJavaScriptEnabled(true);
            youtubeViewWebView.setWebViewClient(new WebViewClient());
            youtubeViewWebView.setWebChromeClient(new WebChromeClient());
            imageFrameLayout = (FrameLayout) view.findViewById(R.id.imageFrameLayout);

            postCity = (TextView) view.findViewById(R.id.postCity);
            tagNames = (TextView) view.findViewById(R.id.tagNames);
            tagNames.setClickable(false);
            totalLike = (TextView) view.findViewById(R.id.totalLike);
            totalComment = (TextView) view.findViewById(R.id.totalComment);

            ll_bottomView = view.findViewById(R.id.ll_bottomView);  // Added by Pawan
            cardView = view.findViewById(R.id.cardView);  // Added by Pawan
            shareButtonView = view.findViewById(R.id.shareButtonView);
            likeButtonView = view.findViewById(R.id.likeButtonView);
            commentButtonView = view.findViewById(R.id.commentButtonView);
            gulakButtonView = view.findViewById(R.id.gulakButtonView);

            cardView.setBackgroundResource(R.drawable.card_view_border);
            shareButtonView.setOnClickListener(this);
            likeButtonView.setOnClickListener(this);
            commentButtonView.setOnClickListener(this);
            gulakButtonView.setOnClickListener(this);
            postImageView.setOnClickListener(this);
            showTagView.setOnClickListener(this);

            if(StaticClass.isBalance){
                ll_bottomView.setVisibility(View.GONE);
                messageWebView.setVisibility(View.GONE);
            }else {
                ll_bottomView.setVisibility(View.VISIBLE);
                messageWebView.setVisibility(View.VISIBLE);
            }

//            messageTextView.setOnClickListener(this);

            alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle(mContext.getResources().getString(R.string.tab_gulak));
            alertDialog.setMessage(mContext.getResources().getString(R.string.msg_savebank));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }

        @Override
        public void onClick(View v) {
//            if (v.getId() == messageTextView.getId()) {
//                if (!(list.get(getAdapterPosition()).getDescription()).equalsIgnoreCase(messageTextView.getText().toString())) {
//                    String description = list.get(getAdapterPosition()).getDescription();
//                    description = description.replaceAll("\n", "<br />");
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        messageTextView.setText(Html.fromHtml(description , Html.FROM_HTML_MODE_COMPACT));
//                    }
//                    else
//                    {
//                        messageTextView.setText(Html.fromHtml(description ));
//                    }
//                }
//                return;
//            }
            if (v.getId() == gulakButtonView.getId()) {
                if (!list.get(getAdapterPosition()).isSaved()) {
                    alertDialog.show();
                }
            }
            listener.onClickRecycleView(v, getAdapterPosition(), ViewHolder.this);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface Listener {
        public void onClickRecycleView(View view, int position, ViewHolder holdar);
    }

    private void addDotView(int position, LinearLayout linearLayout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ImageView dot = new ImageView(mContext);
        if (position == 0) {
            dot.setImageDrawable(mContext.getResources().getDrawable(R.drawable.paging_active_bubble));
            params.setMargins(0, 8, 0, 0);
        } else {
            dot.setImageDrawable(mContext.getResources().getDrawable(R.drawable.paging_inactive_bubble));
            params.setMargins(8, 8, 0, 0);
        }
        linearLayout.addView(dot, params);
    }
}
