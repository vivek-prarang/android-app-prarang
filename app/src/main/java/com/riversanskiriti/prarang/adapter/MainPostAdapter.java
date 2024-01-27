package com.riversanskiriti.prarang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.riversanskiriti.prarang.R;

import java.util.List;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class MainPostAdapter extends RecyclerView.Adapter<MainPostAdapter.MyViewHolder> {

    private List<MainPost> list;
    private Context context;
    private PostItemAdapter.Listner listner;

    public MainPostAdapter(Context context, List<MainPost> list, PostItemAdapter.Listner listner) {
        this.context = context;
        this.list = list;
        this.listner = listner;
    }

    @Override
    public MainPostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_post, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (list.get(position).isLoading()) {
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }
        holder.catagoryName.setText(list.get(position).getName());
        if (list.get(position).getList() == null && list.get(position).getMessage() != null) {
            holder.noRecord.setText(list.get(position).getMessage());
            holder.noRecord.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.GONE);
        } else {
            if (list.get(position).getList().size() == 0) {
                holder.noRecord.setText(list.get(position).getMessage());
                holder.noRecord.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
            } else {
                holder.noRecord.setVisibility(View.GONE);
                holder.adapter = new PostItemAdapter(context, list.get(position).getList(), position, listner);
                holder.recyclerView.setAdapter(holder.adapter);
                holder.adapter.notifyDataSetChanged();
                holder.recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView catagoryName, noRecord;
        private RecyclerView recyclerView;
        private PostItemAdapter adapter;
        private ProgressBar progressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            noRecord = (TextView) itemView.findViewById(R.id.noRecord);
            catagoryName = (TextView) itemView.findViewById(R.id.catagoryName);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.hRecyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(list.size());
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

//            EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//                @Override
//                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                    Log.e("TAG", "" + page);
//                }
//            };
//            recyclerView.addOnScrollListener(scrollListener);
        }
    }
}
