package com.stewhouse.nproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gomguk on 16. 7. 13..
 */
public class NBaseSearchAdapter extends RecyclerView.Adapter<NBaseSearchAdapter.SearchViewHolder> {

    private Context mContext = null;
    private ArrayList<String> mData = null;

    public NBaseSearchAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listview_keyword_cell, parent, false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        final String keyword = mData.get(position);

        if (keyword != null) {
            holder.title_text.setText(keyword);
        }

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NSQLiteOpenHelper nsqLiteOpenHelper = NSQLiteOpenHelper.getInstance(mContext);

                nsqLiteOpenHelper.deleteKeyword(nsqLiteOpenHelper.getWritableDatabase(), keyword);
                setData(nsqLiteOpenHelper.getKeywords(nsqLiteOpenHelper.getReadableDatabase()));
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView title_text = null;
        ImageView btn_delete = null;

        public SearchViewHolder(View itemView) {
            super(itemView);

            title_text = itemView.findViewById(R.id.title_text);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
