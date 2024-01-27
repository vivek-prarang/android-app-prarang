package com.riversanskiriti.prarang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.riversanskiriti.prarang.R;

import java.util.List;

/**
 * Created by ARPIT on 07-03-2017.
 */

public class LangAdapter extends RecyclerView.Adapter<LangAdapter.MyViewHolder> {

    Context context;
    List<LangItem> list;
    Listener listener;

    public LangAdapter(Context context, List<LangItem> list, LangAdapter.Listener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_language, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        if (list.get(position).getSubTitle() == null) {
            holder.subTitle.setVisibility(View.GONE);
        } else {
            holder.subTitle.setText(list.get(position).getSubTitle());
        }

        if (list.get(position).isChecked()) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, subTitle;
        ImageView check;
        View seprator, rowView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subTitle);
            check = (ImageView) itemView.findViewById(R.id.check);
            seprator = itemView.findViewById(R.id.seprator);
            rowView = itemView.findViewById(R.id.rowView);
            rowView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClickLanguageView(v, getAdapterPosition());
        }
    }

    public interface Listener {
        public void onClickLanguageView(View v, int postion);
    }
}
