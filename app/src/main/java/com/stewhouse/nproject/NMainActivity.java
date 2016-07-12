package com.stewhouse.nproject;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.stewhouse.gresturlconnection.GRESTURLConnection;
import com.stewhouse.nproject.model.Channel;
import com.stewhouse.nproject.model.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NMainActivity extends AppCompatActivity implements GRESTURLConnection.GRESTURLConnectionListener, AbsListView.OnScrollListener {

    private GSwipeRefreshLayout mSwipeRefreshLayout = null;
    private ListView mListView = null;
    private NBaseAdapter mListAdapter = null;
    private View mFooterLoadingView = null;

    private int mPage = -1;
    private int mTotalCount = -1;
    private boolean mCanLoadExtra = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (GSwipeRefreshLayout) findViewById(R.id.layout_swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPage = 1;
                loadData();
            }
        });
        mSwipeRefreshLayout.setListView((ListView) findViewById(R.id.view_list));
        mSwipeRefreshLayout.setAdapter(new NBaseAdapter(this));
        mListView = mSwipeRefreshLayout.getListView();
        mListView.setOnScrollListener(this);
        mListAdapter = mSwipeRefreshLayout.getAdapter();
        mFooterLoadingView = getLayoutInflater().inflate(R.layout.view_listview_footer, null);
        mPage = 1;

        loadData();
    }

    private void loadData() {
        addLoadingFooter();

        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);
        HashMap<String, String> params = new HashMap<>();

        params.put("apikey", NConstants.API_KEY);
        params.put("q", "위인");
        params.put("output", "json");
        params.put("pageno", String.valueOf(mPage));
        params.put("result", String.valueOf(NConstants.LIST_EXTRA_LOADING_PRE_COUNT));
        connection.execute("https://apis.daum.net/search/book", params, 3000, GRESTURLConnection.RequestType.GET, null, null, null);
    }

    @Override
    public void onPostExecute(Object result) {
        removeLoadingFooter();

        try {
            if (result != null) {
                if (result instanceof String) {
                    JSONObject jsonObject = new JSONObject(result.toString());

                    if (jsonObject != null) {
                        if (jsonObject.has(Channel.JSON_PARAM_ROOT) == true) {
                            Channel channel = Channel.parseJSONObject(jsonObject.getJSONObject(Channel.JSON_PARAM_ROOT));

                            if (channel != null) {
                                if (channel.getTotalCount() > -1) {
                                    mTotalCount = channel.getTotalCount();

                                    if (channel.getItems() != null) {
                                        ArrayList<Item> data = channel.getItems();

                                        setListView(data, mPage > 1);
                                        checkCanLoadExtra();
                                    }
                                }
                            }
                        }
                    }
                } else {

                    // TODO: Process each Exception cases.
                }
            } else {

                // TODO: Process error case.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListView(ArrayList<Item> data, boolean isDataAdd) {
        ArrayList<Item> listData;

        if (isDataAdd == true) {
            listData = mListAdapter.getData();
            listData.addAll(data);
        } else {
            listData = data;
        }
        mListAdapter.setData(listData);
        mListView.setAdapter(mListAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void checkCanLoadExtra() {
        if (NConstants.LIST_EXTRA_LOADING_PRE_COUNT * mPage < mTotalCount && mPage < NConstants.API_PAGE_LIMIT) {
            mCanLoadExtra = true;
        } else {
            mCanLoadExtra = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mListAdapter != null) {
            if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                mListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount > totalItemCount - NConstants.LIST_EXTRA_LOADING_PRE_COUNT) {
            if (mCanLoadExtra == true) {
                mCanLoadExtra = false;
                mPage++;

                loadData();
            }
        }
    }

    private void addLoadingFooter() {
        removeLoadingFooter();
        mListView.addFooterView(mFooterLoadingView);
    }

    private void removeLoadingFooter() {
        while (mListView.getFooterViewsCount() != 0) {
            mListView.removeFooterView(mFooterLoadingView);
        }
    }
}