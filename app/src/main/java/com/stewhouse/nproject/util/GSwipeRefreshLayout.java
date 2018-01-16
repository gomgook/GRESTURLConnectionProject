package com.stewhouse.nproject.util;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.stewhouse.nproject.NBaseResultAdapter;
import com.stewhouse.nproject.NewNBaseResultAdapter;

/**
 * Created by Gomguk on 2016-07-11.
 */
public class GSwipeRefreshLayout extends SwipeRefreshLayout {
    private RecyclerView mListView = null;
    private NewNBaseResultAdapter mListAdapter = null;

    private View mFooterLoadingView = null;

    public GSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerView getListView() {
        return mListView;
    }

    public void setListView(RecyclerView listView) {
        mListView = listView;
    }

    public NewNBaseResultAdapter getAdapter() {
        return mListAdapter;
    }

    public void setAdapter(NewNBaseResultAdapter adapter) {
        mListAdapter = adapter;
        mListView.setAdapter(mListAdapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mListView.setLayoutManager(layoutManager);
    }

    public void setFooterLoadingView(View footerLoadingView) {
        mFooterLoadingView = footerLoadingView;
    }

    public void addLoadingFooter() {
        // TODO
//        if (mListView != null && mFooterLoadingView != null) {
//            removeLoadingFooter();
//            mListView.addFooterView(mFooterLoadingView);
//        }
    }

    public void removeLoadingFooter() {
        // TODO
//        if (mListView != null && mFooterLoadingView != null) {
//            while (mListView.getFooterViewsCount() != 0) {
//                mListView.removeFooterView(mFooterLoadingView);
//            }
//        }
    }
}
