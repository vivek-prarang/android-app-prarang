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

import com.riversanskiriti.prarang.R;
import com.riversanskiriti.utils.BaseUtils;

import java.util.List;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class PrarangAdapter extends RecyclerView.Adapter<PrarangAdapter.MyViewHolder> {

    private List<PrarangItem> list;
    private Context context;
    private Listner listner;
    private BaseUtils baseUtils;

    public PrarangAdapter(Context context, List<PrarangItem> list, PrarangAdapter.Listner listner) {
        this.context = context;
        this.list = list;
        this.listner = listner;
        baseUtils = new BaseUtils(context);
    }

    @Override
    public PrarangAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_prarang, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PrarangAdapter.MyViewHolder holder, int position) {

        holder.title.setText(list.get(position).getTitle());
        holder.count.setText(list.get(position).getCount());
        holder.icon.setImageResource(list.get(position).getIcon());
        GradientDrawable gd = (GradientDrawable) holder.iconFrame.getBackground().getCurrent();
        gd.setStroke(baseUtils.dpToPx(4), list.get(position).getFrameColor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title, count;
        private FrameLayout iconFrame;
        private ImageView icon;
        private View cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            count = (TextView) itemView.findViewById(R.id.count);
            icon = (ImageView) itemView.findViewById(R.id.rowIcon);
            iconFrame = (FrameLayout) itemView.findViewById(R.id.iconFrame);

            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listner != null) {
                listner.onClickPrarangItem(getAdapterPosition());
            }
        }
    }

    public interface Listner {
        public void onClickPrarangItem(int position);
    }
}
