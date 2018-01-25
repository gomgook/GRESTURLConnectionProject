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

public class NBaseResultAdapter extends RecyclerView.Adapter<NBaseResultAdapter.ViewHolder> {
    private static int VIEW_TYPE_ROW = 1;
    private static int VIEW_TYPE_LOADING_FOOTER = 2;

    private ArrayList<Item> mData = null;

    private String mSearchKeyword = null;

    private boolean mIsLastPageLoaded = false;

    public void setData(ArrayList<Item> data) {
        mData = data;
        notifyDataSetChanged();
    }

    void setLastPageLoaded(boolean isLastPageLoaded) {
        mIsLastPageLoaded = isLastPageLoaded;
    }

    public ArrayList<Item> getData() {
        return mData;
    }

    void setSearchKeyword(String searchKeyword) {
        mSearchKeyword = searchKeyword;
    }

    @Override
    public NBaseResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_LOADING_FOOTER) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.view_listview_footer, parent, false);

            return new FooterViewHolder(view);
        } else {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.view_listview_result_cell, parent, false);

            return new ResultViewHolder(view, parent);
        }
    }

    @Override
    public void onBindViewHolder(NBaseResultAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ROW) {
            ResultViewHolder resultViewHolder = (ResultViewHolder) holder;

            Item item = mData.get(position);

            if (item != null) {
                if (item.getTitle() != null) {
                    String htmlStr = item.getTitle();
                    htmlStr = htmlStr.replace(
                            mSearchKeyword,
                            "<font color=\"" + GUtil.getColor(resultViewHolder.parentViewGroup.getContext(),
                                    R.color.view_listview_cell_title_highlight) + "\">" + mSearchKeyword + "</font>");

                    resultViewHolder.title_text.setText(Html.fromHtml(htmlStr));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;

        if (mIsLastPageLoaded) return mData.size();

        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData != null && position == mData.size()) {
            return VIEW_TYPE_LOADING_FOOTER;
        }

        return VIEW_TYPE_ROW;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ResultViewHolder extends ViewHolder {
        ViewGroup parentViewGroup = null;
        TextView title_text;

        ResultViewHolder(View itemView, ViewGroup parent) {
            super(itemView);

            parentViewGroup = parent;
            title_text = itemView.findViewById(R.id.title_text);
        }
    }

    public class FooterViewHolder extends ViewHolder {
        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
