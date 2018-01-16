package com.stewhouse.nproject;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stewhouse.nproject.model.Item;
import com.stewhouse.nproject.util.GUtil;

import java.util.ArrayList;

/**
 * Created by Alphys on 2018. 1. 16..
 */

public class NewNBaseResultAdapter extends RecyclerView.Adapter<NewNBaseResultAdapter.ResultViewHolder> {
    private ArrayList<Item> mData = null;

    private String mSearchKeyword = null;

    public void setData(ArrayList<Item> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public ArrayList<Item> getData() {
        return mData;
    }

    public void setSearchKeyword(String searchKeyword) {
        mSearchKeyword = searchKeyword;
    }

    @Override
    public NewNBaseResultAdapter.ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_listview_result_cell, parent, false);

        return new ResultViewHolder(view, parent);
    }

    @Override
    public void onBindViewHolder(NewNBaseResultAdapter.ResultViewHolder holder, int position) {
        Item item = mData.get(position);

        if (item != null) {
            if (item.getTitle() != null) {
                String htmlStr = item.getTitle();
                htmlStr = htmlStr.replace(
                        mSearchKeyword,
                        "<font color=\"" + GUtil.getColor(holder.parentViewGroup.getContext(),
                                R.color.view_listview_cell_title_highlight) + "\">" + mSearchKeyword + "</font>");

                holder.title_text.setText(Html.fromHtml(htmlStr));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;

        return mData.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        ViewGroup parentViewGroup = null;
        TextView title_text;

        public ResultViewHolder(View itemView, ViewGroup parent) {
            super(itemView);

            parentViewGroup = parent;
            title_text = itemView.findViewById(R.id.title_text);
        }
    }
}
