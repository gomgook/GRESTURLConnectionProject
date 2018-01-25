package com.stewhouse.nproject.util;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.stewhouse.nproject.NBaseResultAdapter;

/**
 * Created by Gomguk on 2016-07-11.
 */
public class GSwipeRefreshLayout extends SwipeRefreshLayout {
    private RecyclerView mListView = null;
    private NBaseResultAdapter mListAdapter = null;

    public GSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerView getListView() {
        return mListView;
    }

    public void setListView(RecyclerView listView) {
        mListView = listView;
    }

    public void setAdapter(NBaseResultAdapter adapter) {
        mListAdapter = adapter;
        mListView.setAdapter(mListAdapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mListView.setLayoutManager(layoutManager);
    }
}
