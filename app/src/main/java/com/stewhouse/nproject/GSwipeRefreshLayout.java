package com.stewhouse.nproject;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Gomguk on 2016-07-11.
 */
public class GSwipeRefreshLayout extends SwipeRefreshLayout {

    private ListView mListView = null;
    private NBaseAdapter mListAdapter = null;

    public GSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListView getListView() {
        return mListView;
    }

    public void setListView(ListView listView) {
        mListView = listView;
    }

    public NBaseAdapter getAdapter() {
        return mListAdapter;
    }

    public void setAdapter(NBaseAdapter adapter) {
        mListAdapter = adapter;
    }
}
